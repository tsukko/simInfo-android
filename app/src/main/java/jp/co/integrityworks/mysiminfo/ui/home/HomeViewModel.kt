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

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.integrityworks.mysiminfo.util.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
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

    // WiFi Usage
    var dailyWifiUsage by mutableStateOf("0.00 MB")
        private set
    var monthlyWifiUsage by mutableStateOf("0.00 MB")
        private set

    // Network Details
    var connectionType by mutableStateOf("None")
        private set
    var privateIp by mutableStateOf("Unknown")
        private set
    var publicIp by mutableStateOf("Fetching...")
        private set
    var wifiSsid by mutableStateOf("Unknown")
        private set
    var wifiSignal by mutableStateOf("N/A")
        private set
    var wifiLinkSpeed by mutableStateOf("N/A")
        private set

    // Speed Test
    var downloadSpeed by mutableStateOf("0.0 Mbps")
        private set
    var uploadSpeed by mutableStateOf("0.0 Mbps")
        private set
    var isTesting by mutableStateOf(false)
        private set

    // Device Information
    var androidVersion by mutableStateOf("")
        private set
    var apiLevel by mutableStateOf("")
        private set
    var securityPatch by mutableStateOf("")
        private set
    var deviceModel by mutableStateOf("")
        private set
    var manufacturer by mutableStateOf("")
        private set
    var kernelVersion by mutableStateOf("")
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
            context.getString(R.string.restricted_by_android)
        } else {
            telMgr.simSerialNumber ?: ""
        }
        deviceId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getString(R.string.restricted_by_android)
        } else {
            // Access to device identifiers is restricted; do not use deprecated APIs.
            ""
        }
        androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        simOperator = telMgr.simOperator ?: ""
        simOperatorName = telMgr.simOperatorName ?: ""
        simState = telMgr.simState.toString()
        voiceMailNumber = telMgr.voiceMailNumber ?: ""

        // Device Info
        androidVersion = Build.VERSION.RELEASE
        apiLevel = Build.VERSION.SDK_INT.toString()
        securityPatch = Build.VERSION.SECURITY_PATCH
        deviceModel = Build.MODEL
        manufacturer = Build.MANUFACTURER
        kernelVersion = System.getProperty("os.version") ?: "Unknown"
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

            // WiFi Usage
            val (wifiTodayRx, wifiTodayTx) = helper.getWifiDataUsage(DataUsageHelper.getTodayStart(), now)
            dailyWifiUsage = helper.formatBytes(wifiTodayRx + wifiTodayTx)

            val (wifiMonthRx, wifiMonthTx) = helper.getWifiDataUsage(DataUsageHelper.getMonthStart(), now)
            monthlyWifiUsage = helper.formatBytes(wifiMonthRx + wifiMonthTx)
        }
    }

    fun updateNetworkInfo(context: Context) {
        val helper = NetworkHelper(context)
        connectionType = helper.getConnectionType()
        privateIp = helper.getPrivateIpAddress()
        
        if (connectionType == "WiFi") {
            val details = helper.getWifiDetails()
            wifiSsid = details["SSID"] ?: "Unknown"
            wifiSignal = details["Signal"] ?: "N/A"
            wifiLinkSpeed = details["LinkSpeed"] ?: "N/A"
        } else {
            wifiSsid = "N/A"
            wifiSignal = "N/A"
            wifiLinkSpeed = "N/A"
        }

        viewModelScope.launch {
            publicIp = helper.getPublicIpAddress()
        }
    }

    fun runSpeedTest(context: Context) {
        if (isTesting) return
        isTesting = true
        val helper = NetworkHelper(context)
        
        viewModelScope.launch {
            val down = helper.testDownloadSpeed()
            downloadSpeed = "%.1f Mbps".format(down)
            
            val up = helper.testUploadSpeed()
            uploadSpeed = "%.1f Mbps".format(up)
            
            isTesting = false
        }
    }
}
