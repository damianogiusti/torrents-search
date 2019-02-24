package torrentsearch.internal.remotes

import torrentsearch.internal.hour
import torrentsearch.internal.minute
import torrentsearch.internal.toCalendar
import torrentsearch.internal.year
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToLong

private val dateSuffixes by lazy { setOf("st", "nd", "rd", "th") }
private const val AM = "am"
private const val PM = "pm"
private val timeSuffixes by lazy { setOf(AM, PM) }
private val timezone = TimeZone.getTimeZone("Europe/Rome")

internal fun parseFormattedDate(
    dateString: String,
    locale: Locale = Locale.US,
    currentYear: Int = Calendar.getInstance(timezone, locale).year
): Date {
    /*
      Date will be presented in the following formats:
        - date: Oct. 26th '11
        - time: 11:22am
        - datetime: 1pm Dec. 11th
     */
    return when {
        isDateTime(dateString) -> {
            val string = dateString.removing(dateSuffixes + timeSuffixes)
            val sdf = SimpleDateFormat("h MMM. d", locale)
            sdf.timeZone = timezone
            sdf.parse(string).toCalendar(timezone, locale).apply {
                set(Calendar.HOUR_OF_DAY, fixAmPmHour(dateString, hour))
                set(Calendar.YEAR, currentYear)
            }.time
        }
        isTime(dateString) -> {
            val sdf = SimpleDateFormat("h:mm", locale)
            sdf.timeZone = timezone
            Calendar.getInstance(timezone, locale).apply {
                val date = sdf.parse(dateString.removing(timeSuffixes)).toCalendar(
                    timezone, locale)
                set(Calendar.HOUR_OF_DAY, fixAmPmHour(dateString, date.hour))
                set(Calendar.MINUTE, date.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        }
        else -> {
            val string = dateString.removing(dateSuffixes)
            val sdf = SimpleDateFormat("MMM. d ''yy", locale)
            sdf.timeZone = timezone
            sdf.parse(string)
        }
    }
}

private fun isTime(string: String) = timeSuffixes.any { it in string }
private fun isDateTime(string: String) = isTime(string) && !string.contains(':')
private fun fixAmPmHour(dateString: String, hour: Int) =
    if (AM in dateString) hour else hour + 12

private fun String.removing(occurrences: Set<String>): String =
    occurrences.fold(this) { acc, suffix -> acc.replace(suffix, "") }

///////////////////////////////////////////////////////////////////////////
// Size
///////////////////////////////////////////////////////////////////////////

private const val UNIT_GB = "GB"
private const val UNIT_MB = "MB"
private const val UNIT_KB = "KB"
private const val UNIT_B = "B"
private val sizeSuffixes by lazy { setOf(
    UNIT_GB,
    UNIT_MB,
    UNIT_KB,
    UNIT_B
) }

internal fun getSizeBytes(sizeString: String): Long {
    val string = sizeSuffixes.fold(sizeString) { acc, suffix -> acc.replace(suffix, "") }.trim()
    val numberFormat = DecimalFormat.getInstance(Locale.US) as DecimalFormat
    val number = numberFormat.parse(string).toDouble()
    val uppercased = sizeString.toUpperCase()
    return when {
        uppercased.contains(UNIT_GB) -> number * Math.pow(1024.0, 3.0)
        uppercased.contains(UNIT_MB) -> number * Math.pow(1024.0, 2.0)
        uppercased.contains(UNIT_KB) -> number * Math.pow(1024.0, 1.0)
        uppercased.contains(UNIT_B) -> number
        else -> 0.0
    }.roundToLong()
}