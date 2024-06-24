package com.enestoptas.kotlinautotest.helper

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import kotlin.random.Random

object FileHelper {

    fun getOrCreateDirectory(project: Project, base: VirtualFile, relativePath: String): VirtualFile {
        val result = base.findFileByRelativePath(relativePath)
        if (result != null) return result

        var source: VirtualFile = base
        val directories = relativePath.split("/")
        directories.forEach { dir ->
            var tmp = source.findFileByRelativePath(dir)
            if (tmp == null) WriteCommandAction.runWriteCommandAction(project) {
                tmp = source.createChildDirectory(null, dir)
            }
            source = tmp!!
        }
        return source
    }

    fun getAvailableClassName(directory: PsiDirectory, className: String): String {
        val existingFileNames = directory.files.map { it.name }.filter { it.contains(className) }.toSet()
        if (existingFileNames.isEmpty()) return className

        repeat(10) {
            val cnt = it + 2
            val candidateClassName =  "${className}$cnt"
            val candidateFileName =  "${className}${cnt}Test.kt"
            if (candidateFileName !in existingFileNames) return candidateClassName
        }

        return "${className}${Random.nextInt(100, 200)}"
    }
}
