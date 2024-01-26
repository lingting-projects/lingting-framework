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
            val mybatisVersion = "3.5.15"
            val mybatisPlusVersion = "3.5.5"
            val jsqlparserVersion = "4.8"

            library("mybatis", "org.mybatis", "mybatis").version(mybatisVersion)
            library("mybatisPlusAnnotation", "com.baomidou", "mybatis-plus-annotation").version(mybatisPlusVersion)
            library("mybatisPlusExtension", "com.baomidou", "mybatis-plus-extension").version(mybatisPlusVersion)
            library("mybatisPlusBootStarter", "com.baomidou", "mybatis-plus-boot-starter").version(mybatisPlusVersion)
            library("mybatisPlusCore", "com.baomidou", "mybatis-plus-core").version(mybatisPlusVersion)
            library("msqlparser", "com.github.jsqlparser", "jsqlparser").version(jsqlparserVersion)

            bundle("mybatisPlus", listOf("mybatis", "mybatisPlusAnnotation", "mybatisPlusExtension", "mybatisPlusBootStarter", "mybatisPlusCore", "msqlparser"))
        }
    }

}


rootProject.name = "lingting-framework"
include("lingting-core")
