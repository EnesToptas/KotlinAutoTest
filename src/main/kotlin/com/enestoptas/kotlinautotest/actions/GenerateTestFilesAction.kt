package com.enestoptas.kotlinautotest.actions

import com.enestoptas.kotlinautotest.helper.FileHelper
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.core.util.toPsiDirectory
import org.jetbrains.kotlin.idea.util.sourceRoots
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile

class GenerateTestFilesAction(private val clazz: KtClass) : AnAction() {

    override fun actionPerformed(p0: AnActionEvent) {
        generateTestFile(clazz)
    }

    private fun generateTestFile(ktClass: KtClass) {
        val testClassContent = java.lang.StringBuilder()

        // Package declaration
        val project = ktClass.project
        val ktFile = ktClass.containingFile as KtFile
        val directory = findTestDirectory(ktClass, ktFile) ?: return

        val className = ktClass.name ?: return
        val testClassName = FileHelper.getAvailableClassName(directory, className)
        val fileName = testClassName + "Test.kt"

        val packageName: String = ktFile.packageName
        if (packageName.isNotEmpty()) {
            testClassContent.append("package ").append(packageName).append("\n\n")
        }

        // Imports
        listOf(
            "import io.mockk.impl.annotations.InjectMockKs\n",
            "import io.mockk.impl.annotations.RelaxedMockK\n",
            "import io.mockk.junit5.MockKExtension\n",
            "import org.junit.jupiter.api.extension.ExtendWith\n",
            "import org.junit.jupiter.api.Test\n",
            "import kotlinx.coroutines.runBlocking\n",
        ).forEach(testClassContent::append)

        // Class declaration

        testClassContent.append("\n")
        testClassContent.append("@ExtendWith(MockKExtension::class)\n")
        testClassContent.append("class ").append(testClassName).append("Test {\n\n")

        // Add tested class
        val classFieldName = getLastWord(className).lowercase()
        testClassContent.append("    @InjectMockKs\n")
        testClassContent.append("    lateinit var ")
            .append(classFieldName).append(": ")
            .append(className).append("\n\n")

        // Generate mocked fields
        for (field in ktClass.primaryConstructorParameters) {
            testClassContent.append("    @RelaxedMockK\n")
            testClassContent.append("    lateinit var ")
                .append(field.name).append(": ")
                .append(field.typeReference?.text).append("\n\n")
        }

        // Example test method
        testClassContent.append("    @Test\n")
        testClassContent.append("    fun testSomething() = runBlocking {\n")
        testClassContent.append("        // TODO: Write your test logic here\n")
        testClassContent.append("    }\n")

        // Close class declaration
        testClassContent.append("}\n")

        // Create the test file

        val factory = PsiFileFactory.getInstance(project)
        val testFile: PsiFile =
            factory.createFileFromText(fileName, KotlinFileType.INSTANCE, testClassContent.toString())

        WriteCommandAction.runWriteCommandAction(project) {
            directory.add(testFile)
            val addedFile = directory.findFile(fileName) ?: return@runWriteCommandAction
            FileEditorManager.getInstance(project).openFile(addedFile.virtualFile, true, true);
        }
    }

    private fun findTestDirectory(ktClass: KtClass, ktFile: KtFile): PsiDirectory? {
        val project = ktClass.project

        val sourceRoots = ModuleUtilCore.findModuleForFile(ktFile)?.sourceRoots ?: return null

        val parentDir = ktFile.containingDirectory?.virtualFile?.path ?: return null
        val sourceDir = sourceRoots.firstOrNull { it.path.contains("main") }?.path ?: return null
        val relativeDir = parentDir.removePrefix(sourceDir)

        val testSourceDir = sourceRoots.first { it.path.contains("test") }
        val targetDirVirtualFile = FileHelper.getOrCreateDirectory(project, testSourceDir, relativeDir)

        return targetDirVirtualFile.toPsiDirectory(project)
    }

    private fun getLastWord(input: String): String {
        return input.split(Regex("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")).last()
    }
}
