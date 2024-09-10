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

class DownloadAction(val font: Font) : AnAction(font.family) {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.getData(PlatformDataKeys.PROJECT) ?: return
        val editor = event.getData(PlatformDataKeys.EDITOR) ?: return

        val currentFile = editor.virtualFile.parent
        val targetDirectory = currentFile.path
        val fontDirectory = "$targetDirectory/${font.family}"

        val directory = File(fontDirectory)
        if (!directory.exists()) {
            directory.mkdir()
        }

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Downloading fonts", false) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = false
                indicator.text = "Downloading fonts..."

                var successCount = 0
                var failureCount = 0

                for ((variant, url) in font.files) {
                    val fileName = "${font.family}-${variant}.ttf"
                    val destination = "$fontDirectory/$fileName"
                    indicator.text2 = "Downloading $fileName"

                    if (FileDownloader.downloadFile(url, destination)) {
                        successCount++
                    } else {
                        failureCount++
                    }

                    indicator.fraction = (successCount + failureCount).toDouble() / font.files.size
                }

                if (failureCount == 0) {
                    NotificationGroupManager.getInstance()
                        .getNotificationGroup("Font Downloader")
                        .createNotification("All fonts downloaded successfully.", NotificationType.INFORMATION)
                        .notify(project)
                    VfsUtil.markDirtyAndRefresh(false, true, true, currentFile)
                } else {
                    NotificationGroupManager.getInstance()
                        .getNotificationGroup("Font Downloader")
                        .createNotification("Failed to download $failureCount fonts.", NotificationType.ERROR)
                        .notify(project)
                }
            }
        })
    }
}