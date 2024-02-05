dependencies {
    implementation(project(":lingting-core"))
    implementation(project(":lingting-jackson"))
    implementation(project(":lingting-grpc"))
    implementation(libs.securityProtobuf)

    api(project(":lingting-security"))
}
