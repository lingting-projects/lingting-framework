dependencies {
    api("jakarta.annotation:jakarta.annotation-api")
    compileOnly("jakarta.servlet:jakarta.servlet-api")

    compileOnly(libs.mybatis.plus.annotation)
    compileOnly("com.fasterxml.jackson.core:jackson-annotations")
    compileOnly("org.springframework:spring-core")

    testImplementation(libs.mybatis.plus.annotation)
    testImplementation("com.fasterxml.jackson.core:jackson-annotations")
    testImplementation("org.springframework:spring-core")
}
