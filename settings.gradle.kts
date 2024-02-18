pluginManagement {
    repositories {
        mavenLocal()

        maven(url = "https://mirrors.huaweicloud.com/repository/maven/")
        maven(url = "https://maven.aliyun.com/repository/public/")
        maven(url = "https://maven.aliyun.com/repository/spring")

        mavenCentral()
    }
}

dependencyResolutionManagement {
    val mybatisVersion = "3.5.15"
    val mybatisPlusVersion = "3.5.5"
    val jSqlParserVersion = "4.8"
    val grpcVersion = "1.61.0"

    versionCatalogs {
        create("libs") {
            version("springBootVersion", "3.2.1")
            version("formatterVersion", "0.0.41")
            version("mapstructVersion", "1.5.3.Final")
            version("lombokVersion", "1.18.30")
            version("lombokMapstructBindingVersion", "0.2.0")

            library("springBootClasspath", "org.springframework.boot", "spring-boot-gradle-plugin").versionRef("springBootVersion")
            library("springFormatterClasspath", "io.spring.javaformat", "spring-javaformat-gradle-plugin").versionRef("formatterVersion")

            library("springBootDependencies", "org.springframework.boot", "spring-boot-dependencies").versionRef("springBootVersion")
            library("grpcDependencies", "io.grpc", "grpc-bom").version(grpcVersion)

            library("mapstruct", "org.mapstruct", "mapstruct").versionRef("mapstructVersion")
            library("lombok", "org.projectlombok", "lombok").versionRef("lombokVersion")

            library("mapstructProcessor", "org.mapstruct", "mapstruct-processor").versionRef("mapstructVersion")
            library("lombokMapstruct", "org.projectlombok", "lombok-mapstruct-binding").versionRef("lombokMapstructBindingVersion")

            library("mybatis", "org.mybatis", "mybatis").version(mybatisVersion)
            library("mybatisPlusAnnotation", "com.baomidou", "mybatis-plus-annotation").version(mybatisPlusVersion)
            library("mybatisPlusExtension", "com.baomidou", "mybatis-plus-extension").version(mybatisPlusVersion)
            library("mybatisPlusBootStarter", "com.baomidou", "mybatis-plus-boot-starter").version(mybatisPlusVersion)
            library("mybatisPlusCore", "com.baomidou", "mybatis-plus-core").version(mybatisPlusVersion)
            library("jSqlParser", "com.github.jsqlparser", "jsqlparser").version(jSqlParserVersion)

            bundle("mybatisPlus", listOf("mybatis", "mybatisPlusAnnotation", "mybatisPlusExtension", "mybatisPlusCore"))

            val commonsNetVersion = "3.10.0"
            library("commonsNet", "commons-net", "commons-net").version(commonsNetVersion)

            val securityProtobufVersion = "17_2023.12.18-SNAPSHOT"
            library("securityProtobuf", "live.lingting.protobuf", "protobuf-java").version(securityProtobufVersion)

            bundle("implementation", listOf("mapstruct"));
            bundle("compile", listOf("lombok"))
            bundle("annotation", listOf("mapstructProcessor", "lombok", "lombokMapstruct"))
        }
    }

}


rootProject.name = "lingting-framework"
include("lingting-core")
include("lingting-jackson")
include("lingting-okhttp")
include("lingting-ntp")
include("lingting-dingtalk")
include("lingting-datascope")
include("lingting-grpc")
include("lingting-security")
include("lingting-security-grpc")
include("lingting-dependencies")
include("lingting-mybatis")
