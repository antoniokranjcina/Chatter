package com.ak.chatter.ui.auth.fragments.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.ak.chatter.R
import com.ak.chatter.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListeners()
        setupToolbar()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {

        val action: NavDirections
        when (v!!.id) {
            binding.buttonRegister.id -> {
                action = RegisterFragmentDirections.actionRegisterFragmentToHomeFragment()
                findNavController().navigate(action)
            }
        }
    }

    private fun setupToolbar() {
        val appCompat = requireActivity() as AppCompatActivity

        val navHostFragment = appCompat.supportFragmentManager.findFragmentById(R.id.nav_host_activity_main) as NavHostFragment
        val navController = navHostFragment.findNavController()

        appCompat.setSupportActionBar(binding.toolbarRegister)
        appCompat.setupActionBarWithNavController(navController)
    }

    private fun setOnClickListeners() {
        binding.apply {
            buttonRegister.setOnClickListener(this@RegisterFragment)
        }
    }

}