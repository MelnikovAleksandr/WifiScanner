package ru.asmelnikov.wifiscanner.ui.adapters

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener
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

    @RequiresApi(Build.VERSION_CODES.Q)
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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showConnectDialog(network: WifiNetwork, view: View) {
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("Enter Password")
        val input = EditText(view.context)
        input.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)
        builder.setPositiveButton("Connect") { _, _ ->
            val password = input.text.toString()

            val permissions = arrayOf(
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE
            )
            if (ActivityCompat.checkSelfPermission(
                    view.context,
                    Manifest.permission.ACCESS_WIFI_STATE
                ) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    view.context,
                    Manifest.permission.CHANGE_WIFI_STATE
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    view.context,
                    Manifest.permission.CHANGE_NETWORK_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(view.context as Activity, permissions, 0)
                return@setPositiveButton
            }
            connectToWifi(network.ssid, password, view)
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun connectToWifi(ssid: String, key: String, view: View) {
        WifiUtils.withContext(view.context)
            .connectWith(ssid, key)
            .onConnectionResult(object : ConnectionSuccessListener {
                override fun success() {
                    Toast.makeText(view.context, "SUCCESS!", Toast.LENGTH_SHORT).show()
                }

                override fun failed(errorCode: ConnectionErrorCode) {
                    Toast.makeText(
                        view.context,
                        "EPIC FAIL! $errorCode",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            .start()
    }
}
