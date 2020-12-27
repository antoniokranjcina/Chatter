package com.ak.chatter.ui.auth.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ak.chatter.databinding.FragmentLoginBinding
import com.ak.chatter.util.Constants.EMAIL_AND_PASSWORD_CANNOT_BE_EMPTY
import com.ak.chatter.util.Constants.UNEXPECTED_ERROR
import com.ak.chatter.util.Constants.WRONG_EMAIL_OR_PASSWORD
import com.ak.chatter.util.KeyboardBehaviour
import com.google.firebase.auth.*


class LoginFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var isEmailFocused: Boolean? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            binding.buttonLogin.id -> {
                login()
            }

            binding.textViewRegister.id -> {
                KeyboardBehaviour.closeKeyboard(requireActivity())
                val action = LoginFragmentDirections.actionLoginFragmentToNameFragment()
                findNavController().navigate(action)
            }
        }
    }

    private fun setOnClickListeners() {
        binding.apply {
            buttonLogin.setOnClickListener(this@LoginFragment)
            textViewRegister.setOnClickListener(this@LoginFragment)
        }
    }

    private fun login() {
        val email = binding.textInputLayoutEmail.editText?.text.toString().trim()
        val password = binding.textInputLayoutPassword.editText?.text.toString().trim()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            showLoading()
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                    findNavController().navigate(action)
                } else {
                    try {
                        if (task.exception != null) {
                            throw task.exception!!
                        } else {
                            Toast.makeText(requireContext(), UNEXPECTED_ERROR, LENGTH_SHORT).show()
                        }
                    } catch (e: FirebaseAuthInvalidUserException) {
                        Toast.makeText(requireContext(), WRONG_EMAIL_OR_PASSWORD, LENGTH_LONG).show()
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(requireContext(), WRONG_EMAIL_OR_PASSWORD, LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.message, LENGTH_LONG).show()
                    }
                    hideLoading()
                }
            }
        } else {
            Toast.makeText(requireContext(), EMAIL_AND_PASSWORD_CANNOT_BE_EMPTY, LENGTH_LONG).show()
        }
    }

    private fun showLoading() {
        binding.apply {
            isEmailFocused = textInputLayoutEmail.editText?.isFocused!!
            KeyboardBehaviour.closeKeyboard(requireActivity())
            textInputLayoutEmail.visibility = View.GONE
            textInputLayoutPassword.visibility = View.GONE
            buttonLogin.visibility = View.GONE
            textViewRegister.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        binding.apply {
            if (isEmailFocused!!) {
                textInputLayoutEmail.editText?.requestFocus()
            } else {
                textInputLayoutPassword.editText?.requestFocus()
            }
            KeyboardBehaviour.showKeyboard(requireActivity())

            textInputLayoutEmail.visibility = View.VISIBLE
            textInputLayoutPassword.visibility = View.VISIBLE
            buttonLogin.visibility = View.VISIBLE
            textViewRegister.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }
}