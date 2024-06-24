package com.enestoptas.kotlinautotest.notification

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import javax.swing.Icon

class NotificationWrappedAction(
    private val wrappedAction: AnAction,
    text: String? = null,
    description: String? = null,
    icon: Icon? = null,
) : AnAction(text, description, icon) {

    override fun actionPerformed(p0: AnActionEvent) = wrappedAction.actionPerformed(p0)
}
