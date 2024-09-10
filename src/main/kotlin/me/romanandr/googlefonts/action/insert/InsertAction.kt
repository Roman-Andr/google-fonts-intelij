package me.romanandr.googlefonts.action.insert

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.vfs.ReadonlyStatusHandler
import me.romanandr.googlefonts.api.FontService.generateUrl
import me.romanandr.googlefonts.model.Font

class InsertAction(val font: Font) : AnAction(font.family) {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.getData(PlatformDataKeys.PROJECT) ?: return
        val editor = event.getData(PlatformDataKeys.EDITOR) ?: return

        val currentDocument: Document = editor.document

        if (ReadonlyStatusHandler.ensureDocumentWritable(project, currentDocument)) {
            CommandProcessor.getInstance().runUndoTransparentAction {
                ApplicationManager.getApplication().runWriteAction {
                    var target = generateUrl(font)
                    target = if (listOf("css", "scss", "sass", "less", "pcss").contains(editor.virtualFile.extension)) {
                        "@import url('${target}');"
                    } else {
                        "<link href=\"${target}\" rel=\"stylesheet\" />"
                    }
                    currentDocument.insertString(editor.caretModel.offset, target)
                    editor.caretModel.moveToOffset(editor.caretModel.offset + target.length)
                }
            }
        }
    }
}