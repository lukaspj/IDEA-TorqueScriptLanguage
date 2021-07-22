package org.lukasj.idea.torquescript.engine

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.lukasj.idea.torquescript.engine.model.EngineApi
import org.lukasj.idea.torquescript.engine.parser.EngineApiParser
import org.lukasj.idea.torquescript.symbols.TSModificationTracker
import java.nio.file.Path
import javax.xml.stream.XMLInputFactory

@Service
class EngineApiService(private val project: Project) {

    private val modificationTracker = TSModificationTracker()

    private var cachedApi: CachedValue<EngineApi>? = null
    private val xmlInputFactory = XMLInputFactory.newInstance()


    init {
        buildCaches()
    }

    fun dropCaches() {
        modificationTracker.count++
    }

    private fun buildCaches() {
        val manager = CachedValuesManager.getManager(project)
        val dependencies = arrayOf<Any>(modificationTracker)

        cachedApi =
            manager.createCachedValue(
                {
                    val engineApiFile = VfsUtil.findFile(Path.of(project.basePath, "engineApi.xml"), true)
                        ?: return@createCachedValue null

                    val string = String(engineApiFile.inputStream.readAllBytes())
                    val xmlStream = string
                        .replace("&#x1C", "")
                        .replace("&#x1D", "")
                        .replace("&#x1A", "")
                        .replace("&#x01", "")
                        .replace("&#x04", "")
                        .replace("&#x18", "")
                        .replace("&#x19", "")
                        .byteInputStream()

                    val eventReader = xmlInputFactory.createXMLEventReader(xmlStream, "UTF-8")

                    CachedValueProvider.Result.create(
                        EngineApiParser(eventReader).parse(),
                        dependencies
                    )
                },
                false
            )
    }

    fun getFunctions() = cachedApi?.value?.getAllFunctions() ?: listOf()
    fun getStaticFunctions() = getFunctions().filter { it.isStatic }
    fun findFunction(name: String) = getFunctions().firstOrNull { it.name == name }
    fun findClass(className: String) = cachedApi?.value?.getAllClasses()?.firstOrNull {it.name == className}
    fun getMethods(className: String) = findClass(className)?.methods ?: listOf()
    fun findMethod(className: String, name: String) = getMethods(className)
        .firstOrNull { it.name == name }
}