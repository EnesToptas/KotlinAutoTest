package com.enestoptas.kotlinautotest.helper

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testIntegration.TestFinderHelper
import org.jetbrains.kotlin.psi.KtClass

object ClassHelper {

    fun getClassFromAction(e: AnActionEvent): KtClass? {
        val psiFile: PsiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return null
        val element: PsiElement? = psiFile.findElementAt(e.getData(CommonDataKeys.CARET)?.offset!!)
        val psiClass: KtClass = PsiTreeUtil.getParentOfType(element, KtClass::class.java) ?: return null

        return psiClass
    }

    fun getTestsOfClass(ktClass: KtClass): List<PsiFile> {
        val tests = TestFinderHelper.findTestsForClass(ktClass)
        if (tests.isEmpty()) return emptyList()

        return tests.map { it.containingFile }
    }
}
