dependencies {
    api(project(":lingting-grpc"))
    implementation(project(":lingting-jackson"))

    compileOnly(platform("com.tencent.polaris:polaris-dependencies:${libs.versions.polarisVersion.get()}"))
    compileOnly("com.tencent.polaris:polaris-client")
    compileOnly("com.tencent.polaris:polaris-discovery-api")
    compileOnly("com.tencent.polaris:polaris-router-api")
    compileOnly("com.tencent.polaris:polaris-discovery-factory")
    compileOnly("com.tencent.polaris:polaris-router-factory")
    compileOnly("com.tencent.polaris:polaris-ratelimit-api")
    compileOnly("com.tencent.polaris:polaris-ratelimit-factory")
}
