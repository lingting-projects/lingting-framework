import java.nio.file.Paths

val javaProjects = subprojects.filter { it.name.startsWith("lingting-") }
val javaVersion = JavaVersion.VERSION_17
val encoding = "UTF-8"
val formatterVersion = "0.0.41"

plugins {
    id("idea")
    id("checkstyle")
    id("io.spring.javaformat").version(buildString { append("0.0.41") })
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
    println(project.displayName)

    apply {
        plugin("java")
        plugin("java-library")
        plugin("checkstyle")
        plugin("io.spring.javaformat")
    }

    buildscript {
        dependencies {
            classpath("io.spring.javaformat:spring-javaformat-gradle-plugin:${formatterVersion}")
        }
    }

    dependencies {
        checkstyle("io.spring.javaformat:spring-javaformat-checkstyle:${formatterVersion}")

        val springBootVersion = "3.2.1"
        val mapstructVersion = "1.5.3.Final"
        val lombokVersion = "1.18.30"
        val lombokMapstructBindingVersion = "0.2.0"

        add("implementation", platform("org.springframework.boot:spring-boot-dependencies:${springBootVersion}"))
        add("implementation", "org.slf4j:slf4j-api")

        val compileOnlyList = listOf("org.mapstruct:mapstruct:${mapstructVersion}", "org.projectlombok:lombok:${lombokVersion}")
        compileOnlyList.forEach {
            add("compileOnly", it)
            add("testCompileOnly", it)
        }

        val annotationProcessorList = listOf("org.projectlombok:lombok:${lombokVersion}", "org.mapstruct:mapstruct-processor:${mapstructVersion}", "org.projectlombok:lombok-mapstruct-binding:${lombokMapstructBindingVersion}")
        annotationProcessorList.forEach {
            add("annotationProcessor", it)
            add("testAnnotationProcessor", it)
        }

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

//    tasks.withType(io.spring.javaformat.gradle.tasks.CheckFormat) {
//        exclude "package/to/exclude"
//    }

}


