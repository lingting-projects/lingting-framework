dependencies {
    api(libs.bundles.mybatisPlus)

    implementation(project(":lingting-datascope-jsql"))
    implementation(project(":lingting-core"))

    compileOnly(project(":lingting-jackson"))
}
