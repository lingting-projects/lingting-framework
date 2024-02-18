dependencies {
    implementation(project(":lingting-core"))
    implementation(project(":lingting-jackson"))

    api(project(":lingting-grpc"))
    api(libs.securityProtobuf)
    api(project(":lingting-security"))
}
