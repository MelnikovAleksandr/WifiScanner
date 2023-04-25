package ru.asmelnikov.wifiscanner.ui.wifi_list_fragment

import android.net.wifi.ScanResult
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.asmelnikov.wifiscanner.data.WifiNetwork
import ru.asmelnikov.wifiscanner.data.WifiScanner

class WifiScannerViewModel(private val wifiScanner: WifiScanner) : ViewModel() {

    private val _wifiNetworksLiveData = MutableLiveData<List<WifiNetwork>>()
    val wifiNetworksLiveData: LiveData<List<WifiNetwork>> = _wifiNetworksLiveData

    fun scanWifiNetworks() {
        wifiScanner.scan(object : WifiScanner.OnWifiScanResultListener {

            override fun onScanResult(results: List<ScanResult>) {
                val wifiNetworks = results.map { result ->
                    WifiNetwork(
                        ssid = result.SSID,
                        bssid = result.BSSID,
                        capabilities = result.capabilities,
                        frequency = result.frequency,
                        level = result.level
                    )
                }
                _wifiNetworksLiveData.postValue(wifiNetworks)
            }

            override fun onError(error: Throwable) {
                Log.d("Scan Error", error.toString())
            }
        })
    }
}
