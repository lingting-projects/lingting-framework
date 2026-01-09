plugins {
    id("lingting_jvm")
}

dependencies {
    implementation(project(":lingting-core"))
    implementation(project(":lingting-jackson"))
    implementation(project(":lingting-http"))

}
