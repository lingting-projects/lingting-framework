plugins {
    id("lingting_jvm")
}

dependencies {
    implementation(project(":lingting-core"))
    api(project(":lingting-jackson"))

    api("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
}
