package torrentsearch.internal

import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

val Calendar.day: Int get() = get(Calendar.DATE)
val Calendar.month: Int get() = get(Calendar.MONTH)
val Calendar.year: Int get() = get(Calendar.YEAR)
val Calendar.hourOfDay: Int get() = get(Calendar.HOUR_OF_DAY)
val Calendar.hour: Int get() = get(Calendar.HOUR)
val Calendar.minute: Int get() = get(Calendar.MINUTE)
val Calendar.second: Int get() = get(Calendar.SECOND)
val Calendar.millis: Int get() = get(Calendar.MILLISECOND)
fun Date.toCalendar(timeZone: TimeZone, locale: Locale): Calendar =
    Calendar.getInstance(timeZone, locale).also { it.time = this }