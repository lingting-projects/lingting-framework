plugins {
    id("lingting_jvm")
}

// 确保 aws 先加载, 避免下面直接引用aws模块时出错
evaluationDependsOn(":lingting-aws")

dependencies{
    api(project(":lingting-aws"))

    implementation(project(":lingting-core"))
    implementation(project(":lingting-jackson"))

    testImplementation(
        project(":lingting-aws").dependencyProject
            .extensions.getByType<SourceSetContainer>()
            .getByName("test")
            .output
    )
}
