package torrentsearch.internal.remotes

import torrentsearch.internal.day
import torrentsearch.internal.hourOfDay
import torrentsearch.internal.millis
import torrentsearch.internal.minute
import torrentsearch.internal.month
import torrentsearch.internal.second
import torrentsearch.internal.toCalendar
import torrentsearch.internal.year
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals


class ParsingUtilsKtTest {

    @Test fun `getSizeBytes should convert values`() {
        assertEquals(1, getSizeBytes("1b"))
        assertEquals(1, getSizeBytes("1B"))

        assertEquals(1024, getSizeBytes("1kB"))
        assertEquals(1024, getSizeBytes("1kb"))
        assertEquals(1024, getSizeBytes("1KB"))

        assertEquals(1_048_576, getSizeBytes("1MB"))
        assertEquals(1_048_576, getSizeBytes("1mB"))
        assertEquals(1_048_576, getSizeBytes("1mb"))

        assertEquals(1_073_741_824, getSizeBytes("1GB"))
        assertEquals(1_073_741_824, getSizeBytes("1gB"))
        assertEquals(1_073_741_824, getSizeBytes("1gb"))
    }

    @Test fun `parseFormattedDate parses relative date`() {
        assertEquals(1319580000000, parseFormattedDate("Oct. 26th '11").time)
        assertEquals(1446332400000, parseFormattedDate("Nov. 1st '15").time)
        assertEquals(1442872800000, parseFormattedDate("Sep. 22nd '15").time)
        assertEquals(1463954400000, parseFormattedDate("May. 23rd '16").time)
        assertEquals(1195426800000, parseFormattedDate("Nov. 19th '07").time)
    }

    @Test fun `parseFormattedDate parses relative time`() {
        assertDate(11, 22) { parseFormattedDate("11:22am") }
        assertDate(13, 47) { parseFormattedDate("1:47pm") }
        assertDate(12, 21) { parseFormattedDate("12:21pm") }
        assertDate(10, 0) { parseFormattedDate("10:00am") }
        assertDate(16, 15) { parseFormattedDate("4:15pm") }
        assertDate(3, 40) { parseFormattedDate("3:40am") }
        assertDate(0, 0) { parseFormattedDate("12:00am") }
    }

    @Test fun `parseFormattedDate parses relative timestamp`() {
        parseFormattedDate("1pm Dec. 11th", currentYear = 2018)
            .run { assertEquals(1544529600000, time, toString()) }
        parseFormattedDate("4pm Jun. 22nd", currentYear = 2018)
            .run { assertEquals(1529676000000, time, toString()) }
        parseFormattedDate("12am Feb. 23rd", currentYear = 2018) // Midnight.
            .run { assertEquals(1519340400000, time, toString()) }
    }
}

private fun assertDate(hour: Int, minute: Int, block: () -> Date) {
    val locale = Locale.US
    val timeZone = TimeZone.getTimeZone("Europe/Rome")
    val calendar = block().toCalendar(timeZone, locale)
    val today = Calendar.getInstance(timeZone, locale)
    assertEquals(today.day, calendar.day)
    assertEquals(today.month, calendar.month)
    assertEquals(today.year, calendar.year)
    assertEquals(hour, calendar.hourOfDay)
    assertEquals(minute, calendar.minute)
    assertEquals(0, calendar.second)
    assertEquals(0, calendar.millis)
}