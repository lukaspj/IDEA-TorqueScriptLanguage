package org.lukasj.idea.torquescript.editor

import com.intellij.diagnostic.IdeaReportingEvent
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.util.Consumer
import org.lukasj.idea.torquescript.SentryService
import java.awt.Component
import java.util.*


class SentryErrorReporter : ErrorReportSubmitter() {
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
        val project: Project = CommonDataKeys.PROJECT.getData(context) ?: return false

        object : Task.Backgroundable(project, "Sending error report") {
            override fun run(indicator: ProgressIndicator) {
                val hub = project.getService(SentryService::class.java).getHub()

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
}