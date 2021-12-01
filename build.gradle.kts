import org.jetbrains.grammarkit.tasks.*
import org.jetbrains.intellij.tasks.PublishPluginTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val channel = prop("publishChannel")

plugins {
    id("org.jetbrains.intellij") version "1.1.2"
    kotlin("jvm") version "1.4.32"

    id("org.jetbrains.grammarkit") version "2021.1.3"
    id("org.jetbrains.changelog") version "1.2.0"
}

group = "org.lukasj"
version = prop("pluginVersion")
repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

sourceSets {
    getByName("main").apply {
        java.srcDirs("src", "gen")
        resources.srcDirs("resources")
            .exclude("scripts/**")
    }
}

// Java target version
java.sourceCompatibility = JavaVersion.VERSION_11

// https://plugins.jetbrains.com/docs/intellij/dynamic-plugins.html#diagnosing-leaks
tasks.runIde {
    jvmArgs = mutableListOf("-XX:+UnlockDiagnosticVMOptions")

    // Set to true to generate hprof files on unload fails
    systemProperty("ide.plugins.snapshot.on.unload.fail", "false")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2021.3")
    pluginName.set("TorqueScript")
}

val generateTorqueScriptParser = task<GenerateParser>("GenerateTorqueScriptParser") {
    source = "src/org/lukasj/idea/torquescript/grammar/TorqueScript.bnf"

    targetRoot = "gen"

    pathToParser = "/org/lukasj/idea/torquescript/parser/TSParser"

    pathToPsiRoot = "/org/lukasj/idea/torquescript/psi"

    purgeOldFiles = true

    outputs.file("${targetRoot}${pathToParser}.java")
    outputs.dir("${targetRoot}${pathToPsiRoot}")
}

val generateTorqueScriptLexer = task<GenerateLexer>("GenerateTorqueScriptLexer") {
    source = "src/org/lukasj/idea/torquescript/grammar/TorqueScript.flex"

    targetDir = "gen/org/lukasj/idea/torquescript/lexer"
    targetClass = "TSLexer"

    outputs.file("${targetDir}/${targetClass}.java")
}

changelog {
    version.set(prop("pluginVersion"))
}

tasks {
    patchPluginXml {
        if (!prop("pluginVersion").contains("beta")) {
            changeNotes.set(provider { changelog.get(prop("pluginVersion")).toHTML() })
        }
        sinceBuild.set("211")
        untilBuild.set(null as String?)
    }

    withType<PublishPluginTask> {
        token.set(prop("publishToken"))
        channels.set(listOf(channel))
    }

    prepareSandbox {
        from(fileTree("resources") { include("scripts/**") }) {
            into("/${project.name}")
        }
    }

    // Specify the right jvm target for Kotlin
    withType<KotlinCompile>().configureEach {
        dependsOn(generateTorqueScriptLexer, generateTorqueScriptParser)

        sourceCompatibility = JavaVersion.VERSION_11.toString()
        targetCompatibility = JavaVersion.VERSION_11.toString()

        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = listOf("-Xjvm-default=enable")
        }
    }
}


fun hasProp(name: String): Boolean = extra.has(name)

fun prop(name: String): String =
    extra.properties[name] as? String
        ?: error("Property `$name` is not defined in gradle.properties")