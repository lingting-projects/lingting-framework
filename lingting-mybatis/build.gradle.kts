dependencies {
    api(libs.bundles.mybatis)

    implementation(project(":lingting-datascope-jsql"))
    implementation(project(":lingting-core"))

    compileOnly(project(":lingting-jackson"))
    testImplementation(project(":lingting-jackson"))
}
