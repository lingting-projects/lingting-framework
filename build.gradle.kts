var javaProjects = subprojects.filter { it.name.startsWith("lingting-") }

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
        add("testImplementation", platform("org.junit:junit-bom:5.9.1"))
        add("testImplementation", "org.junit.jupiter:junit-jupiter")
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

