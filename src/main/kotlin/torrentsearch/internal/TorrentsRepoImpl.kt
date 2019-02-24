package torrentsearch.internal

import torrentsearch.TorrentsRemoteSource
import torrentsearch.TorrentsRepository
import torrentsearch.model.Torrent
import torrentsearch.internal.remotes.TorrentRestModel

internal class TorrentsRepoImpl(
    private val source: TorrentsRemoteSource
) : TorrentsRepository {

    override suspend fun getTorrents(name: String): List<Torrent> {
        val torrents = source.searchTorrents(name)
        return torrents.mapNotNull(::mapToTorrent)
    }
}

private fun mapToTorrent(torrent: TorrentRestModel): Torrent? {
    return Torrent(
        id = torrent.id ?: return null,
        name = torrent.name ?: return null,
        type = torrent.type ?: "",
        date = torrent.date ?: return null,
        size = torrent.sizeBytes,
        seeders = torrent.seeders?.toLongOrNull() ?: 0,
        leechers = torrent.leechers?.toLongOrNull() ?: 0,
        magnetLink = torrent.magnetLink ?: return null
    )
}