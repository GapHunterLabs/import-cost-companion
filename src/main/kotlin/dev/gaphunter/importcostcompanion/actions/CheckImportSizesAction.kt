package dev.gaphunter.importcostcompanion.actions

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import dev.gaphunter.importcostcompanion.model.ImportSizeEntry
import dev.gaphunter.importcostcompanion.parse.ImportExtractor
import dev.gaphunter.importcostcompanion.report.ImportSizeReportWriter
import dev.gaphunter.importcostcompanion.resolve.DirectorySizeCalculator
import dev.gaphunter.importcostcompanion.resolve.NodeModulesResolver

/**
 * Editor context-menu entry point. Deliberately on-demand only --
 * never triggered automatically on keystroke/save, which is exactly
 * what the cited competitor's real performance complaints trace back
 * to. All the real file-system work (recursive directory size
 * summation, potentially many files for a large package) runs off the
 * EDT.
 */
class CheckImportSizesAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val project = e.project
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabledAndVisible = project != null && file != null && !file.isDirectory && !DumbService.isDumb(project)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

        ApplicationManager.getApplication().executeOnPooledThread {
            val text = String(file.contentsToByteArray(), file.charset)
            val importPaths = ImportExtractor.extractBareImportPaths(text)

            if (importPaths.isEmpty()) {
                ApplicationManager.getApplication().invokeLater {
                    notify(project, "No node_modules imports found in ${file.name}.", NotificationType.INFORMATION)
                }
                return@executeOnPooledThread
            }

            val entries = importPaths.map { path ->
                val packageName = ImportExtractor.packageNameFor(path)
                val packageDir = NodeModulesResolver.findPackageDir(file, packageName)
                val size = packageDir?.let { DirectorySizeCalculator.totalSize(it) }
                ImportSizeEntry(path, packageName, size)
            }
            val report = ImportSizeReportWriter.render(entries)

            ApplicationManager.getApplication().invokeLater {
                writeReport(project, file, report)
                val notFound = entries.count { it.sizeBytes == null }
                val message = if (notFound > 0) {
                    "${entries.size} import(s) checked, $notFound not found locally -- see import-cost-report.md."
                } else {
                    "${entries.size} import(s) checked -- see import-cost-report.md."
                }
                notify(project, message, NotificationType.INFORMATION)
            }
        }
    }

    private fun writeReport(project: Project, anchorFile: VirtualFile, reportText: String) {
        val directory = PsiManager.getInstance(project).findDirectory(anchorFile.parent ?: return) ?: return
        val fileName = "import-cost-report.md"

        WriteCommandAction.runWriteCommandAction(project, "Check Import Sizes", null, {
            val existing = directory.findFile(fileName)
            val resultFile = if (existing != null) {
                PsiDocumentManager.getInstance(project).getDocument(existing)?.setText(reportText)
                existing
            } else {
                val newFile = PsiFileFactory.getInstance(project)
                    .createFileFromText(fileName, PlainTextFileType.INSTANCE.language, reportText)
                directory.add(newFile) as PsiFile
            }
            resultFile.virtualFile?.let { FileEditorManager.getInstance(project).openFile(it, true) }
        })
    }

    private fun notify(project: Project, message: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Import Cost Companion")
            .createNotification(message, type)
            .notify(project)
    }
}
