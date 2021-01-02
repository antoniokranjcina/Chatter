package com.ak.chatter.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.ak.chatter.R
import com.google.firebase.auth.FirebaseAuth

class TransitionFragment : Fragment(R.layout.fragment_transition) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val action: NavDirections = if (FirebaseAuth.getInstance().currentUser == null) {
            TransitionFragmentDirections.actionSplashScreenFragmentToLoginFragment()
        } else {
            TransitionFragmentDirections.actionSplashScreenFragmentToHomeFragment()
        }
        findNavController().navigate(action)
    }

}