package org.lukasj.idea.torquescript.util

import com.intellij.ide.util.PsiNavigationSupport
import com.intellij.lang.xml.XMLLanguage
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.descendantsOfType
import com.intellij.psi.xml.XmlTag
import com.intellij.util.PlatformIcons
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.engine.model.EngineClass
import org.lukasj.idea.torquescript.psi.TSObjectDeclaration
import org.lukasj.idea.torquescript.reference.ReferenceUtil
import org.lukasj.idea.torquescript.taml.TamlModule
import org.lukasj.idea.torquescript.taml.TamlModuleService

interface TSCachedObject : NavigationItem {
    val objectName: String
    val type: String
    val parent: String?
    val containingFile: VirtualFile?
    val psiElement: PsiElement?
}

class CachedObjectDeclaration(private val declaration: TSObjectDeclaration) : TSCachedObject {
    companion object {
        fun fromDeclaration(declaration: TSObjectDeclaration) = CachedObjectDeclaration(declaration)
    }

    override val objectName: String
        get() = declaration.name!!

    override val type: String
        get() = declaration.getObjectTypeName().text

    override val parent: String?
        get() = declaration.getParentBlock()?.lastChild?.text

    override val containingFile: VirtualFile?
        get() = declaration.containingFile?.virtualFile

    override val psiElement: PsiElement
        get() = declaration

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

    override val parent: String
        get() = "ModuleRoot"

    override val containingFile: VirtualFile
        get() = module.file

    override val psiElement: PsiElement?
        get() = PsiManager.getInstance(module.project).findFile(containingFile)
            ?.firstChild
            ?.children
            ?.filterIsInstance<XmlTag>()
            ?.firstOrNull {
                it.getAttribute("ModuleId") != null
            }
            ?.getAttribute("ModuleId")
            ?.valueElement

    override fun navigate(requestFocus: Boolean) {
        val file = PsiManager.getInstance(module.project).findFile(containingFile)
        val descriptor = file?.let { PsiNavigationSupport.getInstance().getDescriptor(file) }
        descriptor?.navigate(requestFocus)
    }

    override fun canNavigate() = canNavigateToSource()

    override fun canNavigateToSource() = PsiManager.getInstance(module.project).findFile(containingFile)
        ?.let { PsiNavigationSupport.getInstance().getDescriptor(it) } != null

    override fun getName() = objectName

    override fun getPresentation() = object : ItemPresentation {
        override fun getPresentableText() = objectName
        override fun getLocationString() = containingFile.name

        override fun getIcon(unused: Boolean) =
            PlatformIcons.CLASS_ICON
    }
}

class EngineClassObject(private val project: Project, private val engineClass: EngineClass) : TSCachedObject {
    companion object {
        fun fromEngineClass(project: Project, engineClass: EngineClass) = EngineClassObject(project, engineClass)
    }

    var psiFile: PsiFile =
        PsiFileFactory.getInstance(project).createFileFromText(
            XMLLanguage.INSTANCE, """
                                            <?xml version="1.0" encoding="UTF-8" standalone ="yes"?>
                                            <!-- Stub XML file to allow IDEA to resolve references -->
                                            <EngineExportScope
                                                name=""
                                                docs="">
                                                <EngineClassType
                                                    name="${engineClass.name}">
                                                </EngineClassType>
                                            </EngineExportScope>
                                        """.trimIndent()
        )
    override val objectName: String
        get() = engineClass.name
    override val type: String
        get() = engineClass.name
    override val parent: String?
        get() = engineClass.superType
    override val containingFile: VirtualFile
        get() = psiFile.virtualFile
    override val psiElement: PsiElement?
        get() =
            psiFile.firstChild
                .descendantsOfType<XmlTag>()
                .mapNotNull { it.getAttribute("name")?.valueElement }
                .single { it.value == engineClass.name }

    override fun navigate(requestFocus: Boolean) {
        val file = PsiManager.getInstance(project).findFile(containingFile)
        val descriptor = file?.let { PsiNavigationSupport.getInstance().getDescriptor(file) }
        descriptor?.navigate(requestFocus)
    }

    override fun canNavigate() = canNavigateToSource()

    override fun canNavigateToSource() = PsiManager.getInstance(project).findFile(containingFile)
        ?.let { PsiNavigationSupport.getInstance().getDescriptor(it) } != null

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

    override val psiElement: PsiElement?
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

@Service(Service.Level.PROJECT)
class TSTypeLookupService(private val project: Project) {
    val cachedEngineClasses =
        CachedValuesManager.getManager(project)
            .let { cachedValuesManaged ->
                project.service<EngineApiService>()
                    .findEngineApiFile()!!
                    .let { engineApiFile ->
                        cachedValuesManaged.createCachedValue(
                            {
                                CachedValueProvider.Result.create(
                                    project.service<EngineApiService>()
                                        .getClasses()
                                        .map { engineClass ->
                                            EngineClassObject.fromEngineClass(project, engineClass)
                                        },
                                    arrayOf<Any>(engineApiFile)
                                )
                            },
                            false
                        )
                    }
            }

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
                        get() = "AssetManager"
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

    fun getEngineClasses() =
        cachedEngineClasses.value

    fun getNamespaces(project: Project) =
        project.getService(EngineApiService::class.java)
            .findEngineApiFile()!!
            .let { engineApiFile ->
                getObjects(project)
                    .plus(
                        getEngineClasses()
                    )
                    .toList()
            }

    fun findNamespace(project: Project, key: String): List<TSCachedObject> =
        getNamespaces(project)
            .filter { it.objectName.equals(key, true) }

    fun getNamespaceInheritanceList(rootNs: String, project: Project): List<String> {
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
                getNamespaceInheritanceList(superType, project)
                    .plus(rootNs)
            } else {
                logger<TSTypeLookupService>()
                    .warn("No instances of $rootNs found")
                listOf(rootNs)
            }
        }

        return if (obj[0].parent != null) {
            getNamespaceInheritanceList(obj[0].parent!!, project)
        } else {
            getNamespaceInheritanceList(obj[0].type, project)
        }.plus(rootNs)
    }
}