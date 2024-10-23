import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel

val projectGroup = "live.lingting.framework"
val projectVersion = "2024.01.24-Bata-7"

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
val ideaLanguageLevel = IdeaLanguageLevel(javaVersion);

plugins {
    id("idea")
    id("java")
    id("com.vanniktech.maven.publish") version "0.29.0"
    id("signing")
}

idea {
    project {
        languageLevel = ideaLanguageLevel
        targetBytecodeVersion = javaVersion
    }
}

buildscript {
    dependencies {
        classpath(libs.springFormatterPlugin)
        classpath(libs.grpcProtobufPlugin)
    }
}

allprojects {
    group = projectGroup
    version = projectVersion

    val isJava = javaProjects.contains(project)
    val isDependency = dependencyProjects.contains(project)

    apply {
        plugin("idea")
        plugin("com.vanniktech.maven.publish")
        plugin("signing")
    }

    if (isJava) {
        apply {
            plugin("java")
            plugin("java-library")
        }
    }

    if (isDependency) {
        apply {
            plugin("java-platform")
        }
    }

    idea {
        module {
            languageLevel = ideaLanguageLevel
            targetBytecodeVersion = javaVersion

            excludeDirs.add(File(rootDir, "src"))
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
        }

    }

}

configure(javaProjects) {
    apply {
        plugin("io.spring.javaformat")
        plugin("checkstyle")
    }

    dependencies {
        add("checkstyle", catalogLibs.springFormatterCheckstyle)

        add("implementation", platform(catalogLibs.springBootDependencies))
        add("implementation", platform(catalogLibs.grpcDependencies))

        add("implementation", "org.slf4j:slf4j-api")

        add("implementation", catalogLibs.bundles.implementation)
        add("compileOnly", catalogLibs.bundles.compile)
        add("testCompileOnly", catalogLibs.bundles.compile)
        add("annotationProcessor", catalogLibs.bundles.annotation)
        add("testAnnotationProcessor", catalogLibs.bundles.annotation)

        add("testImplementation", "org.awaitility:awaitility")
        add("testImplementation", "org.junit.jupiter:junit-jupiter")
        add("testImplementation", "ch.qos.logback:logback-classic")
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            showStandardStreams = true
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = encoding
        options.compilerArgs.add("-parameters")
    }

    tasks.withType<Javadoc> {
        isFailOnError = false
        options.encoding(encoding)
    }
}
