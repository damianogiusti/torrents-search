package torrentsearch

import torrentsearch.internal.remotes.TorrentRestModel

internal interface TorrentsRemoteSource {
    suspend fun searchTorrents(text: String): List<TorrentRestModel>
}