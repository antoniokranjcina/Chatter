package com.ak.chatter.ui.auth.fragments.register

import android.annotation.SuppressLint
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
import com.ak.chatter.databinding.FragmentBirthdayBinding
import com.ak.chatter.util.Constants.BIRTHDAY_DAY
import com.ak.chatter.util.Constants.BIRTHDAY_MONTH
import com.ak.chatter.util.Constants.BIRTHDAY_YEAR
import com.ak.chatter.util.MyDataStore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class BirthdayFragment : Fragment() {

    private var _binding: FragmentBirthdayBinding? = null
    private val binding get() = _binding!!

    private val navArgs: BirthdayFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBirthdayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        binding.apply {
            datePicker.maxDate = Calendar.getInstance().timeInMillis
            buttonContinue.setOnClickListener {
                checkIfAgeIsValidAndNavigate()
            }
        }

        loadBirthday()
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

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("SimpleDateFormat")
    private fun checkIfAgeIsValidAndNavigate() {
        val day = binding.datePicker.dayOfMonth
        val month: Int = binding.datePicker.month + 1
        val year = binding.datePicker.year
        val birthday = "$day/$month/$year"

        val currentTime = Calendar.getInstance().timeInMillis
        val datePickerTime = SimpleDateFormat("dd/MM/yyyy").parse(birthday).time
        val differenceInDays = currentTime - datePickerTime
        val differenceInYears = TimeUnit.DAYS.convert(differenceInDays, TimeUnit.MILLISECONDS) / 365.0

        if (differenceInYears < 12) {
            Toast.makeText(requireContext(), "You have to be at least 12 years old.", LENGTH_LONG).show()
        } else {
            val action = BirthdayFragmentDirections.actionBirthdayFragmentToGenderFragment(navArgs.firstName, navArgs.lastName, birthday)
            findNavController().navigate(action)

            lifecycleScope.launch {
                MyDataStore.save(BIRTHDAY_DAY, day.toString())
                MyDataStore.save(BIRTHDAY_MONTH, month.toString())
                MyDataStore.save(BIRTHDAY_YEAR, year.toString())
            }
        }
    }

    private fun loadBirthday() {
        lifecycleScope.launch {
            val day = MyDataStore.read(BIRTHDAY_DAY)
            val month = MyDataStore.read(BIRTHDAY_MONTH)
            val year = MyDataStore.read(BIRTHDAY_YEAR)

            binding.apply {
                if (day != null && month != null && year != null) {
                    datePicker.updateDate(year.toInt(), month.toInt() - 1, day.toInt())
                }
            }
        }
    }
}