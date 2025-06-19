package me.romanandr.googlefonts.action.insert

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.romanandr.googlefonts.api.FontService
import me.romanandr.googlefonts.ui.FontSelector

class InsertFontAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.getData(PlatformDataKeys.PROJECT) ?: return

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Fetching fonts", false) {
            override fun run(indicator: ProgressIndicator) {
                indicator.text = "Fetching font data..."
                indicator.isIndeterminate = true

                CoroutineScope(Dispatchers.IO).launch {
                    val data = FontService.fetchFonts()
                    withContext(Dispatchers.Main) {
                        if (data.items == null) return@withContext

                        val dialog = FontSelector(project, data.items)
                        if (dialog.showAndGet()) {
                            val selectedFonts = dialog.getSelectedFonts()
                            if (selectedFonts.isNotEmpty()) {
                                ApplicationManager.getApplication().invokeLater({
                                    InsertAction(selectedFonts).actionPerformed(event)
                                }, project.disposed)
                            }
                        }
                    }
                }
            }
        })
    }
}