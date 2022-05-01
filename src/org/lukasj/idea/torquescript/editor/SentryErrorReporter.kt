package org.lukasj.idea.torquescript.editor

import com.intellij.diagnostic.AbstractMessage
import com.intellij.diagnostic.IdeaReportingEvent
import com.intellij.ide.DataManager
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.idea.IdeaLogger
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.util.Consumer
import io.sentry.Hub
import io.sentry.Sentry
import io.sentry.SentryLevel
import io.sentry.SentryOptions
import java.awt.Component
import java.util.*


class SentryErrorReporter : ErrorReportSubmitter() {
    private val hub: Hub

    override fun getPrivacyNoticeText() =
        "The error report will be shared with Lukas Peter Aldershaab through the SaaS Sentry."

    override fun getReportActionText() =
        "Report to Author"

    override fun getReporterAccount() = "user-id"

    override fun submit(
        events: Array<out IdeaLoggingEvent>,
        additionalInfo: String?,
        parentComponent: Component,
        consumer: Consumer<in SubmittedReportInfo>
    ): Boolean {
        val context = DataManager.getInstance().getDataContext(parentComponent)
        val project: Project? = CommonDataKeys.PROJECT.getData(context)

        object : Task.Backgroundable(project, "Sending error report") {
            override fun run(indicator: ProgressIndicator) {
                hub.setLevel(SentryLevel.ERROR)
                val descriptor = pluginDescriptor
                if (descriptor is IdeaPluginDescriptor) {
                    hub.setExtra("version", "${descriptor.name}@${descriptor.version}")
                }

                val correlationId = UUID.randomUUID()
                hub.setExtra("correlation-id", correlationId.toString())
                // might be useful to debug the exception
                hub.setExtra("last_action", IdeaLogger.ourLastActionId)

                events
                    .filterIsInstance<IdeaReportingEvent>()
                    .forEach {
                        hub.captureException(it.data.throwable)
                    }

                ApplicationManager.getApplication().invokeLater {

                    // we're a bit lazy here.
                    // Alternatively, we could add a listener to the sentry client
                    // to be notified if the message was successfully send
                    Messages.showInfoMessage(parentComponent, "Thank you for submitting your report!", "Error Report")
                    consumer.consume(SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.NEW_ISSUE))
                }
            }
        }.queue()
        return true
    }

    init {
        val options = SentryOptions()
        options.dsn = "https://4758406b81314cfd81e0dbc6f6582b5a@o1161252.ingest.sentry.io/6247137"
        options .tracesSampleRate = 1.0
        options.setDebug(true)
        options.release = "org.lukasj.idea.torquescript@" + (PluginManagerCore.getPlugin(PluginId.getId("org.lukasj.idea.torquescript"))?.version ?: "unknown")
        options.environment = System.getProperty("os.name")
        hub = Hub(options)
    }
}