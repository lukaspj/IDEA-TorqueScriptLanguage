package org.lukasj.idea.torquescript.asset

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.util.ProgressWindow
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.io.exists
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.lukasj.idea.torquescript.TSFileUtil
import org.lukasj.idea.torquescript.engine.EngineDumpService
import org.lukasj.idea.torquescript.taml.*
import org.lukasj.idea.torquescript.telnet.TelnetConsoleService
import java.nio.file.Path
import java.util.concurrent.TimeUnit

class AssetImporter {
    fun accepts(file: VirtualFile) =
        when (file.extension) {
            "png", "jpg", "dts" -> true
            else -> false
        }

    fun import(project: Project?, file: VirtualFile) {
        if (project == null) {
            return
        }
        ProgressManager.getInstance()
            .runProcessWithProgressAsynchronously(
                object : Task.Backgroundable(project, "Rebuilding exports", true) {
                    override fun run(indicator: ProgressIndicator) {
                        when (file.extension) {
                            "png", "jpg" -> ImageAsset(
                                Path.of(file.parent.path).resolve("${file.nameWithoutExtension}.image.asset.taml"),
                                file.nameWithoutExtension
                            ).also {
                                it.imageFilePath = file.name
                            }.let {
                                Pair<TamlAsset, List<TamlAsset>>(it, listOf())
                            }

                            "dts" ->
                                extractDataFromEngine(project, file)
                                    .let {
                                        Pair(
                                            it.first(),
                                            it.drop(1)
                                        )
                                    }
                            /*ShapeAsset(
                            Path.of(file.parent.path).resolve("${file.nameWithoutExtension}.shape.asset.taml"),
                            file.nameWithoutExtension
                        ).also {
                            it.fileName = file.name
                        }.let {
                            Pair<TamlAsset, List<TamlAsset>>(
                                it,
                                extractDataFromEngine(project, file)
                            )
                        }*/
                            else -> null
                        }?.let { (asset, children) ->
                            ApplicationManager.getApplication()
                                .invokeLater {
                                    if (asset.assetFile.exists()) {
                                        JBPopupFactory.getInstance()
                                            .createMessage("Asset file ${asset.assetFile.fileName} already exists")
                                            .showCenteredInCurrentWindow(project)
                                    } else if (ImportAssetDialog(project, asset, children).showAndGet()) {
                                        children.forEach {
                                            it.saveToFile()
                                        }

                                        when (asset) {
                                            is ShapeAsset ->
                                                children.forEachIndexed { idx, child ->
                                                    if (child is MaterialAsset) {
                                                        project.getService(TamlModuleService::class.java)
                                                            .getModuleForFile(child.assetFile)
                                                            ?.let {
                                                                asset.materialSlots[idx] =
                                                                    "@asset=${it.moduleId}:${child.assetName}"
                                                            }
                                                    }
                                                }
                                        }
                                        asset.saveToFile()
                                    }
                                }
                        }
                    }
                },
                ProgressWindow(true, false, project)
                    .also { it.title = "Building asset data" }
            )
    }

    fun extractDataFromEngine(project: Project, assetFile: VirtualFile): List<TamlAsset> {
        var result: List<TamlAsset> = listOf()
        project.getService(TelnetConsoleService::class.java)
            .runTelnetSession(project) { telnetClient ->
                runBlocking {
                    telnetClient.eval(
                        """
                        setLogMode(6);
                        ModuleDatabase.setModuleExtension("module");
                        ModuleDatabase.scanModules( "core", false );
                        ModuleDatabase.LoadExplicit( "CoreModule" );
    
                        new AssetImporter(myImporter);
                        ${'$'}assetImportConfig = new AssetImportConfig();
                        if(!isObject(AssetImportSettings))
                        {
                            new Settings(AssetImportSettings)
                            {
                                file = "tools/assetBrowser/assetImportConfigs.xml";
                            };
                        }
                        AssetImportSettings.read();
                        ${'$'}assetImportConfig.loadImportConfig(AssetImportSettings, "DefaultImportConfig");
                        myImporter.setImportConfig(${'$'}assetImportConfig);
    
                        ${'$'}assetObj = myImporter.addImportingFile("${
                            TSFileUtil.relativePathFromRoot(
                                project,
                                assetFile
                            ).toString().replace('\\', '/')
                        }");
                        myImporter.processImportingAssets();
                        
                        echo("##XX####XX##OBJECT START##XX####XX##");
                        ${'$'}assetObj.dump();
                        echo("##XX####XX##OBJECT END##XX####XX##");
    
                        ${'$'}count = myImporter.getAssetItemChildCount(${'$'}assetObj);
                        echo("printing out " @ ${'$'}count @ " children");
                        for (${'$'}i = 0; ${'$'}i < ${'$'}count; ${'$'}i++) {
                            echo("##XX####XX##OBJECT START##XX####XX##");
                            ${'$'}assetObjChild = myImporter.getAssetItemChild(${'$'}assetObj, ${'$'}i);
                            ${'$'}assetObjChild.dump();
                            echo("##XX####XX##OBJECT END##XX####XX##");
                        }
                        
                        quit();
                    """.split('\n')
                            .joinToString(" ") { it.trim() }
                    )
                }
                suspend fun appendOutput(sb: StringBuilder): StringBuilder =
                    try {
                        withTimeout(5000) {
                            telnetClient.output.receive()
                                .let { sb.appendLine(it) }
                                .let { appendOutput(it) }
                        }
                    } catch (e: Exception) { sb }

                runBlocking { appendOutput(StringBuilder() ) }
                    .let {
                        Regex("##XX####XX##OBJECT START##XX####XX##([\\s\\S]+?)##XX####XX##OBJECT END##XX####XX##")
                            .findAll(it.toString())
                            .map { matchResult ->
                                matchResult.groupValues.drop(1).first()
                            }
                    }
                    .mapNotNull { objectDumpLog ->
                        project.getService(EngineDumpService::class.java)
                            .readObjectDump(objectDumpLog)
                            .let { objectDump ->
                                when (objectDump.staticFields.first { it.name.lowercase() == "assettype" }.value) {
                                    "MaterialAsset" ->
                                        objectDump.staticFields.first { it.name.lowercase() == "assetname" }.value
                                            .let { assetName ->
                                                MaterialAsset(
                                                    Path.of(assetFile.parent.path)
                                                        .resolve("${assetName}.material.asset.taml"),
                                                    assetName
                                                ).also {
                                                    it.materialDefinitionName = assetName
                                                }
                                            }
                                            .also {
                                                objectDump.staticFields.first { it.name.lowercase() == "cleanassetname" }.value
                                                    .let { cleanAssetName ->
                                                        it.mapTo = cleanAssetName
                                                    }
                                            }

                                    "ShapeAsset" ->
                                        objectDump.staticFields.first { it.name.lowercase() == "assetname" }.value
                                            .let { assetName ->
                                                ShapeAsset(
                                                    Path.of(assetFile.parent.path)
                                                        .resolve("${assetName}.shape.asset.taml"),
                                                    assetName
                                                ).also {
                                                    it.fileName = assetFile.name
                                                }
                                            }

                                    else -> {
                                        logger<AssetImporter>()
                                            .warn("The asset type ${objectDump.staticFields.first { it.name.lowercase() == "assettype" }.value} was not implemented as an engine dump asset type")
                                        null
                                    }
                                }
                            }
                    }
                    .let {
                        result = it.toList()
                    }
            }

        return result
    }
}