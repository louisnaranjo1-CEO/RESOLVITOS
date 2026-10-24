package com.example.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt

/**
 * Fetches the official USD -> VES (Bolívares) exchange rate directly from
 * Venezuela's Banco Central de Venezuela (BCV) official rate APIs.
 */
object BcvRateService {
    private const val TAG = "BcvRateService"

    suspend fun fetchOfficialBcvRate(): Double? = withContext(Dispatchers.IO) {
        // 1. Primary Official BCV API (ve.dolarapi.com/v1/dolares/oficial)
        fetchFromDolarApiVe()?.let { rate ->
            if (rate > 1.0) return@withContext roundTwoDecimals(rate)
        }

        // 2. Secondary Official BCV Monitor API (pydolarve.org)
        fetchFromPyDolarVe()?.let { rate ->
            if (rate > 1.0) return@withContext roundTwoDecimals(rate)
        }

        // 3. Tertiary ExchangeRate API for VES
        fetchFromOpenErApi()?.let { rate ->
            if (rate > 1.0) return@withContext roundTwoDecimals(rate)
        }

        null
    }

    private fun fetchFromDolarApiVe(): Double? {
        return try {
            val jsonStr = httpGet("https://ve.dolarapi.com/v1/dolares/oficial") ?: return null
            val json = JSONObject(jsonStr)
            val promedio = json.optDouble("promedio", -1.0)
            if (promedio > 1.0) promedio else null
        } catch (e: Exception) {
            Log.w(TAG, "Primary BCV API fallback: ${e.message}")
            null
        }
    }

    private fun fetchFromPyDolarVe(): Double? {
        return try {
            val jsonStr = httpGet("https://pydolarve.org/api/v1/dollar?page=bcv") ?: return null
            val json = JSONObject(jsonStr)
            val monitors = json.optJSONObject("monitors")
            val usd = monitors?.optJSONObject("usd") ?: monitors?.optJSONObject("bcv")
            val price = usd?.optDouble("price", -1.0) ?: -1.0
            if (price > 1.0) price else null
        } catch (e: Exception) {
            Log.w(TAG, "Secondary BCV API fallback: ${e.message}")
            null
        }
    }

    private fun fetchFromOpenErApi(): Double? {
        return try {
            val jsonStr = httpGet("https://open.er-api.com/v6/latest/USD") ?: return null
            val json = JSONObject(jsonStr)
            val rates = json.optJSONObject("rates")
            val ves = rates?.optDouble("VES", -1.0) ?: -1.0
            if (ves > 1.0) ves else null
        } catch (e: Exception) {
            Log.w(TAG, "Tertiary BCV API fallback: ${e.message}")
            null
        }
    }

    private fun httpGet(urlString: String): String? {
        var connection: HttpURLConnection? = null
        return try {
            val url = URL(urlString)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 6000
                readTimeout = 6000
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "Resolvitos-Android/1.0")
            }
            if (connection.responseCode in 200..299) {
                BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                    val sb = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        sb.append(line)
                    }
                    sb.toString()
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        } finally {
            connection?.disconnect()
        }
    }

    private fun roundTwoDecimals(value: Double): Double {
        return (value * 100.0).roundToInt() / 100.0
    }
}
