package jp.co.integrityworks.mysiminfo.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import jp.co.integrityworks.mysiminfo.util.DataUsageHelper

import jp.co.integrityworks.mysiminfo.R

class HomeViewModel : ViewModel() {
    var line1Number by mutableStateOf("")
        private set
    var simCountryIso by mutableStateOf("")
        private set
    var simSerialNumber by mutableStateOf("")
        private set
    var deviceId by mutableStateOf("")
        private set
    var androidId by mutableStateOf("")
        private set
    var simOperator by mutableStateOf("")
        private set
    var simOperatorName by mutableStateOf("")
        private set
    var simState by mutableStateOf("")
        private set
    var voiceMailNumber by mutableStateOf("")
        private set

    var isUsageAccessGranted by mutableStateOf(false)
        private set
    var dailyUsage by mutableStateOf("0.00 MB")
        private set
    var yesterdayUsage by mutableStateOf("0.00 MB")
        private set
    var weeklyUsage by mutableStateOf("0.00 MB")
        private set
    var monthlyUsage by mutableStateOf("0.00 MB")
        private set
    var monthlyDownload by mutableStateOf("0.00 MB")
        private set
    var monthlyUpload by mutableStateOf("0.00 MB")
        private set

    @SuppressLint("MissingPermission", "HardwareIds")
    fun initParameters(context: Context) {
        val telMgr = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val subscriptionManager =
            context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

        // アクティブなサブスクリプション情報の取得（Android 10+ では権限が必要な場合がある）
        try {
            subscriptionManager.activeSubscriptionInfoList?.forEach { _ ->
                // 必要に応じて個別SIMの情報取得ロジックを追加可能
            }
        } catch (e: SecurityException) {
            // Permission not granted
        }

        // Access to line1Number and other direct device identifiers is deprecated and restricted.
        // Avoid using deprecated APIs; provide empty or placeholder values on modern Android.
        line1Number = ""
        simCountryIso = telMgr.simCountryIso ?: ""
        simSerialNumber = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getString(R.string.not_supported_android_10)
        } else {
            telMgr.simSerialNumber ?: ""
        }
        deviceId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getString(R.string.not_supported_android_10)
        } else {
            // Access to device identifiers is restricted; do not use deprecated APIs.
            ""
        }
        androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        simOperator = telMgr.simOperator ?: ""
        simOperatorName = telMgr.simOperatorName ?: ""
        simState = telMgr.simState.toString()
        voiceMailNumber = telMgr.voiceMailNumber ?: ""
    }

    fun updateUsageStats(context: Context) {
        val helper = DataUsageHelper(context)
        isUsageAccessGranted = helper.isUsageAccessGranted()

        if (isUsageAccessGranted) {
            val now = System.currentTimeMillis()
            
            val (todayRx, todayTx) = helper.getMobileDataUsage(DataUsageHelper.getTodayStart(), now)
            dailyUsage = helper.formatBytes(todayRx + todayTx)

            val (yestRx, yestTx) = helper.getMobileDataUsage(DataUsageHelper.getYesterdayStart(), DataUsageHelper.getTodayStart())
            yesterdayUsage = helper.formatBytes(yestRx + yestTx)

            val (weekRx, weekTx) = helper.getMobileDataUsage(DataUsageHelper.getThisWeekStart(), now)
            weeklyUsage = helper.formatBytes(weekRx + weekTx)

            val (monthRx, monthTx) = helper.getMobileDataUsage(DataUsageHelper.getMonthStart(), now)
            monthlyUsage = helper.formatBytes(monthRx + monthTx)
            monthlyDownload = helper.formatBytes(monthRx)
            monthlyUpload = helper.formatBytes(monthTx)
        }
    }
}
