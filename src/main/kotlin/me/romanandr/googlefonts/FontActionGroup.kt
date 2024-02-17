package me.romanandr.googlefonts

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class FontActionGroup(val fonts: List<Font>) : ActionGroup() {
    override fun getChildren(event: AnActionEvent?): Array<AnAction> {
        val actions = mutableListOf<AnAction>()
        for (x in fonts) {
            actions.add(InsertFontAction(x))
        }
        return actions.toTypedArray()
    }
}