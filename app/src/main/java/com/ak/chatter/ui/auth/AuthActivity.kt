package com.ak.chatter.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.ak.chatter.R
import com.ak.chatter.databinding.ActivityAuthBinding
import com.ak.chatter.util.KeyboardBehaviour

class AuthActivity : AppCompatActivity() {

    private var _binding: ActivityAuthBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Chatter)
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_activity_auth) as NavHostFragment
        navController = navHostFragment.findNavController()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onSupportNavigateUp(): Boolean {
        KeyboardBehaviour.closeKeyboard(this)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}