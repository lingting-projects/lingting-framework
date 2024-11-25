dependencies {
    api(libs.bundles.elasticsearch) {
        exclude("commons-logging", "commons-logging")
    }

    implementation("org.slf4j:jcl-over-slf4j")
    implementation(project(":lingting-core"))
    implementation(project(":lingting-jackson"))

    compileOnly(project(":lingting-datascope"))
}
