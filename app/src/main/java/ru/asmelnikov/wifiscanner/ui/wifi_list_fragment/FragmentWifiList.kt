package ru.asmelnikov.wifiscanner.ui.wifi_list_fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ru.asmelnikov.wifiscanner.R
import ru.asmelnikov.wifiscanner.data.WifiScanner
import ru.asmelnikov.wifiscanner.domain.toWifiSaved
import ru.asmelnikov.wifiscanner.ui.adapter.WifiNetworkAdapter

@AndroidEntryPoint
class FragmentWifiList : Fragment(R.layout.fragment_wifi_list) {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }
    private val viewModel: WifiScannerViewModel by viewModels()
    //private lateinit var viewModel: WifiScannerViewModel
    private lateinit var adapter: WifiNetworkAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val wifiScanner = WifiScanner(requireContext())
//        val viewModelFactory = WifiScannerViewModelFactory(wifiScanner)
//        viewModel = ViewModelProvider(this, viewModelFactory)[WifiScannerViewModel::class.java]
        adapter = WifiNetworkAdapter()

        view.findViewById<Button>(R.id.scanButton).setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

                    AlertDialog.Builder(requireContext())
                        .setTitle("Location Permission")
                        .setMessage("This app needs location permission to perform wifi scanning")
                        .setPositiveButton("OK") { _, _ ->
                            requestPermissions(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_WIFI_STATE,
                                    Manifest.permission.CHANGE_WIFI_STATE
                                ),
                                PERMISSION_REQUEST_CODE
                            )
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                } else {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE
                        ),
                        PERMISSION_REQUEST_CODE
                    )
                }
            } else {
                viewModel.scanWifiNetworks()
            }

        }

        view.findViewById<RecyclerView>(R.id.networkRecyclerView).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FragmentWifiList.adapter
        }

        viewModel.wifiNetworksLiveData.observe(viewLifecycleOwner) { networks ->
            adapter.updateNetworks(networks)
            view.findViewById<Button>(R.id.saveButton).setOnClickListener {
                val list = networks.map { it.toWifiSaved() }
                viewModel.insertWifiList(list)
            }
        }
    }
}

