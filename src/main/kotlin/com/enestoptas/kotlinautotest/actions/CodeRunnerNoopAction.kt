package com.enestoptas.kotlinautotest.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class CodeRunnerNoopAction(private val block: () -> Unit) : AnAction() {
    override fun actionPerformed(e: AnActionEvent) = block()
}
