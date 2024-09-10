package me.romanandr.googlefonts.action.download

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.ui.popup.JBPopupFactory
import me.romanandr.googlefonts.api.FontService

class DownloadFontAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val data = FontService.fetchFonts()
        val project = event.getData(PlatformDataKeys.PROJECT) ?: return

        JBPopupFactory.getInstance().createActionGroupPopup(
            "Google Fonts",
            DownloadActionGroup(data.items), event.dataContext,
            JBPopupFactory.ActionSelectionAid.SPEEDSEARCH, true
        ).showCenteredInCurrentWindow(project)
    }
}