package me.romanandr.googlefonts.utils

import me.romanandr.googlefonts.Font

object GoogleApi {
    fun generateUrl(font: Font): String {
        return "https://fonts.googleapis.com/css?family=${font.family.replace(" ", "+")}:${font.variants.joinToString(",")}&display=swap"
    }
}