package ru.asmelnikov.wifiscanner.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.asmelnikov.wifiscanner.R
import ru.asmelnikov.wifiscanner.domain.WifiItemSave

class WifiSavedAdapter : RecyclerView.Adapter<WifiSavedAdapter.NetworkViewHolder>() {
    private var networks: List<WifiItemSave> = listOf()

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

    fun updateNetworks(networks: List<WifiItemSave>) {
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

        fun bind(network: WifiItemSave) {
            ssidTextView.text = network.ssid
            bssidTextView.text = network.bssid
            signalStrengthTextView.text = "${network.level} dBm"
            if (network.capabilities == "[ESS]") {
                openLock.setImageResource(R.drawable.baseline_lock_open_24)
            } else {
                openLock.setImageResource(R.drawable.baseline_lock_24)
            }
        }
    }
}
