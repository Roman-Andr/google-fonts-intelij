package me.romanandr.googlefonts.api

import com.google.gson.Gson
import me.romanandr.googlefonts.model.Font
import me.romanandr.googlefonts.model.FontsInfo
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object FontService {
    fun generateUrl(font: Font): String {
        return "https://fonts.googleapis.com/css?family=${
            font.family.replace(
                " ",
                "+"
            )
        }:${font.variants.joinToString(",")}&display=swap"
    }

    fun fetchFonts(): FontsInfo {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://www.googleapis.com/webfonts/v1/webfonts?sort=trending&key=${System.getenv("api_key")}"))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return Gson().fromJson(response.body(), FontsInfo::class.java)
    }
}