package torrentsearch

import torrentsearch.model.Torrent

internal interface TorrentsRepository {
    suspend fun getTorrents(name: String): List<Torrent>
}