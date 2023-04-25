package ru.asmelnikov.wifiscanner.data

data class WifiNetwork(
    val ssid: String,
    val bssid: String,
    val capabilities: String,
    val frequency: Int,
    val level: Int
)
