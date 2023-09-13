import org.jetbrains.changelog.Changelog
import org.jetbrains.intellij.tasks.PublishPluginTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val channel = prop("publishChannel")

plugins {
    kotlin("jvm") version "1.8.10"

    id("org.jetbrains.intellij") version "1.15.0"
    id("org.jetbrains.grammarkit") version "2022.3.1"
    id("org.jetbrains.changelog") version "2.2.0"
}

group = "org.lukasj"
val isLegacyBuild = prop("legacyBuild") == "true"
version = if (isLegacyBuild) {
    "${prop("pluginVersion")}-legacy"
} else {
    prop("pluginVersion")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.sentry:sentry:6.4.2") {
        exclude(group = "org.slf4j")
    }

    implementation(kotlin("reflect"))
}

idea {
    module {
        generatedSourceDirs.add(file("gen"))
    }
}

sourceSets {
    main {
        java {
            if (isLegacyBuild) {
                srcDirs("src", "gen")
            } else {
                srcDirs("src", "gen")
                    .exclude("org/lukasj/idea/torquescript/**/*legacy*")
            }
        }
        resources {
            srcDirs("resources")
                .exclude("scripts/**")
                .exclude("placeholder-schema.xsd")
        }
    }
}

// Java target version
java.sourceCompatibility = JavaVersion.VERSION_17

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    if (isLegacyBuild) {
        version.set("2022.2")
    } else {
        version.set("2023.2")
    }

    //version.set("2021.2")
    //type.set("RD")
    pluginName.set("TorqueScript")
}

changelog {
    version.set(prop("pluginVersion"))
}

tasks {
    patchPluginXml {
        if (!prop("pluginVersion").contains("beta")) {
            changeNotes.set(provider {
                changelog.renderItem(
                    changelog.get(prop("pluginVersion")),
                    Changelog.OutputType.HTML
                )
            })
        }
        if (isLegacyBuild) {
            sinceBuild.set("221")
            untilBuild.set("223")
        } else {
            sinceBuild.set("223")
            untilBuild.set("232.*")
        }
    }

    generateLexer {
        sourceFile.set(file("src/org/lukasj/idea/torquescript/grammar/TorqueScript.flex"))

        targetDir.set("gen/org/lukasj/idea/torquescript/lexer")
        targetClass.set("TSLexer")

        purgeOldFiles.set(true)

        outputs.file("${targetDir.get()}/${targetClass.get()}.java")
    }

    generateParser {
        sourceFile.set(file("src/org/lukasj/idea/torquescript/grammar/TorqueScript.bnf"))

        targetRoot.set("gen")

        pathToParser.set("/org/lukasj/idea/torquescript/parser/TSParser")

        pathToPsiRoot.set("/org/lukasj/idea/torquescript/psi")

        purgeOldFiles.set(true)

        outputs.file("${targetRoot.get()}${pathToParser.get()}.java")
        outputs.dir("${targetRoot.get()}${pathToPsiRoot.get()}")
    }

    // https://plugins.jetbrains.com/docs/intellij/dynamic-plugins.html#diagnosing-leaks
    runIde {
        jvmArgs = mutableListOf("-XX:+UnlockDiagnosticVMOptions")

        // Set to true to generate hprof files on unload fails
        systemProperty("ide.plugins.snapshot.on.unload.fail", "false")
        systemProperty("idea.is.internal", "true")
    }

    withType<PublishPluginTask>().configureEach {
        token.set(prop("publishToken"))
        channels.set(listOf(channel))
    }

    prepareSandbox {
        from(fileTree("resources") { include("scripts/**") }) {
            into("/${project.name}")
        }
        from(fileTree("resources") { include("placeholder-schema.xsd") }) {
            into("/${project.name}")
        }
    }

    // Specify the right jvm target for Kotlin
    withType<KotlinCompile>().configureEach {
        dependsOn("generateLexer", "generateParser")
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = listOf("-Xjvm-default=all")
        }
    }
}


fun hasProp(name: String): Boolean = extra.has(name)

fun prop(name: String): String =
    extra.properties[name] as? String
        ?: error("Property `$name` is not defined in gradle.properties")