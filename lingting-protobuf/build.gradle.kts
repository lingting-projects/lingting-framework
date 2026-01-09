plugins {
    id("lingting_jvm")
    alias(libs.plugins.grpc)
}


dependencies {
    api("io.grpc:grpc-stub")
    api("io.grpc:grpc-protobuf")
    api(libs.javax.annotation)
    compileOnly(enforcedPlatform(libs.grpc.dependencies))
}

protobuf {
    protoc {
        artifact = libs.grpc.protoc.get().toString()
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
