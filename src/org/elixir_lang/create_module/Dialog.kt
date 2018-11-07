package org.elixir_lang.create_module

import com.google.common.annotations.VisibleForTesting
import com.google.common.base.CaseFormat
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.actions.TemplateKindCombo
import com.intellij.lang.LangBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidatorEx
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.psi.PsiDirectory
import org.elixir_lang.Icons
import org.elixir_lang.psi.ElixirFile
import org.junit.Test

const val NEW_ELIXIR_MODULE = "New Elixir Module"
const val DESCRIPTION = "Nested Aliases, like Foo.Bar.Baz, are created in subdirectory for the " + "parent Aliases, foo/bar/baz.ex"

private const val EXTENSION = ".ex"

fun ancestorDirectoryNamesBaseNamePair(moduleName: String): Pair<List<String>, String> {
    val directoryList: MutableList<String>
    val lastAlias: String

    if (moduleName.contains(".")) {
        val aliases = moduleName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val directoryListSize = aliases.size - 1
        directoryList = mutableListOf()

        for (i in 0 until directoryListSize) {
            val alias = aliases[i]
            val subdirectoryName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, alias)
            directoryList.add(subdirectoryName)
        }

        lastAlias = aliases[aliases.size - 1]
    } else {
        directoryList = mutableListOf()
        lastAlias = moduleName
    }

    val basename = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, lastAlias) + EXTENSION

    return directoryList to basename
}

class Dialog(private val project: Project, val directory: PsiDirectory) : CreateFileFromTemplateDialog(project), InputValidatorEx {
    init {
        title = NEW_ELIXIR_MODULE

        kindCombo.apply {
            addItem("Empty module", Icons.FILE, "Elixir Module")
            addItem("Application", Icons.File.APPLICATION, "Elixir Application")
            addItem("Supervisor", Icons.File.SUPERVISOR, "Elixir Supervisor")
            addItem("GenServer", Icons.File.GEN_SERVER, "Elixir GenServer")
            addItem("GenEvent", Icons.File.GEN_EVENT, "Elixir GenEvent")
        }

        setTemplateKindComponentsVisible(true)
    }

    var created: ElixirFile? = null
        private set

    override fun doValidate(): ValidationInfo? {
        val text = nameField.text.trim()
        val canClose = canClose(text)

        return if (!canClose) {
            val errorText = getErrorText(text) ?: LangBundle.message("incorrect.name")!!

            ValidationInfo(errorText, nameField)
        } else {
            super.doValidate()
        }
    }

    @VisibleForTesting
    public override fun doOKAction() {
        val created = org.elixir_lang.create_module.ElementCreator(project, directory, kindCombo.selectedName).tryCreate(getEnteredName())

        if (created.isNotEmpty()) {
            this.created = created.single() as ElixirFile
            super.doOKAction()
        }
    }

    @VisibleForTesting
    internal var name: String
      get() = nameField.text.trim()
      set(newName) {
          nameField.text = newName.trim()
      }

    @VisibleForTesting
    internal var templateName: String
      get() = kindCombo.selectedName
      set(newTemplateName) {
          kindCombo.setSelectedName(newTemplateName)
      }

    private fun getEnteredName(): String {
        val text = nameField.text.trim()
        nameField.text = text
        return text
    }

    override fun canClose(inputString: String): Boolean =
        !inputString.isBlank() && getErrorText(inputString) == null

    override fun checkInput(inputString: String): Boolean =
        checkFormat(inputString) && checkDoesNotExist(inputString)

    override fun getErrorText(inputString: String): String? =
        if (!inputString.isEmpty()) {
            if (!checkFormat(inputString)) {
                String.format(INVALID_MODULE_MESSAGE_FMT, inputString)
            } else if (!checkDoesNotExist(inputString)) {
                val fullPath = fullPath(ancestorDirectoryNamesBaseNamePair(inputString))
                String.format(EXISTING_MODULE_MESSAGE_FMT, fullPath)
            } else {
                null
            }
        } else {
            "Module name cannot be empty"
        }

    private fun checkFormat(inputString: String): Boolean = MODULE_NAME_REGEX.matches(inputString)

    private fun checkDoesNotExist(moduleName: String): Boolean {
        val ancestorDirectoryNamesBaseNamePair = ancestorDirectoryNamesBaseNamePair(
                moduleName
        )
        val ancestorDirectoryNames = ancestorDirectoryNamesBaseNamePair.first
        var currentDirectory: PsiDirectory? = directory
        var doesNotExists = false

        for (ancestorDirectoryName in ancestorDirectoryNames) {
            val subdirectory = currentDirectory!!.findSubdirectory(ancestorDirectoryName)

            if (subdirectory == null) {
                doesNotExists = true

                break
            }

            currentDirectory = subdirectory
        }

        // if all the directories exist
        if (!doesNotExists) {
            val baseName = ancestorDirectoryNamesBaseNamePair.second
            doesNotExists = currentDirectory!!.findFile(baseName) == null
        }

        return doesNotExists
    }
    private fun fullPath(ancestorDirectoryNamesBaseNamePair: Pair<List<String>, String>): String =
            directory.virtualFile.canonicalPath + "/" + path(ancestorDirectoryNamesBaseNamePair)

}
private const val INVALID_MODULE_MESSAGE_FMT = "'%s' is not a valid Elixir module name. Elixir module " +
                "names should be a dot-separated-sequence of alphanumeric (and underscore) Aliases, each starting with a " +
                "capital letter. " + DESCRIPTION
private const val EXISTING_MODULE_MESSAGE_FMT = "'%s' already exists"
private val ALIAS_REGEX = Regex("[A-Z][0-9a-zA-Z_]*")
private val MODULE_NAME_REGEX = Regex("$ALIAS_REGEX(\\.$ALIAS_REGEX)*")

private fun path(ancestorDirectoryNamesBaseNamePair: Pair<List<String>, String>): String {
    val (ancestorDirectoryNames, baseName) = ancestorDirectoryNamesBaseNamePair
    val directoryPath = ancestorDirectoryNames.joinToString("/")

    return directoryPath + baseName
}
