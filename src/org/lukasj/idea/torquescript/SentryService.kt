package org.lukasj.idea.torquescript

import com.intellij.ide.plugins.PluginManager
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.idea.IdeaLogger
import com.intellij.openapi.components.Service
import com.intellij.openapi.extensions.PluginId
import io.sentry.Hub
import io.sentry.SentryLevel
import io.sentry.SentryOptions
import java.util.*

@Service(Service.Level.APP)
class SentryService {
    val hub: Hub

    init {
        val options = SentryOptions()
        options.dsn = "https://4758406b81314cfd81e0dbc6f6582b5a@o1161252.ingest.sentry.io/6247137"
        options.tracesSampleRate = 1.0
        options.setDebug(true)
        options.release =
            "org.lukasj.idea.torquescript@" + (PluginManagerCore.getPlugin(PluginId.getId("org.lukasj.idea.torquescript"))?.version
                ?: "unknown")
        options.environment = System.getProperty("os.name")
        hub = Hub(options)
        val tsPlugin =
            PluginManager
                .getInstance()
                .findEnabledPlugin(PluginId.getId("org.lukasj.idea.torquescript"))

        if (tsPlugin != null) {
            hub.setExtra("version", "${tsPlugin.name}@${tsPlugin.version}")
        }

        val correlationId = UUID.randomUUID()
        hub.setExtra("correlation-id", correlationId.toString())
        // might be useful to debug the exception
        hub.setExtra("last_action", IdeaLogger.ourLastActionId)
        hub.setLevel(SentryLevel.ERROR)
    }

    companion object {
        fun getHub(): Hub {
            return SentryService().hub
        }
    }
}
