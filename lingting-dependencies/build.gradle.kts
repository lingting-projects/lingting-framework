plugins {
    id("java-platform")
}

dependencies {
    constraints {
        // 三方依赖约束
        for (catalog in versionCatalogs) {
            for (alias in catalog.libraryAliases) {
                val optional = catalog.findLibrary(alias)
                if (optional.isEmpty) {
                    continue
                }
                val provider = optional.get()
                if (alias.contains("Dependencies")) {
                    api(platform(provider))
                } else {
                    api(provider)
                }
            }
        }

        // 子项目依赖约束
        project.parent?.subprojects?.forEach {
            if (it != project) {
                api(project(":${it.name}"))
            }
        }
    }
}
