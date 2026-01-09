plugins {
    id("com.google.devtools.ksp")
    // jvm 插件最后加载, 因为配置是基于这个插件进行的.
    id("org.jetbrains.kotlin.jvm")
    id("lingting_publish")
}

