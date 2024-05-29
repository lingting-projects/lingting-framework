dependencyResolutionManagement {
    val mybatisVersion = "3.5.15"
    val mybatisPlusVersion = "3.5.5"
    val jSqlParserVersion = "4.8"
    val grpcVersion = "1.61.1"
    val grpcProtobufVersion = "0.9.4"
    // 和grpc中依赖的版本保持一致
    val grpcProtocVersion = "3.25.1"
    val elasticVersion = "8.12.2"
    val commonsNetVersion = "3.10.0"
    val polarisVersion = "1.15.0"
    val javaxVersion = "1.3.2"

    versionCatalogs {
        create("libs") {
            version("springBoot", "3.2.4")
            version("formatter", "0.0.41")
            version("mapstruct", "1.5.3.Final")
            version("lombok", "1.18.30")
            version("lombokMapstructBinding", "0.2.0")
            version("jSqlParser", jSqlParserVersion)
            version("polaris", polarisVersion)
            version("grpc", grpcVersion)

            library("springBootClasspath", "org.springframework.boot", "spring-boot-gradle-plugin").versionRef("springBoot")
            library("springFormatterPlugin", "io.spring.javaformat", "spring-javaformat-gradle-plugin").versionRef("formatter")
            library("springFormatterCheckstyle", "io.spring.javaformat", "spring-javaformat-checkstyle").versionRef("formatter")

            library("springBootDependencies", "org.springframework.boot", "spring-boot-dependencies").versionRef("springBoot")
            library("polarisDependencies", "com.tencent.polaris", "polaris-dependencies").versionRef("polaris")
            library("grpcDependencies", "io.grpc", "grpc-bom").version(grpcVersion)

            library("grpcProtobufPlugin", "com.google.protobuf", "protobuf-gradle-plugin").version(grpcProtobufVersion)
            library("grpcProtoc", "com.google.protobuf", "protoc").version(grpcProtocVersion)
            library("javaxAnnotation", "javax.annotation", "javax.annotation-api").version(javaxVersion)

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
