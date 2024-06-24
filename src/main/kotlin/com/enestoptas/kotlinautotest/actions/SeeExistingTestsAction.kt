package com.enestoptas.kotlinautotest.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.testIntegration.GotoTestOrCodeAction

class SeeExistingTestsAction(private val originalEvent: AnActionEvent) : AnAction() {

    override fun actionPerformed(e: AnActionEvent) = GotoTestOrCodeAction().actionPerformed(originalEvent)
}
