package ru.asmelnikov.wifiscanner.data

import kotlinx.coroutines.flow.Flow
import ru.asmelnikov.wifiscanner.domain.WifiItemSave
import ru.asmelnikov.wifiscanner.domain.WifiRepository

class WifiRepositoryImpl(private val wifiDao: WifiDao) : WifiRepository {
    override fun getAllWifiNetworks(): Flow<List<WifiItemSave>> {
        return wifiDao.getAllWifiNetworks()
    }

    override suspend fun insertWifiNetworks(wifiNetworks: List<WifiItemSave>) {
        wifiDao.insertWifiNetworks(wifiNetworks)
    }
}