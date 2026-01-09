plugins {
    id("lingting_jvm")
}

dependencies {
    implementation(project(":lingting-core"))

    implementation(libs.commons.net)
}
