package ru.asmelnikov.wifiscanner.ui.adapter

import android.content.Context
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
import androidx.recyclerview.widget.RecyclerView
import ru.asmelnikov.wifiscanner.R
import ru.asmelnikov.wifiscanner.data.WifiNetwork

class WifiNetworkAdapter : RecyclerView.Adapter<WifiNetworkAdapter.NetworkViewHolder>() {
    private var networks: List<WifiNetwork> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NetworkViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.wifi_item, parent, false)
        return NetworkViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NetworkViewHolder, position: Int) {
        holder.bind(networks[position])
    }

    override fun getItemCount(): Int {
        return networks.size
    }

    fun updateNetworks(networks: List<WifiNetwork>) {
        this.networks = networks
        notifyDataSetChanged()
    }

    class NetworkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ssidTextView: TextView = itemView.findViewById(R.id.networkNameTextView)
        private val bssidTextView: TextView =
            itemView.findViewById(R.id.networkCapabilitiesTextView)
        private val signalStrengthTextView: TextView =
            itemView.findViewById(R.id.networkStrengthTextView)
        private val openLock: ImageView = itemView.findViewById(R.id.lockImageView)

        fun bind(network: WifiNetwork) {
            ssidTextView.text = network.ssid
            bssidTextView.text = network.bssid
            signalStrengthTextView.text = "${network.level} dBm"
            if (network.capabilities == "[ESS]") {
                openLock.setImageResource(R.drawable.baseline_lock_open_24)
            } else {
                openLock.setImageResource(R.drawable.baseline_lock_24)
            }

            itemView.setOnClickListener {
                showConnectDialog(network)
            }
        }

        private fun showConnectDialog(network: WifiNetwork) {
            val builder = AlertDialog.Builder(itemView.context)
            builder.setTitle("Enter Password")
            val input = EditText(itemView.context)
            input.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            builder.setView(input)
            builder.setPositiveButton("Connect") { _, _ ->
                val password = input.text.toString()
                val wifiConfiguration = WifiConfiguration()
                wifiConfiguration.SSID = "\"" + network.ssid + "\""
                wifiConfiguration.preSharedKey = "\"$password\""
                val wifiManager =
                    itemView.context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val networkId = wifiManager.addNetwork(wifiConfiguration)
                if (networkId != -1) {
                    wifiManager.enableNetwork(networkId, true)
                } else {
                    Toast.makeText(
                        itemView.context.applicationContext,
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
}
