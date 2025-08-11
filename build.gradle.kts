import com.vanniktech.maven.publish.SonatypeHost
import groovy.namespace.QName
import groovy.util.Node
import groovy.util.NodeList
import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

val projectGroup = "live.lingting.framework"
val projectVersion = "2025.05.23-Beta-9"

// 用于子模块获取包管理信息
val catalogLibs = libs
// 用于声明依赖的项目
val dependencyProjects = subprojects.filter { it.name.endsWith("dependencies") }
// java 项目
val javaProjects = subprojects.filter { it.name.startsWith("lingting-") && !dependencyProjects.contains(it) }
// 使用的java版本
val javaVersion = JavaVersion.VERSION_21
// 字符集
val encoding = "UTF-8"
val ideaLanguageLevel = IdeaLanguageLevel(javaVersion)

plugins {
    id("idea")
    id("signing")
    alias(libs.plugins.publish)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.ksp)
}

idea {
    project {
        languageLevel = ideaLanguageLevel
        targetBytecodeVersion = javaVersion
    }
}

allprojects {
    group = projectGroup
    version = projectVersion
}

configure(javaProjects + dependencyProjects) {

    apply {
        plugin("idea")
    }

    idea {
        module {
            languageLevel = ideaLanguageLevel
            targetBytecodeVersion = javaVersion
        }
    }

}

configure(dependencyProjects) {

    apply {
        plugin("java-platform")
    }

}

configure(javaProjects) {

    apply {
        plugin(catalogLibs.plugins.kotlin.jvm.get().pluginId)
        plugin(catalogLibs.plugins.kotlin.ksp.get().pluginId)
    }

    dependencies {
        catalogLibs.bundles.dependencies.get().forEach {
            implementation(platform(it))
        }
        implementation(catalogLibs.bundles.implementation)

        annotationProcessor(catalogLibs.bundles.annotation)
        ksp(catalogLibs.bundles.ksp)
        compileOnly(catalogLibs.bundles.compile)
        testImplementation(catalogLibs.bundles.test)
    }

    // 这样子Java代码直接卸载kotlin里面就可以被访问了
    sourceSets {
        main { java { srcDir("src/main/kotlin") } }
        test { java { srcDir("src/test/kotlin") } }
    }

    configure<KotlinJvmProjectExtension> {
        jvmToolchain(javaVersion.majorVersion.toInt())
        compilerOptions {
            freeCompilerArgs.addAll(
                "-Xjvm-default=all",
                "-Xjsr305=strict",
            )
        }
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    tasks.withType<Test> {
        enabled = gradle.startParameter.taskNames.contains("test")
        useJUnitPlatform()
        testLogging {
            showStandardStreams = true
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = encoding
        options.compilerArgs.add("-parameters")
        options.setIncremental(true)
    }

    tasks.withType<Javadoc> {
        isFailOnError = false
        options.encoding(encoding)
    }
}

configure(javaProjects + dependencyProjects) {

    apply {
        plugin("signing")
        plugin(catalogLibs.plugins.publish.get().pluginId)
    }

    tasks.withType<GenerateModuleMetadata> {
        enabled = false
    }

    class Dependency(val group: String, val module: String, val v: String?) {

        // 从上下文中解析出具体的依赖版本
        fun resolveVersion(project: Project): String? {
            val resolvedDeps = project.configurations.getByName("compileClasspath")
                .resolvedConfiguration.firstLevelModuleDependencies

            val match = resolvedDeps.firstOrNull { it.moduleGroup == group && it.moduleName == module }
            return match?.moduleVersion
        }

    }

    fun toDependency(node: Node): Dependency {
        val nodes = node.value() as NodeList
        var group: String? = null
        var module: String? = null
        var version: String? = null

        nodes.mapNotNull { it as? Node }.forEach {
            val name = it.name() as QName
            when (name.localPart) {
                "groupId" -> group = it.value() as String
                "artifactId" -> module = it.value() as String
                "version" -> version = it.value() as String
            }
        }

        return Dependency(group!!, module!!, version)
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

            withXml {
                val node = asNode()
                val nodes = node.get("dependencies") as NodeList
                if (nodes.isNotEmpty()) {
                    val managements = node.get("dependencyManagement") as NodeList
                    if (managements.isNotEmpty()) {
                        node.remove(managements[0] as Node)
                    }
                }
                nodes.mapNotNull { it as? Node }.forEach { dns ->
                    val dependencies = dns.value() as NodeList
                    dependencies.mapNotNull { it as? Node }.forEach { dn ->
                        val dependency = toDependency(dn)
                        if (dependency.v.isNullOrBlank()) {
                            val version = dependency.resolveVersion(project)
                            if (version.isNullOrBlank()) {
                                println("无法解析具体版本号: ${dependency.group}:${dependency.module}")
                            } else {
                                dn.appendNode("version", version)
                            }
                        }
                    }
                }
            }

        }

    }
}
