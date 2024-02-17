package me.romanandr.googlefonts

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.vfs.ReadonlyStatusHandler
import me.romanandr.googlefonts.utils.GoogleApi.generateUrl

class InsertFontAction(val font: Font) : AnAction(font.family) {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.getData(PlatformDataKeys.PROJECT)
        val editor = event.getData(PlatformDataKeys.EDITOR)
        if (editor != null && project != null) {
            val currentDocument: Document = editor.document

            if (ReadonlyStatusHandler.ensureDocumentWritable(project, currentDocument)) {
                CommandProcessor.getInstance().runUndoTransparentAction {
                    ApplicationManager.getApplication().runWriteAction {
                        var target = generateUrl(font)
                        if (listOf("css", "scss", "sass", "less", "pcss").contains(editor.virtualFile.extension)) {
                            target = "@import url('${target}');"
                        } else {
                            target = "<link href=\"${target}\" rel=\"stylesheet\" />"
                        }
                        currentDocument.insertString(editor.caretModel.offset, target)
                        editor.caretModel.moveToOffset(editor.caretModel.offset + target.length)
                    }
                }
            }
        }
    }
}