package ru.asmelnikov.wifiscanner.ui.wifi_list_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.asmelnikov.wifiscanner.data.WifiScanner

class WifiScannerViewModelFactory(private val wifiScanner: WifiScanner) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WifiScannerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WifiScannerViewModel(wifiScanner) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
