package ru.asmelnikov.wifiscanner.ui.wifi_list_fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.asmelnikov.wifiscanner.R
import ru.asmelnikov.wifiscanner.databinding.FragmentWifiListBinding
import ru.asmelnikov.wifiscanner.domain.toWifiSaved
import ru.asmelnikov.wifiscanner.ui.adapters.WifiNetworkAdapter

@AndroidEntryPoint
class FragmentWifiList : Fragment(R.layout.fragment_wifi_list) {

    private var _binding: FragmentWifiListBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }

    private val viewModel: WifiScannerViewModel by viewModels()

    private lateinit var adapter: WifiNetworkAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWifiListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = WifiNetworkAdapter()

        binding.scanButton.setOnClickListener {

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

        binding.networkRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FragmentWifiList.adapter
        }

        viewModel.wifiNetworksLiveData.observe(viewLifecycleOwner) { networks ->
            adapter.differ.submitList(networks)
            if (networks.isNotEmpty()) binding.saveButton.isEnabled = true
            binding.saveButton.setOnClickListener {
                val list = networks.map { it.toWifiSaved() }
                viewModel.insertWifiList(list)
                Toast.makeText(requireContext(), "Wifi list is saved!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

