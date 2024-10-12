import org.jetbrains.changelog.Changelog
import org.jetbrains.intellij.platform.gradle.tasks.PublishPluginTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val channel = prop("publishChannel")

plugins {
    id("org.jetbrains.intellij.platform") version "2.1.0"
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.grammarkit") version "2022.3.2.2"
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

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation("io.sentry:sentry:6.4.2") {
        exclude(group = "org.slf4j")
    }

    implementation(kotlin("reflect"))

    intellijPlatform {
        instrumentationTools()
        pluginVerifier()
        zipSigner()

        version = if (isLegacyBuild) {
            intellijIdeaCommunity("2022.2")
        } else {
            intellijIdeaCommunity("2022.3")
            // intellijIdeaCommunity("2024.2.3")
        }
    }
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

// See https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellijPlatform  {
    //type.set("RD")

    pluginConfiguration {
        name.set("TorqueScript")
    }
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
            untilBuild.set("242.*")
        }
    }

    generateLexer {
        sourceFile.set(file("src/org/lukasj/idea/torquescript/grammar/TorqueScript.flex"))

        targetOutputDir.set(file("gen/org/lukasj/idea/torquescript/lexer"))

        purgeOldFiles.set(true)

        outputs.dir(targetOutputDir.get())
    }

    generateParser {
        sourceFile.set(file("src/org/lukasj/idea/torquescript/grammar/TorqueScript.bnf"))

        targetRootOutputDir.set(file("gen"))

        pathToParser.set("/org/lukasj/idea/torquescript/parser/TSParser")

        pathToPsiRoot.set("/org/lukasj/idea/torquescript/psi")

        purgeOldFiles.set(true)

        outputs.file("${targetRootOutputDir.get()}${pathToParser.get()}.java")
        outputs.dir("${targetRootOutputDir.get()}${pathToPsiRoot.get()}")
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