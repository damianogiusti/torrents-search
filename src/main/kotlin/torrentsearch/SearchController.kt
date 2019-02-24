@file:Suppress("FunctionName")

package torrentsearch

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import torrentsearch.internal.TorrentsRepoImpl
import torrentsearch.internal.remotes.X1337Parser
import torrentsearch.internal.remotes.X1337Source
import torrentsearch.model.Torrent

fun SearchController(): SearchController {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://127.0.0.1")
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
    return SearchController(
        torrentsRepo = TorrentsRepoImpl(
            source = X1337Source(retrofit.create(), X1337Parser())
        )
    )
}

class SearchController internal constructor(
    private val torrentsRepo: TorrentsRepository
) {

    suspend fun searchTorrents(name: String?) = try {
        if (name == null) {
            Response(400, SearchTorrentsResponse.Error("Missing name parameter"))
        } else {
            val result = torrentsRepo.getTorrents(name)
            Response(200, SearchTorrentsResponse.Success(result))
        }
    } catch (ex: Exception) {
        Response(500, SearchTorrentsResponse.Error(ex.message ?: "Error getting search results"))
    }
}

sealed class SearchTorrentsResponse {
    data class Success(val torrents: List<Torrent>) : SearchTorrentsResponse()
    data class Error(val message: String) : SearchTorrentsResponse()
}
