package com.ak.chatter.ui.auth.fragments.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.ak.chatter.R
import com.ak.chatter.databinding.FragmentNameBinding
import com.ak.chatter.util.Constants.FIRST_NAME
import com.ak.chatter.util.Constants.LAST_NAME
import com.ak.chatter.util.KeyboardBehaviour
import com.ak.chatter.util.MyDataStore
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class NameFragment : Fragment() {

    private var _binding: FragmentNameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        binding.apply {
            textInputLayoutFirstName.editText?.requestFocus()
            KeyboardBehaviour.showKeyboard(requireActivity())

            buttonContinue.setOnClickListener {
                checkIfNameIsValidAndNavigate()
            }
        }

        loadName()
    }

    override fun onDetach() {
        super.onDetach()
        MainScope().launch {
            MyDataStore.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupToolbar() {
        val appCompat = requireActivity() as AppCompatActivity

        val navHostFragment = appCompat.supportFragmentManager.findFragmentById(R.id.nav_host_activity_main) as NavHostFragment
        val navController = navHostFragment.findNavController()

        appCompat.setSupportActionBar(binding.toolbarRegister)
        appCompat.setupActionBarWithNavController(navController)
    }

    private fun checkIfNameIsValidAndNavigate() {
        val firstName = binding.textInputLayoutFirstName.editText?.text.toString().trim()
        val lastName = binding.textInputLayoutLastName.editText?.text.toString().trim()

        var firstNameValid = false
        var lastNameValid = false

        binding.apply {
            when {
                firstName.isEmpty() -> {
                    textInputLayoutFirstName.error = "Last name cannot be empty."
                }
                firstName.length <= 1 -> {
                    textInputLayoutFirstName.error = "Last name cannot have less than 1 character."
                }
                else -> {
                    textInputLayoutFirstName.error = null
                    firstNameValid = true
                }
            }

            when {
                lastName.isEmpty() -> {
                    textInputLayoutLastName.error = "Last name cannot be empty."
                }
                lastName.length <= 1 -> {
                    textInputLayoutLastName.error = "Last name cannot have less than 1 character."
                }
                else -> {
                    textInputLayoutLastName.error = null
                    lastNameValid = true
                }
            }
        }

        if (firstNameValid && lastNameValid) {
            KeyboardBehaviour.closeKeyboard(requireActivity())
            val action = NameFragmentDirections.actionNameFragmentToBirthdayFragment(firstName, lastName)
            findNavController().navigate(action)

            lifecycleScope.launch {
                MyDataStore.save(FIRST_NAME, firstName)
                MyDataStore.save(LAST_NAME, lastName)
            }
        }
    }

    private fun loadName() {
        lifecycleScope.launch {
            val firstName = MyDataStore.read(FIRST_NAME)
            val lastName = MyDataStore.read(LAST_NAME)

            binding.apply {
                if (firstName != null && lastName != null) {
                    textInputLayoutFirstName.editText?.setText(firstName)
                    textInputLayoutLastName.editText?.setText(lastName)
                }
            }
        }
    }
}