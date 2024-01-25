pluginManagement {
    repositories {
        mavenLocal()

        maven(url = "https://mirrors.huaweicloud.com/repository/maven/")
        maven(url = "https://maven.aliyun.com/repository/public/")
        maven(url = "https://maven.aliyun.com/repository/spring")

        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    // 编写版本目录的依赖库
    versionCatalogs {
        create("libs") {
//            val slf4jVersion = "1.7.36"
//            val mapstructVersion = "1.5.3.Final"
//            val lombokVersion = "1.18.24"
//            val lombokMapstructBindingVersion = "0.2.0"
//
//            library("slf4j", "org.slf4j", "slf4j-api").version(slf4jVersion)
//            library("mapstruct", "org.mapstruct", "mapstruct").version(mapstructVersion)
//            library("mapstruct-processor", "org.mapstruct", "mapstruct-processor").version(mapstructVersion)
//            library("lombok", "org.projectlombok", "lombok").version(lombokVersion)
//            library("lombok_mapstruct", "org.projectlombok", "lombok-mapstruct-binding").version(lombokMapstructBindingVersion)
//
//            bundle("globalCompileOnly", listOf("mapstruct", "lombok"))
//            bundle("annotationProcessor", listOf("mapstruct-processor", "lombok", "lombok_mapstruct"))
        }
    }

}


rootProject.name = "lingting-framework"
include("lingting-core")
