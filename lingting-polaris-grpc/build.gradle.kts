dependencies {
    api(project(":lingting-grpc"))
    implementation(project(":lingting-jackson"))

    implementation(platform(libs.polaris.dependencies))
    implementation("com.tencent.polaris:polaris-client")
    implementation("com.tencent.polaris:polaris-discovery-api")
    implementation("com.tencent.polaris:polaris-router-api")
    implementation("com.tencent.polaris:polaris-discovery-factory")
    implementation("com.tencent.polaris:polaris-router-factory")
    implementation("com.tencent.polaris:polaris-ratelimit-api")
    implementation("com.tencent.polaris:polaris-ratelimit-factory")
}
