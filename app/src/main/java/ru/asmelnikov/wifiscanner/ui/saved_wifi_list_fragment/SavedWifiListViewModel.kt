package ru.asmelnikov.wifiscanner.ui.saved_wifi_list_fragment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import ru.asmelnikov.wifiscanner.domain.WifiItemSave
import ru.asmelnikov.wifiscanner.domain.WifiRepository
import javax.inject.Inject

@HiltViewModel
class SavedWifiListViewModel @Inject constructor(private val repository: WifiRepository) :
    ViewModel() {
    fun getAllWifi(): Flow<List<WifiItemSave>> = repository.getAllWifiNetworks()

}