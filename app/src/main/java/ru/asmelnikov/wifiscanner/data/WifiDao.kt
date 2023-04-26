package ru.asmelnikov.wifiscanner.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.asmelnikov.wifiscanner.domain.WifiItemSave

@Dao
interface WifiDao {

    @Query("SELECT * FROM item")
    fun getAllWifiNetworks(): Flow<List<WifiItemSave>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWifiNetworks(wifiNetworks: List<WifiItemSave>)
}