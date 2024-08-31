package me.romanandr.googlefonts.utils

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Paths

object FileDownloader {
    fun downloadFile(url: String, destination: String): Boolean {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build()

        try {
            val response = client.send(request, HttpResponse.BodyHandlers.ofByteArray())
            Files.write(Paths.get(destination), response.body())
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
