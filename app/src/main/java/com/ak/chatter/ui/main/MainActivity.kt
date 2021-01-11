package com.ak.chatter.ui.main

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ak.chatter.R
import com.ak.chatter.databinding.ActivityMainBinding
import com.ak.chatter.util.KeyboardBehaviour

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Chatter)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar()
        setUpBottomNavigationViewAndFragmentContainer()
        handleBottomNavigationView(navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onSupportNavigateUp(): Boolean {
        KeyboardBehaviour.closeKeyboard(this)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun handleBottomNavigationView(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    showBottomNavigationView()
                }

                R.id.searchFragment -> {
                    showBottomNavigationView()
                }

                R.id.newPostFragment -> {
                    hideBottomNavigationView()
                }

                R.id.notificationsFragment -> {
                    showBottomNavigationView()
                }

                R.id.profileFragment -> {
                    showBottomNavigationView()
                }

                R.id.settingsFragment -> {
                    hideBottomNavigationView()
                }
            }
        }
    }

    private fun setUpToolbar() {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        val navController = navHost.findNavController()
        val appCompat = this as AppCompatActivity
        val appBarConfiguration = AppBarConfiguration.Builder(R.id.homeFragment, R.id.searchFragment, R.id.notificationsFragment, R.id.profileFragment).build()

        appCompat.setSupportActionBar(binding.toolbar)
        appCompat.setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setUpBottomNavigationViewAndFragmentContainer() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        navController = navHostFragment.findNavController()
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun showBottomNavigationView() {
        binding.bottomNavigationView.visibility = VISIBLE
    }

    private fun hideBottomNavigationView() {
        binding.bottomNavigationView.visibility = GONE
    }
}