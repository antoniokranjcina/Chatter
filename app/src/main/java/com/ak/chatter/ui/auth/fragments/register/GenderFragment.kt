package com.ak.chatter.ui.auth.fragments.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupActionBarWithNavController
import com.ak.chatter.R
import com.ak.chatter.databinding.FragmentGenderBinding
import com.ak.chatter.util.Constants.GENDER
import com.ak.chatter.util.Constants.GENDER_FEMALE
import com.ak.chatter.util.Constants.GENDER_MALE
import com.ak.chatter.util.Constants.GENDER_OTHER
import com.ak.chatter.util.Constants.SELECT_GENDER
import com.ak.chatter.util.MyDataStore
import kotlinx.coroutines.launch

class GenderFragment : Fragment() {

    private var _binding: FragmentGenderBinding? = null
    private val binding get() = _binding!!

    private val navArgs: GenderFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGenderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        binding.buttonContinue.setOnClickListener {
            selectGender()
        }

        loadGender()
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

    private fun selectGender() {
        val genderRadioButtonId = binding.radioGroup.checkedRadioButtonId

        if (genderRadioButtonId != -1) {
            var genderRadioButton = GENDER_MALE
            var currentlySelectedButton = 1

            when (genderRadioButtonId) {
                binding.radioButtonMale.id -> {
                    currentlySelectedButton = 1
                    genderRadioButton = GENDER_MALE
                }

                binding.radioButtonFemale.id -> {
                    currentlySelectedButton = 2
                    genderRadioButton = GENDER_FEMALE
                }

                binding.radioButtonOther.id -> {
                    currentlySelectedButton = 3
                    genderRadioButton = GENDER_OTHER
                }
            }

            val action = GenderFragmentDirections.actionGenderFragmentToEmailFragment(navArgs.firstName, navArgs.birthday, navArgs.lastName, genderRadioButton)
            findNavController().navigate(action)

            lifecycleScope.launch {
                MyDataStore.save(GENDER, currentlySelectedButton.toString())
            }
        } else {
            Toast.makeText(requireContext(), SELECT_GENDER, LENGTH_LONG).show()
        }
    }

    private fun loadGender() {
        lifecycleScope.launch {
            val selectedGender = MyDataStore.read(GENDER)

            if (selectedGender != null) {
                when (selectedGender.toInt()) {
                    1 -> binding.radioGroup.check(binding.radioButtonMale.id)
                    2 -> binding.radioGroup.check(binding.radioButtonFemale.id)
                    3 -> binding.radioGroup.check(binding.radioButtonOther.id)
                }
            }
        }
    }
}