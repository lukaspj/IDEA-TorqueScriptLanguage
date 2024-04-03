package org.lukasj.idea.torquescript.engine

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.lukasj.idea.torquescript.TSFileUtil
import org.lukasj.idea.torquescript.engine.model.EngineApi
import org.lukasj.idea.torquescript.engine.model.EngineClass
import org.lukasj.idea.torquescript.engine.parser.EngineApiParser
import org.lukasj.idea.torquescript.symbols.TSModificationTracker
import java.nio.file.Path
import javax.xml.stream.XMLInputFactory

@Service(Service.Level.PROJECT)
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

    fun findEngineApiFile() =
        TSFileUtil.getRootDirectory(project)
            ?.let { VfsUtil.findFile(Path.of(it, "engineApi.xml"), false) }
            ?: VfsUtil.findFileByURL(this::class.java.getResource("/samples/engineApi.xml")!!)

    private fun buildCaches() {
        val manager = CachedValuesManager.getManager(project)
        val engineApiFile = findEngineApiFile()
            ?: return
        val dependencies = arrayOf<Any>(modificationTracker, engineApiFile)

        cachedApi =
            manager.createCachedValue(
                {

                    val xmlStream = String(engineApiFile.inputStream.readAllBytes())
                        .let { removeIllegalXmlCharacters(it) }
                        .let { escapeNewlinesInAttributes(it) }
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

    private fun escapeNewlinesInAttributes(text: String) =
        text.split('"')
            .chunked(2)
            .fold(StringBuilder()) { acc, chunk ->
                if (chunk.size == 1) {
                    acc.append(chunk[0])
                } else {
                    acc.append(chunk[0])
                        .append('"')
                        .append(
                            chunk[1]
                                .replace("\r\n", "&#xA;")
                                .replace("\r", "&#xA;")
                                .replace("\n", "&#xA;")
                        )
                        .append('"')
                }
            }.toString()

    private fun removeIllegalXmlCharacters(text: String) =
        Regex("&#x([A-Za-z0-9]+);")
            .findAll(text, 0)
            .filter { match ->
                match.groups[1]!!.value.toInt(16)
                    .let {
                        !(it == 0x9
                                || (it in 0xE000..0xFFFD)
                                || (it in 0x10000..0x10FFFF))
                    }
            }
            .toList()
            .foldRight(text) { match, acc ->
                acc.removeRange(match.range)
            }

    fun getFunctions() = cachedApi?.value?.getAllFunctions() ?: listOf()
    fun getStaticFunctions() = getFunctions().filter { it.isStatic }
    fun findFunction(name: String) = getFunctions()
        .firstOrNull {
            it.toString().equals(name, ignoreCase = true)
        }


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