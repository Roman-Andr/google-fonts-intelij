package me.romanandr.googlefonts.action.insert

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import me.romanandr.googlefonts.model.Font

class InsertActionGroup(val items: List<Font>) : ActionGroup() {
    override fun getChildren(event: AnActionEvent?): Array<AnAction> {
        val actions = mutableListOf<AnAction>()
        for (x in items) {
            actions.add(InsertAction(x))
        }
        return actions.toTypedArray()
    }
}