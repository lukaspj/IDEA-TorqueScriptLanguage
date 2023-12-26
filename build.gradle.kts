import org.jetbrains.grammarkit.tasks.*
import org.jetbrains.intellij.tasks.PublishPluginTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.resolve.compatibility

val channel = prop("publishChannel")

plugins {
    id("org.jetbrains.intellij") version "1.16.1"
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
    id("org.jetbrains.grammarkit") version "2021.2.2"
    id("org.jetbrains.changelog") version "1.3.1"
}

group = "org.lukasj"
version = "${prop("pluginVersion")}-legacy2021"

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
            srcDirs("src", "gen")
                .exclude(
                    "org/lukasj/idea/torquescript/runner/TSRunConfigurationSettingsEditor.kt",
                    "org/lukasj/idea/torquescript/runner/TSAttachConfigurationSettingsEditor.kt",
                    "org/lukasj/idea/torquescript/asset/**",
                    "org/lukasj/idea/torquescript/action/ImportAsset.kt"
                )
        }
        resources {
            srcDirs("resources")
                .exclude("scripts/**")
                .exclude("placeholder-schema.xsd")
        }
    }
}

// Java target version
java.sourceCompatibility = JavaVersion.VERSION_11

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2021.2")
    //type.set("RD")
    pluginName.set("TorqueScript")
}

changelog {
    version.set(prop("pluginVersion"))
}

tasks {
    patchPluginXml {
        if (!prop("pluginVersion").contains("beta")) {
            changeNotes.set(provider { changelog.get(prop("pluginVersion")).toHTML() })
        }
        sinceBuild.set("212")
        untilBuild.set("221")
    }

    generateLexer {
        source.set("src/org/lukasj/idea/torquescript/grammar/TorqueScript.flex")

        targetDir.set("gen/org/lukasj/idea/torquescript/lexer")
        targetClass.set("TSLexer")

        purgeOldFiles.set(true)

        outputs.file("${targetDir.get()}/${targetClass.get()}.java")
    }

    generateParser {
        source.set("src/org/lukasj/idea/torquescript/grammar/TorqueScript.bnf")

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
            jvmTarget = "11"
            freeCompilerArgs = listOf("-Xjvm-default=all")
        }
    }
}


fun hasProp(name: String): Boolean = extra.has(name)

fun prop(name: String): String =
    extra.properties[name] as? String
        ?: error("Property `$name` is not defined in gradle.properties")