package com.ak.chatter.ui.auth.fragments.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupActionBarWithNavController
import com.ak.chatter.R
import com.ak.chatter.data.model.User
import com.ak.chatter.databinding.FragmentPasswordBinding
import com.ak.chatter.ui.main.MainActivity
import com.ak.chatter.util.Constants.MEDIUM
import com.ak.chatter.util.Constants.STRONG
import com.ak.chatter.util.Constants.USERS
import com.ak.chatter.util.Constants.WEAK
import com.ak.chatter.util.Constants.WEAK_PASSWORD
import com.ak.chatter.util.KeyboardBehaviour
import com.ak.chatter.util.PasswordStrengthCalculator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class PasswordFragment : Fragment() {

    private var _binding: FragmentPasswordBinding? = null
    private val binding get() = _binding!!

    private val navArgs: PasswordFragmentArgs by navArgs()

    private val userCollectionRef = Firebase.firestore.collection(USERS)

    private val passwordStrengthCalculator = PasswordStrengthCalculator()
    private var color = R.color.weak
    private var strengthLevel = WEAK

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        passwordStrengthObservables()
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

    private fun passwordStrengthObservables() {
        binding.apply {
            buttonContinue.setOnClickListener {
                register()
            }
            textInputLayoutPassword.editText?.addTextChangedListener(passwordStrengthCalculator)
        }

        passwordStrengthCalculator.strengthLevel.observe(viewLifecycleOwner, {
            strengthLevel = it
            binding.apply {
                textViewPasswordStrength.apply {
                    text = strengthLevel
                    setTextColor(ContextCompat.getColor(requireContext(), color))
                }
            }
        })

        passwordStrengthCalculator.strengthColor.observe(viewLifecycleOwner, {
            color = it
        })
    }

    private fun register() {
        val password = binding.textInputLayoutPassword.editText?.text.toString()

        if (strengthLevel == MEDIUM || strengthLevel == STRONG) {
            showLoading()
            Firebase.auth.createUserWithEmailAndPassword(navArgs.email, password)
                .addOnSuccessListener {
                    saveUser()
                    navigateToMainActivity()
                }
                .addOnFailureListener {
                    hideLoading()
                    Toast.makeText(requireContext(), it.localizedMessage, LENGTH_LONG).show()
                }
        } else {
            binding.textInputLayoutPassword.error = WEAK_PASSWORD
        }
    }

    private fun saveUser() {
        val userUid = Firebase.auth.uid!!
        val user = User(
            uid = userUid,
            idUserDocument = "",
            firstName = navArgs.firstName,
            lastName = navArgs.lastName,
            birthday = navArgs.birthday,
            gender = navArgs.gender,
            email = navArgs.email
        )

        userCollectionRef.add(user)
            .addOnSuccessListener {
                val userDocumentId = it.id
                user.idUserDocument = userDocumentId

                userCollectionRef.document(userDocumentId).set(user)
                    .addOnSuccessListener {
                        Log.d(TAG, "saveUser: user added successfully")
                    }
                    .addOnFailureListener { userIdException ->
                        Log.e(TAG, "saveUser: ${userIdException.localizedMessage}")
                    }
            }
            .addOnFailureListener { userException ->
                Log.e(TAG, "saveUser: ${userException.localizedMessage}")
                Toast.makeText(requireContext(), userException.localizedMessage, LENGTH_LONG).show()
            }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showLoading() {
        KeyboardBehaviour.closeKeyboard(requireActivity())
        binding.apply {
            progressBar.visibility = View.VISIBLE
            constraintLayout.visibility = View.GONE
            toolbarRegister.visibility = View.GONE
        }
    }

    private fun hideLoading() {
        binding.apply {
            textInputLayoutPassword.editText?.requestFocus()
            KeyboardBehaviour.showKeyboard(requireActivity())
            progressBar.visibility = View.GONE
            constraintLayout.visibility = View.VISIBLE
            toolbarRegister.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val TAG = "PasswordFragment"
    }
}