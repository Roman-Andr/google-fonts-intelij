package me.romanandr.googlefonts.action.download

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.vfs.VfsUtil
import me.romanandr.googlefonts.model.Font
import me.romanandr.googlefonts.utils.FileDownloader
import java.io.File

class DownloadAction(val fonts: List<Font>) : AnAction() {
    constructor(font: Font) : this(listOf(font))

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.getData(PlatformDataKeys.PROJECT) ?: return
        val editor = event.getData(PlatformDataKeys.EDITOR) ?: return

        val currentFile = editor.virtualFile.parent
        val fontsDirectory = File(currentFile.path, "Fonts")
        if (!fontsDirectory.exists()) {
            fontsDirectory.mkdir()
        }
        val targetDirectory = fontsDirectory.path

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Downloading fonts", false) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = false
                indicator.text = "Downloading fonts..."

                var totalSuccessCount = 0
                var totalFailureCount = 0
                val totalFiles = fonts.sumOf { it.files.size }
                var processedFiles = 0

                for (font in fonts) {
                    val fontDirectory = "$targetDirectory"
                    val directory = File(fontDirectory)
                    if (!directory.exists()) {
                        directory.mkdir()
                    }

                    for ((variant, url) in font.files) {
                        val fileName = "${font.family}-${variant}.ttf"
                        val destination = "$fontDirectory/$fileName"
                        indicator.text2 = "Downloading $fileName"

                        if (FileDownloader.downloadFile(url, destination)) {
                            totalSuccessCount++
                        } else {
                            totalFailureCount++
                        }

                        processedFiles++
                        indicator.fraction = processedFiles.toDouble() / totalFiles
                    }
                }

                if (totalFailureCount == 0) {
                    NotificationGroupManager.getInstance()
                        .getNotificationGroup("Font Downloader")
                        .createNotification("All fonts downloaded successfully.", NotificationType.INFORMATION)
                        .notify(project)
                    VfsUtil.markDirtyAndRefresh(false, true, true, currentFile)
                } else {
                    NotificationGroupManager.getInstance()
                        .getNotificationGroup("Font Downloader")
                        .createNotification("Failed to download $totalFailureCount fonts.", NotificationType.ERROR)
                        .notify(project)
                }
            }
        })
    }
}