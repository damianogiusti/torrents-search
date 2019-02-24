package torrentsearch.internal.remotes

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class X1337ParserTest {

    private val parser by lazy { X1337Parser() }
    private val masterPage by htmlReader("x1337-master.html")
    private val detailPage by htmlReader("x1337-detail.html")

    @BeforeTest
    fun setUp() {

    }

    @Test fun setupCorrectly() {
        assertTrue(masterPage.isNotEmpty())
        assertTrue(detailPage.isNotEmpty())
        parser.toString()
    }

    @Test fun `getMainInfo will parse the master page`() {
        val results = parser.getMainInfo(masterPage)
        assertNotNull(results)
        val (first, second, third) = results
        assertEquals("Harry Potter And The Deathly Hallows Part 2 (2011) 720p - YIFY", first.name)
        assertEquals("Oct. 26th '11", first.date)
        assertEquals("1009", first.seedersCount)
        assertEquals("91", first.leechersCount)
        assertEquals("1.0 GB", first.size)
        assertEquals(
            expected = "/torrent/258188/Harry-Potter-And-The-Deathly-Hallows-Part-2-2011-720p-YIFY/",
            actual = first.linkToDetail
        )

        assertEquals("Harry Potter and the Order of the Phoenix 2007 1080p BrRip x264 YIFY", second.name)
        assertEquals("Oct. 28th '15", second.date)
        assertEquals("767", second.seedersCount)
        assertEquals("103", second.leechersCount)
        assertEquals("1.9 GB", second.size)
        assertEquals(
            expected = "/torrent/1335348/Harry-Potter-and-the-Order-of-the-Phoenix-2007-1080p-BrRip-x264-YIFY/",
            actual = second.linkToDetail
        )

        assertEquals("Harry Potter and the Sorcerers Stone 2001 1080p BrRip x264 YIFY FIRST TRY", third.name)
        assertEquals("Oct. 28th '15", third.date)
        assertEquals("741", third.seedersCount)
        assertEquals("149", third.leechersCount)
        assertEquals("1.2 GB", third.size)
        assertEquals(
            expected = "/torrent/1335352/Harry-Potter-and-the-Sorcerers-Stone-2001-1080p-BrRip-x264-YIFY-FIRST-TRY/",
            actual = third.linkToDetail
        )
    }

    @Test fun `getDetailInfo will parse the detail page`() {
        val result = parser.getDetailInfo(detailPage)
        assertNotNull(result)
        assertEquals("1335352", result.id)
        assertEquals(
            expected = "magnet:?xt=urn:btih:B47882A62EEDEC7767AA86B7A866F1DD846C5357&dn=Harry+Potter+and+the+Sorcerers+Stone+2001+1080p+BrRip+x264+YIFY+++FIRST+TRY+&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Ftracker.pomf.se%3A80&tr=udp%3A%2F%2Ftracker.publicbt.com%3A80&tr=udp%3A%2F%2Ftracker.openbittorrent.com%3A80&tr=udp%3A%2F%2Ftracker.istole.it%3A80&tr=udp%3A%2F%2Ftracker.zer0day.to%3A1337%2Fannounce&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969%2Fannounce&tr=udp%3A%2F%2Fcoppersurfer.tk%3A6969%2Fannounce",
            actual = result.magnetLink
        )
    }
}

private fun Any.htmlReader(name: String) = lazy {
    javaClass.classLoader.getResource(name)?.openStream()
        ?.bufferedReader()?.use { it.readText() } ?: error("Error reading stream!")
}