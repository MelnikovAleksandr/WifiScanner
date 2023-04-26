package ru.asmelnikov.wifiscanner.domain

import ru.asmelnikov.wifiscanner.data.WifiNetwork

fun WifiNetwork.toWifiSaved(): WifiItemSave {
    return WifiItemSave(
        id = 0,
        ssid = ssid,
        bssid = bssid,
        capabilities = capabilities,
        frequency = frequency,
        level = level
    )
}