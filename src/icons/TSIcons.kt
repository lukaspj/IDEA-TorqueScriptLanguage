package icons

import com.intellij.openapi.util.IconLoader

class TSIcons {
    companion object {
        @JvmField
        val FILE = IconLoader.getIcon("/icons/file.png", TSIcons::class.java)
        @JvmField
        val TAML = IconLoader.getIcon("/icons/taml.png", TSIcons::class.java)
        @JvmField
        val MODULE = IconLoader.getIcon("/icons/taml.png", TSIcons::class.java)
    }
}