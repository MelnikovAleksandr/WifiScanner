package ru.asmelnikov.wifiscanner.ui.adapters

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.asmelnikov.wifiscanner.R
import ru.asmelnikov.wifiscanner.data.WifiNetwork
import ru.asmelnikov.wifiscanner.databinding.WifiItemBinding

class WifiNetworkAdapter : RecyclerView.Adapter<WifiNetworkAdapter.WifiListViewHolder>() {

    inner class WifiListViewHolder(val viewBinding: WifiItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    private val callBack = object : DiffUtil.ItemCallback<WifiNetwork>() {
        override fun areItemsTheSame(oldItem: WifiNetwork, newItem: WifiNetwork): Boolean {
            return oldItem.bssid == newItem.bssid
        }

        override fun areContentsTheSame(oldItem: WifiNetwork, newItem: WifiNetwork): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, callBack)

    override fun onBindViewHolder(holder: WifiListViewHolder, position: Int) {
        val wifiItem = differ.currentList[position]
        holder.viewBinding.apply {
            networkNameTextView.text = wifiItem.ssid
            networkCapabilitiesTextView.text = wifiItem.bssid
            networkStrengthTextView.text = "${wifiItem.level} dBm"
            if (wifiItem.capabilities == "[ESS]") {
                lockImageView.setImageResource(R.drawable.baseline_lock_open_24)
            } else {
                lockImageView.setImageResource(R.drawable.baseline_lock_24)
            }
            listItem.setOnClickListener {
                showConnectDialog(wifiItem, listItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiListViewHolder {
        val binding = WifiItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return WifiListViewHolder(binding)
    }

    private fun showConnectDialog(network: WifiNetwork, view: View) {
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("Enter Password")
        val input = EditText(view.context)
        input.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)
        builder.setPositiveButton("Connect") { _, _ ->
            val password = input.text.toString()
            val wifiConfiguration = WifiConfiguration()
            wifiConfiguration.SSID = "\"" + network.ssid + "\""
            wifiConfiguration.preSharedKey = "\"$password\""

            val wifiManager =
                view.context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

            val permissions = arrayOf(
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
            )
            if (ActivityCompat.checkSelfPermission(
                    view.context,
                    Manifest.permission.ACCESS_WIFI_STATE
                ) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    view.context,
                    Manifest.permission.CHANGE_WIFI_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(view.context as Activity, permissions, 0)
                return@setPositiveButton
            }

            var networkId = wifiManager.addNetwork(wifiConfiguration)
            if (networkId == -1) {
                val existingConfig =
                    wifiManager.configuredNetworks.firstOrNull { it.SSID == wifiConfiguration.SSID }
                if (existingConfig != null) {
                    networkId = existingConfig.networkId
                }
            }
            if (networkId != -1) {
                wifiManager.enableNetwork(networkId, true)
                wifiManager.saveConfiguration()
            } else {
                Toast.makeText(
                    view.context.applicationContext,
                    "Failed to add network configuration",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

}
