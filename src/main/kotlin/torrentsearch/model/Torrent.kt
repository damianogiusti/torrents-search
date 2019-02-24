package torrentsearch.model

data class Torrent(
    val id: String,
    val name: String,
    val type: String,
    val seeders: Long,
    val leechers: Long,
    val date: Long,
    val size: Long,
    val magnetLink: String
)