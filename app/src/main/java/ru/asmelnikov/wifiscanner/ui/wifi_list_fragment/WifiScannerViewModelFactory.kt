package ru.asmelnikov.wifiscanner.ui.wifi_list_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.asmelnikov.wifiscanner.data.WifiScanner
import ru.asmelnikov.wifiscanner.domain.WifiRepository

class WifiScannerViewModelFactory(private val wifiScanner: WifiScanner, private val repository: WifiRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WifiScannerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WifiScannerViewModel(wifiScanner, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
