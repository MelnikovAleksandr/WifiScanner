package ru.asmelnikov.wifiscanner.domain

import kotlinx.coroutines.flow.Flow

interface WifiRepository {

    fun getAllWifiNetworks(): Flow<List<WifiItemSave>>

    suspend fun insertWifiNetworks(wifiNetworks: List<WifiItemSave>)
}