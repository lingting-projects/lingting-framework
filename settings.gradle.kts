plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "lingting-framework"

// 遍历rootDir, 获取符合条件的文件夹名称,  使用代码 include 所有文件夹
rootDir.listFiles().filterNotNull().filter { it.isDirectory && it.name.startsWith("lingting-") }.forEach {
    include(it.name)
}
