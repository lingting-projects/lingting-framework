plugins {
    `kotlin-dsl`
    alias(libs.plugins.publish) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.ksp) apply false
}

dependencies {
    // 使用 get().let { ... } 将插件对象转为 "group:id:version" 字符串
    implementation(libs.plugins.publish.get().let { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" })
    implementation(libs.plugins.kotlin.jvm.get().let { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" })
    implementation(libs.plugins.kotlin.ksp.get().let { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" })
}
