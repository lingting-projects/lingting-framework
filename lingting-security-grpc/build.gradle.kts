dependencies {
    implementation(project(":lingting-core"))
    implementation(project(":lingting-jackson"))

    api(project(":lingting-grpc"))
    api(project(":lingting-security"))
    api(project(":lingting-protobuf"))

    api("io.grpc:grpc-stub")
    api("io.grpc:grpc-protobuf")
}
