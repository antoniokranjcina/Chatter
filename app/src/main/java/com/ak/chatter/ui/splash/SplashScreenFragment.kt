package com.ak.chatter.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.ak.chatter.R

class SplashScreenFragment : Fragment(R.layout.fragment_splash_screen) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val action: NavDirections = if (true) {
            SplashScreenFragmentDirections.actionSplashScreenFragmentToLoginFragment()
        } else {
            SplashScreenFragmentDirections.actionSplashScreenFragmentToHomeFragment()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(action)
        }, 650)
    }

}