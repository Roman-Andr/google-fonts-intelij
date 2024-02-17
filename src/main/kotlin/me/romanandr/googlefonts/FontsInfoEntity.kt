package me.romanandr.googlefonts

data class FontsInfo(
    val kind: String,
    val items: List<Font>
)

data class Font(
    val family: String,
    val variants: List<String>,
    val subsets: List<String>,
    val version: String,
    val lastModified: String,
    val files: Map<String, String>,
    val category: String,
    val kind: String,
    val menu: String
)