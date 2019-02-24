package torrentsearch.internal

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path

internal interface TorrentsApi {

    companion object {
        const val RPC_PATH = "http://192.168.1.101:9091/transmission/rpc"
        const val AUTH_HEADER_NAME = "Authorization"
        const val SESSION_HEADER_NAME = "X-Transmission-Session-Id"
        const val LIME_BASE_URL = "https://limetorrent.cc"
        const val X1337_BASE_URL = "https://1337x.to"
    }

    @GET("$X1337_BASE_URL/search/{query}/1/")
    fun search1337x(
        @Path("query") query: String
    ): Deferred<String>

    @GET("$X1337_BASE_URL{segment}")
    fun search1337Details(@Path("segment", encoded = true) url: String): Deferred<String>
}