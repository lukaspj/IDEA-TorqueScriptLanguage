package org.lukasj.idea.torquescript.engine

object EngineApiUtil {
    fun stringToBool(s: String) =
        when (s.lowercase()) {
            "1" -> true
            "0" -> false
            "true" -> true
            "false" -> false
            "" -> false
            else -> throw Throwable("Unknown boolean type: $s")
        }
}