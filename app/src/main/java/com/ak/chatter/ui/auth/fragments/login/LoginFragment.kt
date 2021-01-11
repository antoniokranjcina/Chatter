package com.ak.chatter.ui.auth.fragments.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ak.chatter.databinding.FragmentLoginBinding
import com.ak.chatter.ui.main.MainActivity
import com.ak.chatter.util.Constants.EMAIL_AND_PASSWORD_CANNOT_BE_EMPTY
import com.ak.chatter.util.KeyboardBehaviour
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


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

            Firebase.auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    navigateToMainActivity()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), it.message, LENGTH_LONG).show()
                    hideLoading()
                }
        } else {
            Toast.makeText(requireContext(), EMAIL_AND_PASSWORD_CANNOT_BE_EMPTY, LENGTH_LONG).show()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
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