package jp.co.integrityworks.mysiminfo.util

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.util.Calendar

class DataUsageHelper(private val context: Context) {

    /**
     * Check if "Usage Access" permission is granted.
     */
    @Suppress("DEPRECATION")
    fun isUsageAccessGranted(): Boolean {
        // Use UsageStatsManager to determine whether usage access is available by querying a small range.
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val now = System.currentTimeMillis()
        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, now - 60_000L, now)
        return stats != null && stats.isNotEmpty()
    }

    /**
     * Get mobile data usage for a specific time range.
     * Returns a Pair of (Received Bytes, Transmitted Bytes).
     */
    @Suppress("DEPRECATION")
    fun getMobileDataUsage(startTime: Long, endTime: Long): Pair<Long, Long> {
        val networkStatsManager =
            context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

        return try {
            // Passing null for subscriberId returns aggregate mobile data on modern Android.
            // Use NetworkCapabilities.TRANSPORT_CELLULAR to avoid deprecated ConnectivityManager.TYPE_MOBILE constant
            val bucket = networkStatsManager.querySummaryForDevice(
                NetworkCapabilities.TRANSPORT_CELLULAR,
                null,
                startTime,
                endTime
            )
            Pair(bucket.rxBytes, bucket.txBytes)
        } catch (e: Exception) {
            Pair(0L, 0L)
        }
    }

    /**
     * Get WiFi data usage for a specific time range.
     */
    @Suppress("DEPRECATION")
    fun getWifiDataUsage(startTime: Long, endTime: Long): Pair<Long, Long> {
        val networkStatsManager =
            context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

        return try {
            val bucket = networkStatsManager.querySummaryForDevice(
                NetworkCapabilities.TRANSPORT_WIFI,
                null,
                startTime,
                endTime
            )
            Pair(bucket.rxBytes, bucket.txBytes)
        } catch (e: Exception) {
            Pair(0L, 0L)
        }
    }

    /**
     * Formats bytes into a human-readable string (MB/GB).
     */
    fun formatBytes(bytes: Long): String {
        val mb = bytes / (1024.0 * 1024.0)
        return if (mb >= 1024) {
            "%.2f GB".format(mb / 1024.0)
        } else {
            "%.2f MB".format(mb)
        }
    }

    companion object {
        fun getTodayStart(): Long {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }

        fun getYesterdayStart(): Long {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }

        fun getThisWeekStart(): Long {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }

        fun getMonthStart(): Long {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }
    }
}
