package ru.asmelnikov.wifiscanner.data

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.asmelnikov.wifiscanner.domain.WifiItemSave

@Database(
    entities = [WifiItemSave::class],
    version = 1,
    exportSchema = true
)
abstract class WifiDatabase : RoomDatabase() {
    abstract fun getWifiDao(): WifiDao
}