package me.romanandr.googlefonts.action.download

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import me.romanandr.googlefonts.model.Font

class DownloadActionGroup(val items: List<Font>) : ActionGroup() {
    override fun getChildren(event: AnActionEvent?): Array<AnAction> {
        val actions = mutableListOf<AnAction>()
        for (x in items) {
            actions.add(DownloadAction(x))
        }
        return actions.toTypedArray()
    }
}
