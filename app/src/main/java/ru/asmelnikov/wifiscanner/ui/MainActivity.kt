package ru.asmelnikov.wifiscanner.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import ru.asmelnikov.wifiscanner.R
import ru.asmelnikov.wifiscanner.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_nav)

        val navController = Navigation.findNavController(this, R.id.nav_fragment)

        NavigationUI.setupWithNavController(bottomNavigation, navController)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}