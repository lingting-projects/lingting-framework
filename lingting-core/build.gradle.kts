dependencies {
    api("jakarta.annotation:jakarta.annotation-api")

    compileOnly(libs.mybatis.plus.annotation)
    compileOnly("org.springframework:spring-core")
    compileOnly("jakarta.servlet:jakarta.servlet-api")
    compileOnly("com.fasterxml.jackson.core:jackson-annotations")

    testImplementation(libs.mybatis.plus.annotation)
    testImplementation("org.springframework:spring-core")
    testImplementation("jakarta.servlet:jakarta.servlet-api")
    testImplementation("com.fasterxml.jackson.core:jackson-annotations")
    testImplementation(kotlin("test"))
}
