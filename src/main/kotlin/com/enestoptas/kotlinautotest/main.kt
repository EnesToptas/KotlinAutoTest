package com.enestoptas.kotlinautotest

import com.enestoptas.kotlinautotest.actions.GenerateTestFilesAction
import com.enestoptas.kotlinautotest.helper.ClassHelper
import com.enestoptas.kotlinautotest.notification.NotificationManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.kotlin.psi.KtClass

class GenerateTestAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val ktClass = ClassHelper.getClassFromAction(event) ?: return

        val existingTests = ClassHelper.getTestsOfClass(ktClass)
        if (existingTests.isNotEmpty()) return showPopup(ktClass, event)

        GenerateTestFilesAction(ktClass).actionPerformed(event)
    }

    private fun showPopup(ktClass: KtClass, event: AnActionEvent) {
        NotificationManager.shopWarningNotification(ktClass.project, ktClass, event)
    }
}
