package ec601.aty.food_app

import java.text.DateFormat
import java.util.TimeZone

import java.text.DateFormat.getDateTimeInstance

object DateAndTimeUtils {

    private val MILLISECONDS_IN_HOUR = 3600000
    private val SECONDS_IN_HOUR = 3600
    private val MILLISECONDS_TO_SECONDS = 3600

    val currentUnixTime: Long
        get() = System.currentTimeMillis()

    fun addHoursToUnixTime(unixTime: Long, hours: Int): Long {
        return unixTime + hours * MILLISECONDS_IN_HOUR
    }

    fun getLocalFormattedDateFromUnixTime(unixTime: Long): String {
        val sdf = getDateTimeInstance()
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(unixTime)
    }

    fun checkIfUnixTimeIsExpired(unixTime: Long): Boolean {
        return currentUnixTime > unixTime
    }
}
