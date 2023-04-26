package ru.asmelnikov.wifiscanner.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item")
data class WifiItemSave(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ssid: String,
    val bssid: String,
    val capabilities: String,
    val frequency: Int,
    val level: Int
)
