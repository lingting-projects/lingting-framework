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
    val mybatisVersion = "3.5.14"
    val mybatisPlusVersion = "3.5.5"
    val jSqlParserVersion = "4.8"
    val grpcVersion = "1.61.0"
    val elasticVersion = "8.12.2"
    val commonsNetVersion = "3.10.0"
    val securityProtobufVersion = "17_2023.12.18-SNAPSHOT"

    versionCatalogs {
        create("libs") {
            version("springBoot", "3.2.1")
            version("formatter", "0.0.41")
            version("mapstruct", "1.5.3.Final")
            version("lombok", "1.18.30")
            version("lombokMapstructBinding", "0.2.0")
            version("jSqlParser", jSqlParserVersion)

            library("springBootClasspath", "org.springframework.boot", "spring-boot-gradle-plugin").versionRef("springBoot")
            library("springFormatterClasspath", "io.spring.javaformat", "spring-javaformat-gradle-plugin").versionRef("formatter")

            library("springBootDependencies", "org.springframework.boot", "spring-boot-dependencies").versionRef("springBoot")
            library("grpcDependencies", "io.grpc", "grpc-bom").version(grpcVersion)

            library("mapstruct", "org.mapstruct", "mapstruct").versionRef("mapstruct")
            library("lombok", "org.projectlombok", "lombok").versionRef("lombok")

            library("mapstructProcessor", "org.mapstruct", "mapstruct-processor").versionRef("mapstruct")
            library("lombokMapstruct", "org.projectlombok", "lombok-mapstruct-binding").versionRef("lombokMapstructBinding")

            library("mybatis", "org.mybatis", "mybatis").version(mybatisVersion)
            library("mybatisPlusAnnotation", "com.baomidou", "mybatis-plus-annotation").version(mybatisPlusVersion)
            library("mybatisPlusExtension", "com.baomidou", "mybatis-plus-extension").version(mybatisPlusVersion)
            library("mybatisPlusBootStarter", "com.baomidou", "mybatis-plus-spring-boot3-starter").version(mybatisPlusVersion)
            library("mybatisPlusCore", "com.baomidou", "mybatis-plus-core").version(mybatisPlusVersion)
            bundle("mybatisPlus", listOf("mybatis", "mybatisPlusAnnotation", "mybatisPlusExtension", "mybatisPlusCore"))

            library("elasticsearch", "co.elastic.clients", "elasticsearch-java").version(elasticVersion)
            library("elasticsearchClient", "org.elasticsearch.client", "elasticsearch-rest-client").version(elasticVersion)
            bundle("elasticsearch", listOf("elasticsearch", "elasticsearchClient"));

            library("commonsNet", "commons-net", "commons-net").version(commonsNetVersion)
            library("securityProtobuf", "live.lingting.protobuf", "protobuf-java").version(securityProtobufVersion)

            bundle("implementation", listOf("mapstruct"));
            bundle("compile", listOf("lombok"))
            bundle("annotation", listOf("mapstructProcessor", "lombok", "lombokMapstruct"))
        }
    }

}


rootProject.name = "lingting-framework"

// 遍历rootDir, 获取符合条件的文件夹名称,  使用代码 include 所有文件夹
rootDir.listFiles()?.filter { it.isDirectory && it.name.startsWith("lingting-") }?.forEach {
    include(it.name)
}
