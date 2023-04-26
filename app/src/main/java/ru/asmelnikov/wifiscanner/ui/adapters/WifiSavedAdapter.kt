package ru.asmelnikov.wifiscanner.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.asmelnikov.wifiscanner.R
import ru.asmelnikov.wifiscanner.databinding.WifiItemBinding
import ru.asmelnikov.wifiscanner.domain.WifiItemSave

class WifiSavedAdapter : RecyclerView.Adapter<WifiSavedAdapter.NetworkViewHolder>() {
    inner class NetworkViewHolder(val viewBinding: WifiItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    private val callBack = object : DiffUtil.ItemCallback<WifiItemSave>() {
        override fun areItemsTheSame(oldItem: WifiItemSave, newItem: WifiItemSave): Boolean {
            return oldItem.bssid == newItem.bssid
        }

        override fun areContentsTheSame(oldItem: WifiItemSave, newItem: WifiItemSave): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, callBack)

    override fun onBindViewHolder(holder: NetworkViewHolder, position: Int) {
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
        }
    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NetworkViewHolder {
        val binding = WifiItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return NetworkViewHolder(binding)
    }
}
