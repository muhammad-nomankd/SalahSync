import com.durranitech.salahsync.domain.model.NextPrayer
import com.durranitech.salahsync.domain.model.SalahTime
import java.util.Calendar

fun SalahTime.getNextPrayerSimple(): NextPrayer {
    val now = System.currentTimeMillis()
    val cal = Calendar.getInstance()

    fun timeOf(hour: Int, minute: Int, addDay: Boolean = false): Long {
        cal.timeInMillis = now
        if (addDay) cal.add(Calendar.DAY_OF_YEAR, 1)
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    val todayPrayers = listOf(
        "Fajr" to timeOf(fajrHour, fajrMinute),
        "Zuhr" to timeOf(dhuhrHour, dhuhrMinute),
        "Asr" to timeOf(asrHour, asrMinute),
        "Maghrib" to timeOf(maghribHour, maghribMinute),
        "Isha" to timeOf(ishaHour, ishaMinute)
    )

    val upcomingToday = todayPrayers.firstOrNull { it.second > now }

    val (name, time) = upcomingToday ?: run {
        "Fajr" to timeOf(fajrHour, fajrMinute, addDay = true)
    }

    return NextPrayer(
        name = name,
        atMillis = time,
        remainingMillis = time - now
    )
}
