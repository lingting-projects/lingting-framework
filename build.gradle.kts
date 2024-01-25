var javaProjects = subprojects.filter { it.name.startsWith("lingting-") }
var javaVersion = JavaVersion.VERSION_17
var encoding = "UTF-8"

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

}

configure(javaProjects) {
    print(project.displayName)
    apply {
        plugin("java")
    }

    dependencies {
        val springBootVersion = "3.2.1";
        val mapstructVersion = "1.5.3.Final"
        val lombokVersion = "1.18.24"
        val lombokMapstructBindingVersion = "0.2.0"
        val awaitilityVersion = "4.2.0"
        val junitVersion = "5.9.1"

        add("implementation", platform("org.springframework.boot:spring-boot-dependencies:${springBootVersion}"))
        add("implementation", "org.slf4j:slf4j-api")

        add("compileOnly", "org.mapstruct:mapstruct:${mapstructVersion}")
        add("compileOnly", "org.projectlombok:lombok:${lombokVersion}")

        add("annotationProcessor", "org.projectlombok:lombok:${lombokVersion}")
        add("annotationProcessor", "org.mapstruct:mapstruct-processor:${mapstructVersion}")
        add("annotationProcessor", "org.projectlombok:lombok-mapstruct-binding:${lombokMapstructBindingVersion}")

        add("testImplementation", "org.awaitility:awaitility:${awaitilityVersion}")
        add("testImplementation", platform("org.junit:junit-bom:${junitVersion}"))
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

