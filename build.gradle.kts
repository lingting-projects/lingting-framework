import java.nio.file.Paths

val catalogLibs = libs
val javaProjects = subprojects.filter { it.name.startsWith("lingting-") && !it.name.endsWith("dependencies") }
val javaVersion = JavaVersion.VERSION_17
val encoding = "UTF-8"
val formatterVersion = "0.0.41"

plugins {
    id("idea")
    id("checkstyle")
    alias(libs.plugins.springFormat)
}

allprojects {
    group = "live.lingting.framework"
    version = "2024.01.24-SNAPSHOT"

    apply {
        plugin("idea")
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

    pluginManager.withPlugin("idea") {
        idea {
            module {
                val rootPath = Paths.get(project.layout.buildDirectory.get().toString(), "generated", "sources", "annotationProcessor", "java").toString();
                val mainPath = Paths.get(rootPath, "main")
                val testPath = Paths.get(rootPath, "test")
                excludeDirs.add(mainPath.toFile())
                excludeDirs.add(testPath.toFile())
            }
        }
    }
}

configure(javaProjects) {
    apply {
        plugin("java")
        plugin("java-library")
        plugin(catalogLibs.plugins.springFormat.get().pluginId)
    }

    dependencies {
        add("implementation", platform(catalogLibs.springBootDependencies))
        add("implementation", platform(catalogLibs.grpcDependencies))

        add("implementation", "org.slf4j:slf4j-api")

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
    }

}
