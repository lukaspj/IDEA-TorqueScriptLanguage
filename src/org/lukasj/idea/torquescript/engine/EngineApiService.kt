package org.lukasj.idea.torquescript.engine

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.lukasj.idea.torquescript.engine.model.EngineApi
import org.lukasj.idea.torquescript.engine.model.EngineClass
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
                    val engineApiFile = VfsUtil.findFile(Path.of(project.basePath!!, "engineApi.xml"), false)
                        ?: VfsUtil.findFileByURL(this::class.java.getResource("/samples/engineApi.xml")!!)
                        ?: return@createCachedValue null

                    val string = String(engineApiFile.inputStream.readAllBytes())
                    val xmlStream = Regex("&#x([A-Za-z0-9]+);")
                        .findAll(string, 0)
                        .filter { match ->
                            match.groups[1]!!.value.toInt(16)
                                .let {
                                    !(it == 0x9
                                            || it == 0xA
                                            || it == 0xD
                                            || (it in 0xE000..0xFFFD)
                                            || (it in 0x10000..0x10FFFF))
                                }
                        }
                        .toList()
                        .foldRight(string) { match, acc ->
                            acc.removeRange(match.range)
                        }
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
    fun findFunction(name: String) = getFunctions().firstOrNull { it.name.equals(name, ignoreCase = true) }
    fun getEnums() = cachedApi?.value?.getAllEnums() ?: listOf()
    fun findEnum(name: String) = getEnums().firstOrNull { it.name.equals(name, ignoreCase = true) }
    fun getClasses() = cachedApi?.value?.getAllClasses() ?: listOf()
    fun findClass(className: String) = getClasses().firstOrNull { it.name.equals(className, ignoreCase = true) }
    fun isSubclassOf(engineClass: EngineClass, superclass: EngineClass): Boolean =
        engineClass.name == superclass.name
                || engineClass.superType?.let { superType ->
                        findClass(superType)?.let { isSubclassOf(it, superclass) }
                    } ?: false

    fun getSubclasses(baseClass: EngineClass) =
        cachedApi?.value?.getAllClasses()
            ?.filter {
                isSubclassOf(it, baseClass)
            }
            ?: listOf()

    fun getMethods(className: String) = findClass(className)?.methods ?: listOf()
    fun findMethod(className: String, name: String) = getMethods(className)
        .firstOrNull { it.name == name }

}