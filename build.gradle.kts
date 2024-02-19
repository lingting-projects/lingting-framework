import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel
import java.net.URI
import java.nio.file.Paths

val projectGroup = "live.lingting.framework"
val projectVersion = "2024.01.24-SNAPSHOT"

// 是否为测试版本
val isSnapshot = projectVersion.endsWith("SNAPSHOT")
// 用于子模块获取包管理信息
val catalogLibs = libs
// 用于声明依赖的项目
val dependencyProjects = subprojects.filter { it.name.endsWith("dependencies") }
// java 项目
val javaProjects = subprojects.filter { it.name.startsWith("lingting-") && !dependencyProjects.contains(it) }
// 使用的java版本
val javaVersion = JavaVersion.VERSION_17
// 字符集
val encoding = "UTF-8"
val ideaLanguageLevel = IdeaLanguageLevel(javaVersion);
// 仅打包jar, 不打包源文件jar和文档jar
val onlyJar = isSnapshot

plugins {
    id("idea")
    id("java")
    id("maven-publish")
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
        classpath(libs.springFormatterClasspath)
    }
}

allprojects {
    group = projectGroup
    version = projectVersion

    val isJava = javaProjects.contains(project)
    val isDependency = dependencyProjects.contains(project)

    apply {
        plugin("idea")
        plugin("maven-publish")
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

    repositories {
        mavenLocal()

        maven(url = "https://mirrors.huaweicloud.com/repository/maven/")
        maven(url = "https://maven.aliyun.com/repository/public/")
        maven(url = "https://maven.aliyun.com/repository/spring")

        mavenCentral()
    }
    buildscript {
        repositories {
            mavenLocal()

            maven(url = "https://mirrors.huaweicloud.com/repository/maven/")
            maven(url = "https://maven.aliyun.com/repository/public/")
            maven(url = "https://maven.aliyun.com/repository/spring")

            mavenCentral()
        }
    }

    idea {
        module {
            val rootPath = Paths.get(project.layout.buildDirectory.get().toString(), "generated", "sources", "annotationProcessor", "java").toString();
            val mainPath = Paths.get(rootPath, "main")
            val testPath = Paths.get(rootPath, "test")
            excludeDirs.add(mainPath.toFile())
            excludeDirs.add(testPath.toFile())
            languageLevel = ideaLanguageLevel
            targetBytecodeVersion = javaVersion

            excludeDirs.add(File(rootDir, "src"))
            excludeDirs.add(File(rootDir, "src/main"))
            excludeDirs.add(File(rootDir, "src/main/java"))
            excludeDirs.add(File(rootDir, "src/main/resources"))
            excludeDirs.add(File(rootDir, "src"))
            excludeDirs.add(File(rootDir, "src/test"))
            excludeDirs.add(File(rootDir, "src/test/java"))
            excludeDirs.add(File(rootDir, "src/test/resources"))
        }
    }

    publishing {

        publications {
            create<MavenPublication>("mavenJava") {

                if (isJava) {
                    from(components["java"])

                    if (!onlyJar) {
                        java {
                            withJavadocJar()
                            withSourcesJar()
                        }
                    }
                } else if (isDependency) {
                    from(components["javaPlatform"])
                }

                pom {
                    name = project.name
                    description = project.description

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

                    val repository = "lingting-projects/lingting-framework"
                    scm {
                        connection = "scm:git:git@github.com:$repository.git"
                        developerConnection = "scm:git:git@github.com:$repository.git"
                        url = "https://github.com/$repository"
                        tag = "HEAD"
                    }
                }

            }
        }

        repositories {
            mavenCentral() {
                if (isSnapshot) {
                    url = URI.create("https://oss.sonatype.org/content/repositories/snapshots/")
                }

                credentials {
                    username = "${findProperty("nexus.username")}"
                    password = "${findProperty("nexus.password")}"
                }
            }
        }

        signing {
            /**
             * <h1> 需要在 GRADLE_HOME 目录下的 gradle.properties(没有则新建) 配置以下属性</h1>
             *  <p> signing.keyId : kleopatra 查看整数密钥ID, 取后8位</p>
             *  <p> signing.password : 证书的密码</p>
             *  <p> signing.secretKeyRingFile : kleopatra 右键证书 -> Backup Secret Keys -> 保存文件 -> 修改文件后缀为 gpg -> 填写完整的文件地址(eg: C:/Users/lingting/.gradle/lingting.gpg)</p>
             */
            sign(publishing.publications["mavenJava"])
        }
    }
}

configure(javaProjects) {
    apply {
        plugin("io.spring.javaformat")
    }

    dependencies {
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
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    tasks.withType<Test> {
        useJUnitPlatform()
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
