plugins {
    id("com.google.protobuf")
}

dependencies {
    api("io.grpc:grpc-stub")
    api("io.grpc:grpc-protobuf")
    api(libs.javaxAnnotation)
    compileOnly(enforcedPlatform(libs.grpcDependencies))
}

protobuf {
    protoc {
        artifact = libs.grpcProtoc.get().toString()
    }
    plugins {
        create("protoc-grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${libs.versions.grpc.get()}"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").configureEach {
            plugins {
                create("protoc-grpc") {
                }
            }
        }
    }
}
