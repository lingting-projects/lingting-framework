val bomList = mutableListOf<Provider<MinimalExternalModuleDependency>>()
val apiList = mutableListOf<Provider<MinimalExternalModuleDependency>>()

// 三方依赖解析
for (catalog in versionCatalogs) {
    for (alias in catalog.libraryAliases) {
        val optional = catalog.findLibrary(alias)
        if (optional.isEmpty) {
            continue
        }
        val provider = optional.get()
        if (alias.contains("Dependencies")) {
            bomList.add(provider)
        } else {
            apiList.add(provider)
        }
    }
}

javaPlatform {
    allowDependencies()
}

dependencies {

    bomList.forEach {
        api(platform(it))
    }

    constraints {
        // 三方依赖约束
        apiList.forEach {
            api(it)
        }
        // 子项目依赖约束
        project.parent?.subprojects?.forEach {
            if (it != project) {
                api(project(":${it.name}"))
            }
        }
    }
}
