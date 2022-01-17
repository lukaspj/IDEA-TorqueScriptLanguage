package org.lukasj.idea.torquescript.asset

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.EmptyProgressIndicator
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.io.exists
import org.lukasj.idea.torquescript.taml.ImageAsset
import org.lukasj.idea.torquescript.telnet.TelnetConsoleService
import java.nio.file.Path
import java.util.concurrent.TimeUnit

class AssetImporter {
    fun accepts(file: VirtualFile) =
        when (file.extension) {
            "png", "jpg" -> true
            else -> false
        }

    fun import(project: Project?, file: VirtualFile) {
        if (project == null) {
            return
        }
        val outputBuilder = StringBuilder()
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
                            }
                            else -> null
                        }?.let { asset ->
                            extractDataFromEngine(project)
                            ApplicationManager.getApplication()
                                .invokeLater {
                                    if (asset.assetFile.exists()) {
                                        JBPopupFactory.getInstance()
                                            .createMessage("Asset file ${asset.assetFile.fileName} already exists")
                                            .showCenteredInCurrentWindow(project)
                                    } else if (ImportAssetDialog(project, asset).showAndGet()) {
                                        asset.saveToFile()
                                    }
                                }
                        }
                    }
                },
                EmptyProgressIndicator()
            )
    }

    fun extractDataFromEngine(project: Project): Map<String, String> {
        val success = project.getService(TelnetConsoleService::class.java)
            .runTelnetSession(project) { telnetClient ->
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
                    
                                        ${'$'}assetObj = myImporter.addImportingFile("data/CoinCollectionModule/objects/coin/coin.dts");
                                        myImporter.processImportingAssets();
                    
                                        ${'$'}count = myImporter.getAssetItemChildCount(${'$'}assetObj);
                                        echo("printing out " @ ${'$'}count @ " children");
                                        for (${'$'}i = 0; ${'$'}i < ${'$'}count; ${'$'}i++) {
                                            echo("##XX####XX##OBJECT START##XX####XX##");
                                            ${'$'}assetObjChild = myImporter.getAssetItemChild(${'$'}assetObj, ${'$'}i);
                                            ${'$'}assetObjChild.dump();
                                            echo("##XX####XX##OBJECT END##XX####XX##");
                                        }
                                    """.split('\n')
                        .joinToString(" ") { it.trim() }
                )
                fun appendOutput(sb: StringBuilder): StringBuilder =
                    telnetClient.outputQueue.poll(5, TimeUnit.SECONDS)
                        ?.let { sb.appendLine(it) }
                        ?.let { appendOutput(it) }
                        ?: sb
                appendOutput(StringBuilder())
                    .also { println(it) }
                    .let {
                        Regex("##XX####XX##OBJECT START##XX####XX##([\\s\\S]+)##XX####XX##OBJECT END##XX####XX##")
                            .find(it.toString())
                            ?.groupValues?.drop(1)
                    }
                    ?.map {
                        println("Object dump match:")
                        println(it)
                    }
            }

        return mapOf()
    }
}