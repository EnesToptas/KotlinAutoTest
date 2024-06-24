package com.enestoptas.kotlinautotest.notification

import com.enestoptas.kotlinautotest.actions.CodeRunnerNoopAction
import com.enestoptas.kotlinautotest.actions.GenerateTestFilesAction
import com.enestoptas.kotlinautotest.actions.SeeExistingTestsAction
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.psi.KtClass

object NotificationManager {

    @JvmStatic
    fun shopWarningNotification(project: Project, ktClass: KtClass, event: AnActionEvent) {
        val notificationGroup = NotificationGroup(
            displayId = "Kotlin Auto Test",
            displayType = NotificationDisplayType.BALLOON,
            isLogByDefault = true
        )

        val notification = notificationGroup.createNotification(
            title = "Test file exists",
            content = "Tests for the class ${ktClass.name} already exist." +
                    " Do you still want to create a new test file?",
            type = NotificationType.INFORMATION,
            listener = null
        )

        notification.run {
            addAction(NotificationWrappedAction(SeeExistingTestsAction(event), "View..."))
            addAction(NotificationWrappedAction(GenerateTestFilesAction(ktClass), "Create anyway"))
            addAction(NotificationWrappedAction(CodeRunnerNoopAction { notification.expire() }, "Cancel"))
            isSuggestionType = true
            isImportant = true
            notify(project)
        }
    }
}

