plugins {
    id("lingting_jvm")
}

dependencies {
    implementation(project(":lingting-core"))
    implementation(project(":lingting-jackson"))

    api("com.squareup.okhttp3:okhttp")
}
