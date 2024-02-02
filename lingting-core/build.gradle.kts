dependencies {
    implementation("jakarta.servlet:jakarta.servlet-api")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation(libs.mybatisPlusAnnotation)

    compileOnly("org.springframework:spring-core")
    testImplementation("org.springframework:spring-core")
}
