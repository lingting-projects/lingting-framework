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
