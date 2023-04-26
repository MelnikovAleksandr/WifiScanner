package ru.asmelnikov.wifiscanner.ui.wifi_list_fragment

import android.net.wifi.ScanResult
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.asmelnikov.wifiscanner.data.WifiNetwork
import ru.asmelnikov.wifiscanner.data.WifiScanner
import ru.asmelnikov.wifiscanner.domain.WifiItemSave
import ru.asmelnikov.wifiscanner.domain.WifiRepository
import javax.inject.Inject

@HiltViewModel
class WifiScannerViewModel @Inject constructor(
    private val wifiScanner: WifiScanner,
    private val repository: WifiRepository
) : ViewModel() {

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

    fun insertWifiList(list: List<WifiItemSave>) {
        viewModelScope.launch {
            repository.insertWifiNetworks(list)
        }
    }
}
