package ru.asmelnikov.wifiscanner.data

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class WifiScanner(private val context: Context) {
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val mainHandler = Handler(Looper.getMainLooper())
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    interface OnWifiScanResultListener {
        fun onScanResult(results: List<ScanResult>)
        fun onError(error: Throwable)
    }

    fun scan(listener: OnWifiScanResultListener) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            listener.onError(Exception("Location permission is not granted"))
            return
        }

        if (!wifiManager.isWifiEnabled) {
            listener.onError(Exception("Wi-Fi is not enabled"))
            return
        }

        val scanReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                    val results = if (context?.let {
                            ActivityCompat.checkSelfPermission(
                                it,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        } == PackageManager.PERMISSION_GRANTED
                    ) {
                        wifiManager.scanResults
                    } else {
                        emptyList()
                    }
                    mainHandler.post { listener.onScanResult(results) }
                    context?.unregisterReceiver(this)
                }
            }
        }

        val filter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(scanReceiver, filter)

        executor.submit {
            try {
                wifiManager.startScan()
            } catch (e: Throwable) {
                mainHandler.post { listener.onError(e) }
            }
        }
    }
}



