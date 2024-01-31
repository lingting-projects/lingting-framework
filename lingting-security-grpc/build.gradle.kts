dependencies {
    implementation(project(":lingting-core"))
    implementation(project(":lingting-jackson"))
    implementation(project(":lingting-grpc-client"))
    implementation(project(":lingting-grpc-server"))
    implementation(libs.securityProtobuf)

    api(project(":lingting-security"))
}
