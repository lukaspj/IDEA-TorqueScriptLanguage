package org.lukasj.idea.torquescript.util

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.psi.TSObjectDeclaration
import org.lukasj.idea.torquescript.reference.ReferenceUtil
import org.lukasj.idea.torquescript.taml.TamlModule
import org.lukasj.idea.torquescript.taml.TamlModuleService

interface TSCachedObject {
    val name: String
    val type: String
    val parent: String?
    val containingFile: VirtualFile?
}

class CachedObjectDeclaration(private val declaration: TSObjectDeclaration) : TSCachedObject {
    companion object {
        fun fromDeclaration(declaration: TSObjectDeclaration) = CachedObjectDeclaration(declaration)
    }

    override val name: String
        get() = declaration.name!!

    override val type: String
        get() = declaration.getTypeName()

    override val parent: String?
        get() = declaration.getParentBlock()?.lastChild?.text

    override val containingFile: VirtualFile?
        get() = declaration.containingFile?.virtualFile

    override fun toString() = name
}

class ModuleObject(private val module: TamlModule) : TSCachedObject {
    companion object {
        fun fromModule(module: TamlModule) = ModuleObject(module)
    }

    override val name: String
        get() = module.moduleId!!

    override val type: String
        get() = "ModuleDefinition"

    override val parent: String?
        get() = null

    override val containingFile: VirtualFile
        get() = module.file
}

@Service
class TSTypeLookupService {
    fun getObjects(project: Project) =
        ReferenceUtil.getObjects(project)
            .map(CachedObjectDeclaration::fromDeclaration)
            .plus(
                project.getService(TamlModuleService::class.java)
                    .getModules()
                    .map(ModuleObject::fromModule)
            )
            .plus(
                object : TSCachedObject {
                    override val name: String
                        get() = "ModuleDatabase"
                    override val type: String
                        get() = "ModuleManager"
                    override val parent: String?
                        get() = null
                    override val containingFile: VirtualFile?
                        get() = null

                    override fun toString() = name
                }
            )


    fun findObject(project: Project, key: String): List<TSCachedObject> =
        getObjects(project)
            .filter { it.name.equals(key, true) }


    fun getNamespaces(rootNs: String, project: Project): List<String> {
        // Handle edge-case
        if (rootNs == "EngineObject") return listOf()

        val obj = findObject(project, rootNs)

        if (obj.size > 1) {
            logger<TSTypeLookupService>()
                .warn("Too many instances of obj $rootNs")
        }
        if (obj.isEmpty()) {
            val superType = project.getService(EngineApiService::class.java)
                .findClass(rootNs)
                ?.superType
            return if (superType != null) {
                getNamespaces(superType, project)
                    .plus(rootNs)
            } else {
                logger<TSTypeLookupService>()
                    .warn("No instances of $rootNs found")
                listOf(rootNs)
            }
        }

        return if (obj[0].parent != null) {
            getNamespaces(obj[0].parent!!, project)
        } else {
            getNamespaces(obj[0].type, project)
        }.plus(rootNs)
    }
}