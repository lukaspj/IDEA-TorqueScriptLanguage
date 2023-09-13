package org.lukasj.idea.torquescript.util

import com.intellij.ide.util.PsiNavigationSupport
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.util.PlatformIcons
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.psi.TSObjectDeclaration
import org.lukasj.idea.torquescript.reference.ReferenceUtil
import org.lukasj.idea.torquescript.taml.TamlModule
import org.lukasj.idea.torquescript.taml.TamlModuleService

interface TSCachedObject : NavigationItem {
    val objectName: String
    val type: String
    val parent: String?
    val containingFile: VirtualFile?
}

class CachedObjectDeclaration(private val declaration: TSObjectDeclaration) : TSCachedObject {
    companion object {
        fun fromDeclaration(declaration: TSObjectDeclaration) = CachedObjectDeclaration(declaration)
    }

    override val objectName: String
        get() = declaration.name!!

    override val type: String
        get() = declaration.getTypeName()

    override val parent: String?
        get() = declaration.getParentBlock()?.lastChild?.text

    override val containingFile: VirtualFile?
        get() = declaration.containingFile?.virtualFile

    override fun toString() = objectName
    override fun navigate(requestFocus: Boolean) {
        val descriptor = PsiNavigationSupport.getInstance().getDescriptor(declaration)
        descriptor?.navigate(requestFocus)
    }

    override fun canNavigate() = canNavigateToSource()

    override fun canNavigateToSource() = PsiNavigationSupport.getInstance().getDescriptor(declaration) != null

    override fun getName() = objectName

    override fun getPresentation() = object : ItemPresentation {
        override fun getPresentableText() = objectName
        override fun getLocationString() = containingFile?.name

        override fun getIcon(unused: Boolean) =
            PlatformIcons.CLASS_ICON
    }
}

class ModuleObject(private val module: TamlModule) : TSCachedObject {
    companion object {
        fun fromModule(module: TamlModule) = ModuleObject(module)
    }

    override val objectName: String
        get() = module.moduleId!!

    override val type: String
        get() = "SimSet"

    override val parent: String?
        get() = "ModuleRoot"

    override val containingFile: VirtualFile
        get() = module.file


    override fun navigate(requestFocus: Boolean) {
        val file = PsiManager.getInstance(module.project).findFile(containingFile)
        val descriptor = file?.let { PsiNavigationSupport.getInstance().getDescriptor(file) }
        descriptor?.navigate(requestFocus)
    }

    override fun canNavigate() = canNavigateToSource()

    override fun canNavigateToSource() = PsiManager.getInstance(module.project).findFile(containingFile)?.let { PsiNavigationSupport.getInstance().getDescriptor(it) } != null

    override fun getName() = objectName

    override fun getPresentation() = object : ItemPresentation {
        override fun getPresentableText() = objectName
        override fun getLocationString() = containingFile.name

        override fun getIcon(unused: Boolean) =
            PlatformIcons.CLASS_ICON
    }
}

abstract class CachedEngineObject : TSCachedObject {
    override val objectName: String
        get() = ""

    override val type: String
        get() = ""

    override val parent: String?
        get() = null

    override val containingFile: VirtualFile?
        get() = null

    override fun navigate(requestFocus: Boolean) = Unit

    override fun canNavigate() = false

    override fun canNavigateToSource() = false

    override fun getName() = objectName

    override fun getPresentation() = object : ItemPresentation {
        override fun getPresentableText() = objectName
        override fun getLocationString() = "defined in engine"

        override fun getIcon(unused: Boolean) =
            PlatformIcons.CLASS_ICON
    }
}

@Service
class TSTypeLookupService {
    fun getObjects(project: Project) =
        ReferenceUtil.getObjects(project)
            .asSequence()
            .map(CachedObjectDeclaration::fromDeclaration)
            .plus(
                project.getService(TamlModuleService::class.java)
                    .getModules()
                    .map(ModuleObject::fromModule)
            )
            .plus(
                object : CachedEngineObject() {
                    override val objectName: String
                        get() = "ModuleDatabase"
                    override val type: String
                        get() = "ModuleManager"
                }
            )
            .plus(
                object : CachedEngineObject() {
                    override val objectName: String
                        get() = "AssetDatabase"
                    override val type: String
                        get() = "AssetDatabase"
                }
            )
            .plus(
                object : CachedEngineObject() {
                    override val objectName: String
                        get() = "ModuleRoot"
                    override val type: String
                        get() = "SimSet"
                }
            )
            .toList()


    fun findObject(project: Project, key: String): List<TSCachedObject> =
        getObjects(project)
            .filter { it.objectName.equals(key, true) }


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