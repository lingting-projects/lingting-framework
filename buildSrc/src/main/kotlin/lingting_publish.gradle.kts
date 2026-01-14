import com.vanniktech.maven.publish.SonatypeHost
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.namespace.QName
import groovy.util.Node
import groovy.util.NodeList

plugins {
    id("signing")
    id("com.vanniktech.maven.publish")
}

val versionsProvider = provider {
    val versionMap = mutableMapOf<String, String>()

    // 获取所有相关的配置
    val allConfigs = configurations.filter { it.isCanBeResolved }

    // 优先级排序：先处理 Compile，再处理 Runtime
    // 这样如果同一个依赖在两个地方都有，Runtime 的版本会覆盖 Compile 的
    val compileConfigs = allConfigs.filter { it.name.endsWith("CompileClasspath", ignoreCase = true) }
    val runtimeConfigs = allConfigs.filter { it.name.endsWith("RuntimeClasspath", ignoreCase = true) }

    // 按照 [编译 -> 运行] 的顺序处理
    (compileConfigs + runtimeConfigs).forEach { conf ->
        try {
            conf.resolvedConfiguration.firstLevelModuleDependencies.forEach {
                // 放入 Map，如果 key 重复，后者 (Runtime) 会覆盖前者 (Compile)
                versionMap["${it.moduleGroup}:${it.moduleName}"] = it.moduleVersion
            }
        } catch (e: Exception) {
            logger.error("[发布] 解析配置异常!", e)
        }
    }
    versionMap
}

@Suppress("UNCHECKED_CAST")
tasks.withType<GenerateModuleMetadata> {
    // 在运行时获取引用
    val versionsProvider = versionsProvider
    val dependenciesProject = project.name.endsWith("-dependencies")

    doLast {
        val file = outputFile.get().asFile
        if (!file.exists()) {
            println("元数据文件不存在: ${file.absolutePath}")
            return@doLast
        }

        val versions = versionsProvider.orNull

        fun handleVersion(dep: MutableMap<String, Any>) {
            val group = dep["group"] as? String ?: ""
            val module = dep["module"] as? String ?: ""
            val versionObj = dep["version"] as? MutableMap<String, Any>

            // 如果没有 version 节点或 version 内容为空
            if (versionObj == null || versionObj.isEmpty()) {
                val version = versions?.get("$group:$module")
                if (!version.isNullOrBlank()) {
                    dep["version"] = mutableMapOf("requires" to version)
                } else {
                    println("Warning: [Metadata] 无法解析依赖版本: $group:$module")
                }
            }
        }

        val json = JsonSlurper().parse(file) as MutableMap<String, Any>
        val variants = json["variants"] as? List<MutableMap<String, Any>> ?: emptyList()
        for (variant in variants) {
            val dependencies = variant["dependencies"] as? MutableList<MutableMap<String, Any>>

            if (!dependenciesProject) {
                // 过滤掉所有 category 为 platform 的依赖 (BOM)
                dependencies?.removeIf { dep ->
                    val attributes = dep["attributes"] as? Map<*, *>
                    attributes?.get("org.gradle.category") == "platform"
                }
            }

            // 补全剩余依赖的版本号
            dependencies?.forEach { dep ->
                handleVersion(dep)
            }

            val dependencyConstraints = variant["dependencyConstraints"] as? MutableList<MutableMap<String, Any>>
            dependencyConstraints?.forEach { dep ->
                handleVersion(dep)
            }
        }

        // 写回文件
        file.writeText(JsonBuilder(json).toPrettyString())

    }
}

mavenPublishing {
    val projectRepository = "lingting-projects/lingting-framework"
    val projectUrl = "https://github.com/$projectRepository"

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()

    pom {
        name = project.name
        description = if (project.description.isNullOrBlank()) {
            project.name
        } else {
            project.description
        }
        url = projectUrl

        licenses {
            license {
                name = "MIT License"
                url = "https://www.opensource.org/licenses/mit-license.php"
                distribution = "repo"
            }
        }

        developers {
            developer {
                id = "lingting"
                name = id
                email = "sunlisten.gzm@gmail.com"
                url = "https://github.com/lingting"
            }
        }

        scm {
            connection = "scm:git:git@github.com:$projectRepository.git"
            developerConnection = "scm:git:git@github.com:$projectRepository.git"
            url = projectUrl
            tag = "HEAD"
        }
        // 在运行时获取引用
        val versionsProvider = versionsProvider

        withXml {
            val versions = versionsProvider.orNull
            val node = asNode()
            val nodes = node.get("dependencies") as NodeList
            if (nodes.isNotEmpty()) {
                // 移除 bom
                val managements = node.get("dependencyManagement") as NodeList
                if (managements.isNotEmpty()) {
                    node.remove(managements[0] as Node)
                }
            }
            nodes.mapNotNull { it as? Node }.forEach { dns ->
                val dependencies = dns.value() as NodeList
                dependencies.mapNotNull { it as? Node }.forEach { dn ->
                    val nodes = dn.value() as NodeList
                    var group: String? = null
                    var module: String? = null
                    var version: String? = null

                    nodes.mapNotNull { it as? Node }.forEach {
                        val name = it.name() as QName
                        when (name.localPart) {
                            "groupId" -> group = it.text()
                            "artifactId" -> module = it.text()
                            "version" -> version = it.text()
                        }
                    }
                    // 添加版本号
                    if (version.isNullOrBlank()) {
                        val version = versions?.get("$group:$module")
                        if (version.isNullOrBlank()) {
                            println("Warning: [POM] 无法解析依赖版本: ${group}:${module}")
                        } else {
                            dn.appendNode("version", version)
                        }
                    }
                }
            }
        }

    }

}
