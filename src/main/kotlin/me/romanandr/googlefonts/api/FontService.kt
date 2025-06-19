package me.romanandr.googlefonts.api

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.romanandr.googlefonts.model.Font
import me.romanandr.googlefonts.model.FontsInfo
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.TimeUnit

object FontService {
    private val cache = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.DAYS)
        .build<String, FontsInfo>()

    fun generateUrl(font: Font): String {
        return "https://fonts.googleapis.com/css?family=${
            font.family.replace(
                " ",
                "+"
            )
        }:${font.variants.joinToString(",")}&display=swap"
    }

    suspend fun fetchFonts(): FontsInfo = withContext(Dispatchers.IO) {
        cache.getIfPresent("fonts")?.let { return@withContext it }

        val client = HttpClient.newBuilder().build()
        val request = HttpRequest
            .newBuilder()
            .uri(URI.create("https://www.googleapis.com/webfonts/v1/webfonts?sort=trending&key=${System.getenv("api_key")}"))
            .build()
        val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join()
        val fontsInfo = Gson().fromJson(response.body(), FontsInfo::class.java)
        cache.put("fonts", fontsInfo)
        fontsInfo
    }
}