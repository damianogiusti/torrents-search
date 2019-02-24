package torrentsearch.internal.remotes

import torrentsearch.internal.TorrentsApi
import torrentsearch.TorrentsRemoteSource

data class TorrentRestModel(
    var id: String? = null,
    var name: String? = null,
    var type: String? = null,
    var seeders: String? = null,
    var leechers: String? = null,
    var date: Long? = null,
    var sizeBytes: Long = 0,
    var magnetLink: String? = null
)

internal class X1337Source(
    private val torrentsApi: TorrentsApi,
    private val parser: X1337Parser
) : TorrentsRemoteSource {

    override suspend fun searchTorrents(text: String): List<TorrentRestModel> {
        val call = torrentsApi.search1337x(text)
        val results = parser.getMainInfo(call.await()).orEmpty()
        val details = results
            .mapNotNull { info ->
                val link = info.linkToDetail?.takeIf { it.isNotBlank() }
                if (link == null) null
                else torrentsApi.search1337Details(link) to info
            }
            .map { (call, info) -> call.await() to info }

        return details.mapNotNull { (detail, info) ->
            val detailInfo = parser.getDetailInfo(detail)
            if (detailInfo != null) {
                TorrentRestModel(
                    id = detailInfo.id,
                    name = info.name,
                    type = "", // TODO
                    seeders = info.seedersCount,
                    leechers = info.leechersCount,
                    date = parseFormattedDate(info.date).time,
                    sizeBytes = getSizeBytes(info.size),
                    magnetLink = detailInfo.magnetLink
                )
            } else null
        }
    }
}