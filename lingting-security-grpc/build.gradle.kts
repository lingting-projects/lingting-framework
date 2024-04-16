dependencies {
    implementation(project(":lingting-core"))
    implementation(project(":lingting-jackson"))

    api(project(":lingting-grpc"))
    api(project(":lingting-security"))
    api(libs.securityProtobuf) {
        exclude("com.google.protobuf")
        exclude("io.grpc")
    }

    api("com.google.protobuf:protobuf-java")
    api("io.grpc:grpc-stub")
    api("io.grpc:grpc-protobuf")
}
