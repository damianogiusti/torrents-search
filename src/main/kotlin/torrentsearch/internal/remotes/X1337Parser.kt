package torrentsearch.internal.remotes

import org.jsoup.Jsoup
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

private const val TYPE_COLUMN = 0
private const val INFO_COLUMN = 1
private const val SEEDERS_COLUMN = 2
private const val LEECHERS_COLUMN = 3

private val TORRENT_ID_REGEX = "(\\/dltrack\\/[0-9]+)".toRegex()

internal class X1337Parser {

    data class MainInfo(
        val name: String,
        val date: String,
        val size: String,
        val seedersCount: String,
        val leechersCount: String,
        val linkToDetail: String?
    )

    data class DetailInfo(
        val id: String,
        val magnetLink: String
    )

    fun getMainInfo(html: String): List<MainInfo>? {
        val table = Jsoup.parse(html)
            ?.getElementsByClass("table-list table table-responsive table-striped")
            ?.firstOrNull()
            ?: return null

        val trs = table.getElementsByTag("tr").orEmpty()
        return trs.map { tr -> tr.getElementsByTag("td") }
            .filter { it.count() >= 5 }
            .mapNotNull { tds ->
                val (name, seed, leech, date, size) = tds
                val stringName = name.children().find { !it.hasClass("icon") }?.ownText()
                    ?: return@mapNotNull null

                MainInfo(
                    name = stringName,
                    date = date.ownText().trim(),
                    size = size.ownText().trim(),
                    seedersCount = seed.ownText().trim(),
                    leechersCount = leech.ownText().trim(),
                    linkToDetail = name.children().lastOrNull { it.tagName() == "a" }?.attr("href")
                )
            }
    }

    fun getDetailInfo(html: String): DetailInfo? {
        val id = TORRENT_ID_REGEX.find(html)?.value?.split("/")
            ?.lastOrNull()
            ?: return null
        val magnetLinkButton = Jsoup.parse(html)
            ?.getElementsByClass("download-links-dontblock btn-wrap-list")
            ?.firstOrNull()
            ?: return null
        val magnetLinkAnchor = magnetLinkButton.getElementsByTag("a").firstOrNull() ?: return null
        return DetailInfo(
            id = id,
            magnetLink = magnetLinkAnchor.attr("href")
        )
    }

    internal fun getDate(info: String): Long? {
        return try {
            info.split(",").first()
                .replace("Uploaded", "", ignoreCase = true)
                .trim()
                .let(SimpleDateFormat("dd-MM yyyy")::parse)
                .let(Date::getTime)
        } catch (e: ParseException) {
            // If the date parsing fails, return null.
            null
        }
    }

    internal fun getSize(info: String): String {
        return info.split(",")[1]
            .replace("Dimensione", "", ignoreCase = true)
            .replace("Size", "", ignoreCase = true)
            .trim()
    }
}