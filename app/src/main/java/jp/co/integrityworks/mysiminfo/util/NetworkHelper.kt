package jp.co.integrityworks.mysiminfo.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.NetworkInterface
import java.net.URL
import java.util.Collections

class NetworkHelper(private val context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    fun getConnectionType(): String {
        val network = connectivityManager.activeNetwork ?: return "None"
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return "None"
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobile"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Other"
        }
    }

    fun getPrivateIpAddress(): String {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress ?: continue
                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (isIPv4) return sAddr
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "Unknown"
    }

    suspend fun getPublicIpAddress(): String = withContext(Dispatchers.IO) {
        try {
            URL("https://api.ipify.org").readText()
        } catch (e: Exception) {
            "Unable to fetch"
        }
    }

    fun getWifiDetails(): Map<String, String> {
        val info = wifiManager.connectionInfo
        return mapOf(
            "SSID" to (info.ssid?.removeSurrounding("\"") ?: "Unknown"),
            "Signal" to "${info.rssi} dBm",
            "LinkSpeed" to "${info.linkSpeed} Mbps"
        )
    }

    suspend fun testDownloadSpeed(): Double = coroutineScope {
        val streamCount = 4
        val bytesPerStream = 6_291_456L // ~6MB

        val start = System.currentTimeMillis()
        val results = (1..streamCount).map {
            async(Dispatchers.IO) {
                try {
                    val connection = URL("https://speed.cloudflare.com/__down?bytes=$bytesPerStream").openConnection() as HttpURLConnection
                    connection.connectTimeout = 10000
                    connection.readTimeout = 10000
                    connection.connect()
                    
                    val inputStream = connection.inputStream
                    val buffer = ByteArray(65536) // 64KB
                    var readInStream = 0L
                    while (true) {
                        val read = inputStream.read(buffer)
                        if (read == -1) break
                        readInStream += read
                    }
                    readInStream
                } catch (e: Exception) {
                    0L
                }
            }
        }.awaitAll()
        
        val end = System.currentTimeMillis()
        val totalRead = results.sum()
        val durationSeconds = (end - start) / 1000.0
        
        if (durationSeconds == 0.0 || totalRead == 0L) return@coroutineScope 0.0
        // bps to Mbps
        (totalRead * 8.0 / 1_000_000.0) / durationSeconds
    }

    suspend fun testUploadSpeed(): Double = coroutineScope {
        val streamCount = 2
        val bytesPerStream = 2_621_440 // 2.5MB
        val data = ByteArray(bytesPerStream)

        val start = System.currentTimeMillis()
        val results = (1..streamCount).map {
            async(Dispatchers.IO) {
                try {
                    val connection = URL("https://speed.cloudflare.com/__up").openConnection() as HttpURLConnection
                    connection.doOutput = true
                    connection.requestMethod = "POST"
                    connection.connectTimeout = 10000
                    connection.readTimeout = 10000
                    connection.setRequestProperty("Content-Type", "application/octet-stream")
                    connection.outputStream.use { it.write(data) }
                    connection.responseCode
                    bytesPerStream.toLong()
                } catch (e: Exception) {
                    0L
                }
            }
        }.awaitAll()

        val end = System.currentTimeMillis()
        val totalWritten = results.sum()
        val durationSeconds = (end - start) / 1000.0
        
        if (durationSeconds == 0.0 || totalWritten == 0L) return@coroutineScope 0.0
        (totalWritten * 8.0 / 1_000_000.0) / durationSeconds
    }
}
