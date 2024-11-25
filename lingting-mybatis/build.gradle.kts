dependencies {
    api(libs.bundles.mybatis)

    implementation(project(":lingting-core"))

    compileOnly(project(":lingting-datascope"))
    testImplementation(project(":lingting-datascope"))
    compileOnly(project(":lingting-jackson"))
    testImplementation(project(":lingting-jackson"))
}
