package org.lukasj.idea.torquescript.symbols

import com.intellij.openapi.project.Project

class TSCachedList<T>(generator: TSCachedListGenerator<T>) {
    private var items: Collection<T> = listOf()
    private val function: TSCachedListGenerator<T> = generator
    private var lastUpdate: Long = 0

    /**
     * Get a list of all symbols in the project. This list is cached and updated every few seconds
     * so you don't have to find all the symbols for every function call.
     * @param project Containing project in which to search
     * @return A list of all symbol declarations
     */
    fun getList(project: Project): Collection<T> {
        //Cache is still warm, use it instead of searching
        if (!updateOnNext) {
            return items
        }
        //Need to regenerate cache... do this part outside the synchronize because it's slow
        val updated: Collection<T> = function.generate(project)

        //Update safely
        synchronized(LOCK) {
            lastUpdate = System.nanoTime()
            items = updated
            return items
        }
    }//If the cache has existed for long enough we should probably regenerate it//Need to synchronize this in case we update cache while something is accessing the symbols

    /**
     * Easy way to tell if the next call to getList() will regenerate the list
     * @return If the call will generate
     */
    private val updateOnNext: Boolean
        get() {
            //Need to synchronize this in case we update cache while something is accessing the symbols
            synchronized(LOCK) {
                var needUpdate = false

                //If the cache has existed for long enough we should probably regenerate it
                if (items.isEmpty()) {
                    needUpdate = true
                } else {
                    if (System.nanoTime() - lastUpdate > CACHE_LIFETIME) {
                        needUpdate = true
                    }
                }
                return needUpdate
            }
        }

    fun setDirty() {
        lastUpdate = 0
    }

    companion object {
        private const val CACHE_LIFETIME = 15 *  /* ns */1000000000L
        private const val LOCK = "Probably slow"
    }

}