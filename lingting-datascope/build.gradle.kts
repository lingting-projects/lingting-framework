plugins {
    id("lingting_jvm")
}

dependencies {
    implementation(project(":lingting-core"))

    api("com.github.jsqlparser", "jsqlparser", libs.versions.jsqlparser.get())
}
