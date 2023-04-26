package ru.asmelnikov.wifiscanner.ui.saved_wifi_list_fragment

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.asmelnikov.wifiscanner.databinding.FragmentWifiSavedBinding
import ru.asmelnikov.wifiscanner.ui.adapter.WifiSavedAdapter
import java.io.File

@AndroidEntryPoint
class FragmentWifiSaved : Fragment() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }

    private var _binding: FragmentWifiSavedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SavedWifiListViewModel by viewModels()

    private lateinit var adapter: WifiSavedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWifiSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = WifiSavedAdapter()

        binding.networkRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FragmentWifiSaved.adapter
        }

        viewModel.getAllWifi().asLiveData().observe(this.viewLifecycleOwner) { wifiList ->
            adapter.updateNetworks(wifiList)
            if (wifiList.isNotEmpty()) binding.exportButton.isEnabled = true
            binding.exportButton.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val resolver = requireContext().contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Files.FileColumns.DISPLAY_NAME, "wifi_list.json")
                        put(MediaStore.Files.FileColumns.MIME_TYPE, "application/json")
                        put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/my_app_folder")
                    }
                    val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                    uri?.let { data ->
                        resolver.openOutputStream(data)?.use { outputStream ->
                            val gson = Gson()
                            val jsonString = gson.toJson(wifiList)
                            outputStream.write(jsonString.toByteArray())
                            outputStream.flush()
                            outputStream.close()
                        }
                        Toast.makeText(requireContext(), "Wifi list is exported!", Toast.LENGTH_LONG).show()
                    } ?: run {
                        Toast.makeText(requireContext(), "Failed to create file!", Toast.LENGTH_LONG).show()
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissions(
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            PERMISSION_REQUEST_CODE
                        )
                    } else {
                        val gson = Gson()
                        val jsonString = gson.toJson(wifiList)
                        val file = File(
                            Environment.getExternalStorageDirectory().absolutePath +
                                    "/my_app_folder",
                            "wifi_list.json"
                        )
                        file.parentFile?.mkdirs()
                        file.writeText(jsonString)
                        Toast.makeText(requireContext(), "Wifi list is exported!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}