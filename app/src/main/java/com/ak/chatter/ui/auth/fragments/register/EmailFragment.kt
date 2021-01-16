package com.ak.chatter.ui.auth.fragments.register

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupActionBarWithNavController
import com.ak.chatter.R
import com.ak.chatter.databinding.FragmentEmailBinding
import com.ak.chatter.util.Constants.EMAIL
import com.ak.chatter.util.MyDataStore
import kotlinx.coroutines.launch


class EmailFragment : Fragment() {

    private var _binding: FragmentEmailBinding? = null
    private val binding get() = _binding!!

    private val navArgs: EmailFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        binding.buttonContinue.setOnClickListener {
            checkIfEmailIsValidAndNavigate()
        }

        lifecycleScope.launch {
            val email = MyDataStore.read(EMAIL)

            if (email != null) {
                binding.textInputLayoutEmail.editText?.setText(email)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupToolbar() {
        val appCompat = requireActivity() as AppCompatActivity

        val navHostFragment = appCompat.supportFragmentManager.findFragmentById(R.id.nav_host_activity_auth) as NavHostFragment
        val navController = navHostFragment.findNavController()

        appCompat.setSupportActionBar(binding.toolbarRegister)
        appCompat.setupActionBarWithNavController(navController)
    }

    private fun checkIfEmailIsValidAndNavigate() {
        val email = binding.textInputLayoutEmail.editText?.text.toString().trim()

        if (email.isEmpty()) {
            binding.textInputLayoutEmail.error = "Please enter your email."
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textInputLayoutEmail.error = "Invalid Email Address."
        } else {
            binding.textInputLayoutEmail.error = null
            val action = EmailFragmentDirections.actionEmailFragmentToPasswordFragment(navArgs.firstName, navArgs.lastName, navArgs.birthday, navArgs.gender, email)
            findNavController().navigate(action)

            lifecycleScope.launch {
                MyDataStore.save(EMAIL, email)
            }
        }
    }
}