package me.romanandr.googlefonts.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.CheckBoxList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import me.romanandr.googlefonts.model.Font
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*

class FontSelector(project: Project, allFonts: List<Font>) : DialogWrapper(project, true) {
    private val checkBoxList = CheckBoxList<Font>()
    private val searchField = JBTextField()
    private val filteredFonts = mutableListOf<Font>()
    private val selectedFonts = mutableSetOf<Font>()
    private lateinit var scrollPane: JBScrollPane
    private val sortedAllFonts: List<Font>

    init {
        title = "Select Fonts"
        setSize(400, 500)
        sortedAllFonts = allFonts.sortedBy { it.family.lowercase() }
        filteredFonts.addAll(sortedAllFonts)
        init()
        checkBoxList.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        checkBoxList.setCheckBoxListListener { index, value ->
            val font = checkBoxList.getItemAt(index)
            if (font != null) {
                if (value) selectedFonts.add(font) else selectedFonts.remove(font)
            }
        }
        updateList()
        setupSearch()
        setupEnterKeyBinding()

        SwingUtilities.invokeLater {
            searchField.requestFocusInWindow()
        }
    }

    private fun setupSearch() {
        searchField.emptyText.text = "Search fonts..."
        searchField.addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent?) {
                filterFonts()
            }
        })
    }

    private fun setupEnterKeyBinding() {
        val inputMap = checkBoxList.getInputMap(JComponent.WHEN_FOCUSED)
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "toggleCheckbox")
        checkBoxList.actionMap.put("toggleCheckbox", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                toggleSelectedFont()
            }
        })
    }

    private fun toggleSelectedFont() {
        val selectedIndices = checkBoxList.selectedIndices
        if (selectedIndices.isNotEmpty()) {
            selectedIndices.forEach { index ->
                val font = checkBoxList.getItemAt(index)
                if (font != null && !selectedFonts.contains(font)) {
                    checkBoxList.setItemSelected(font, true)
                    selectedFonts.add(font)
                }
            }
            checkBoxList.repaint()
        }
    }

    private fun filterFonts() {
        val query = searchField.text.trim().lowercase()
        filteredFonts.clear()
        filteredFonts.addAll(
            if (query.isEmpty()) sortedAllFonts
            else sortedAllFonts.filter { it.family.lowercase().contains(query) }
                .sortedBy { it.family.lowercase() }
        )
        updateList()
    }

    private fun clearAllSelections() {
        selectedFonts.clear()
        updateList()
    }

    private fun updateList() {
        checkBoxList.clear()
        filteredFonts.forEach { font ->
            checkBoxList.addItem(font, font.family, selectedFonts.contains(font))
        }
        checkBoxList.revalidate()
        checkBoxList.repaint()
        scrollPane.revalidate()
        scrollPane.repaint()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout())
        searchField.preferredSize = Dimension(350, 30)
        panel.add(searchField, BorderLayout.NORTH)
        checkBoxList.minimumSize = Dimension(350, 500)
        scrollPane = JBScrollPane(
            checkBoxList,
            JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        )
        scrollPane.preferredSize = Dimension(350, 500)
        scrollPane.minimumSize = Dimension(350, 500)
        scrollPane.viewport.view = checkBoxList
        panel.add(scrollPane, BorderLayout.CENTER)
        return panel
    }

    override fun createSouthPanel(): JComponent {
        val defaultPanel = super.createSouthPanel() as JPanel
        val clearAllButton = JButton("Clear All")
        clearAllButton.addActionListener { clearAllSelections() }

        val leftPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        leftPanel.add(clearAllButton)

        val rightPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
        defaultPanel.components.forEach { rightPanel.add(it) }

        val customPanel = JPanel(BorderLayout())
        customPanel.add(leftPanel, BorderLayout.WEST)
        customPanel.add(rightPanel, BorderLayout.EAST)

        return customPanel
    }

    fun getSelectedFonts(): List<Font> = selectedFonts.toList()

    override fun getPreferredFocusedComponent() = searchField
}