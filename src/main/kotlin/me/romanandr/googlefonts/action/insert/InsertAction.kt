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

class InsertAction(private val fonts: List<Font>) : AnAction() {
    constructor(font: Font) : this(listOf(font))

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.getData(PlatformDataKeys.PROJECT) ?: return
        val editor = event.getData(PlatformDataKeys.EDITOR) ?: return

        val currentDocument: Document = editor.document

        if (ReadonlyStatusHandler.ensureDocumentWritable(project, currentDocument)) {
            CommandProcessor.getInstance().runUndoTransparentAction {
                ApplicationManager.getApplication().runWriteAction {
                    val extension = editor.virtualFile?.extension
                    val isCssLike = listOf("css", "scss", "sass", "less", "pcss").contains(extension)

                    val caretOffset = editor.caretModel.offset
                    val lineNumber = currentDocument.getLineNumber(caretOffset)
                    val lineStartOffset = currentDocument.getLineStartOffset(lineNumber)
                    val lineText = currentDocument.text.substring(lineStartOffset, caretOffset)
                    val indent = lineText.takeWhile { it.isWhitespace() }

                    val targets = fonts.mapIndexed { index, font ->
                        val url = generateUrl(font)
                        val fontLine =
                            if (isCssLike) "@import url('$url');" else "<link href=\"$url\" rel=\"stylesheet\" />"
                        if (index == 0) fontLine else indent + fontLine
                    }.joinToString("\n")

                    currentDocument.insertString(caretOffset, targets)
                    editor.caretModel.moveToOffset(caretOffset + targets.length)
                }
            }
        }
    }
}