import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val projectGroup = "live.lingting.framework"
val projectVersion = "2025.05.23-Beta-12"

val catalogLibs = libs
val dependencyProjects = subprojects.filter { it.name.endsWith("dependencies") }
val allProjects = subprojects.filter { it.name != "buildSrc" }
// 使用的java版本
val javaVersion = JavaVersion.VERSION_21
val targetVersion = JavaVersion.VERSION_21
// 字符集
val targetEncoding = "UTF-8"
val ideaLanguageLevel = IdeaLanguageLevel(targetVersion)

plugins {
    id("idea")
}

idea {
    project {
        languageLevel = ideaLanguageLevel
        targetBytecodeVersion = javaVersion
    }
}

allprojects {
    group = projectGroup
    version = projectVersion

    apply {
        plugin("idea")
    }

    idea {
        module {
            languageLevel = ideaLanguageLevel
            targetBytecodeVersion = javaVersion
        }
    }

}

configure(allProjects) {

    val isDependencyProject = dependencyProjects.contains(this)

    fun configureKotlin() {
        tasks.withType<KotlinCompile> {
            compilerOptions {
                javaParameters = true
                jvmTarget = JvmTarget.fromTarget(targetVersion.majorVersion)
                freeCompilerArgs.addAll(
                    "-Xjvm-default=all",
                    "-Xjsr305=strict",
                )
            }
        }

        configure<KotlinProjectExtension> {
            jvmToolchain(javaVersion.majorVersion.toInt())
        }
    }

    plugins.withId("org.jetbrains.kotlin.jvm") {
        configureKotlin()

        configure<JavaPluginExtension> {
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
        }

        tasks.withType<Test> {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
                showStandardStreams = true
                exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            }
        }

        tasks.withType<JavaCompile> {
            options.encoding = targetEncoding
            options.compilerArgs.addAll(
                arrayOf(
                    "-parameters",
                    "-Xlint:unchecked",
                    "-Xlint:deprecation"
                )
            )
            options.setIncremental(true)
        }

        tasks.withType<Javadoc> {
            isFailOnError = false
            options.encoding(targetEncoding)
        }

        if (!isDependencyProject) {
            dependencies {
                catalogLibs.bundles.dependencies.get().forEach {
                    add("implementation", platform(it))
                }
                add("implementation", catalogLibs.bundles.implementation)

                add("annotationProcessor", catalogLibs.bundles.annotation)
                add("ksp", catalogLibs.bundles.ksp)
                add("compileOnly", catalogLibs.bundles.compile)
                add("testImplementation", catalogLibs.bundles.test)
            }
        }

    }

}
