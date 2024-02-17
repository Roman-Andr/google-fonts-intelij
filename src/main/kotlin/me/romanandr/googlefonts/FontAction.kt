import com.google.gson.Gson
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.ReadonlyStatusHandler
import me.romanandr.googlefonts.FontActionGroup
import me.romanandr.googlefonts.FontsInfo
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


class FontAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.googleapis.com/webfonts/v1/webfonts?sort=trending&key=${System.getenv("api_key")}"))
                .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val data = Gson().fromJson(response.body(), FontsInfo::class.java)
        val project = event.getData(PlatformDataKeys.PROJECT)

        if (project != null) {
            JBPopupFactory.getInstance().createActionGroupPopup("Google Fonts",
                    FontActionGroup(data.items), event.dataContext,
                    JBPopupFactory.ActionSelectionAid.SPEEDSEARCH, true)
                    .showCenteredInCurrentWindow(project)
        }
    }
}