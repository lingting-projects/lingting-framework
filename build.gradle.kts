import java.nio.file.Paths

var javaProjects = subprojects.filter { it.name.startsWith("lingting-") }
var javaVersion = JavaVersion.VERSION_17
var encoding = "UTF-8"

plugins {
    id("idea")
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

    pluginManager.withPlugin("idea") {
        idea {
            module {
                val path = Paths.get(project.layout.buildDirectory.get().toString(), "generated", "sources", "annotationProcessor", "java", "main")
                excludeDirs.add(path.toFile())
            }
        }
    }
}

configure(javaProjects) {
    println(project.displayName)
    apply {
        plugin("java")
    }

    dependencies {
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

}


