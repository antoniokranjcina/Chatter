package com.ak.chatter.util

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.MutableLiveData
import com.ak.chatter.R
import com.ak.chatter.util.Constants.MEDIUM
import com.ak.chatter.util.Constants.STRONG
import com.ak.chatter.util.Constants.WEAK
import java.util.regex.Matcher
import java.util.regex.Pattern

class PasswordStrengthCalculator : TextWatcher {

    var strengthLevel: MutableLiveData<String> = MutableLiveData()
    var strengthColor: MutableLiveData<Int> = MutableLiveData()

    var lowerCase: MutableLiveData<Int> = MutableLiveData(0)
    var upperCase: MutableLiveData<Int> = MutableLiveData(0)
    var digit: MutableLiveData<Int> = MutableLiveData(0)

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun afterTextChanged(p0: Editable?) {}
    override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (char != null) {
            lowerCase.value = if (char.hasLowerCase()) 1 else 0
            upperCase.value = if (char.hasUpperCase()) 1 else 0
            digit.value = if (char.hasDigit()) 1 else 0
            calculateStrength(char)
        }
    }

    private fun calculateStrength(password: CharSequence) {
        if (password.length in 0..7) {
            strengthColor.value = R.color.weak
            strengthLevel.value = WEAK
        } else if (password.length in 8..10) {
            if (lowerCase.value == 1 || upperCase.value == 1 || digit.value == 1) {
                strengthColor.value = R.color.medium
                strengthLevel.value = MEDIUM
            }
        } else if (password.length > 11) {
            if (lowerCase.value == 1 || upperCase.value == 1 || digit.value == 1) {
                if (lowerCase.value == 1 && upperCase.value == 1) {
                    strengthColor.value = R.color.strong
                    strengthLevel.value = STRONG
                }
            }
        }
    }

    private fun CharSequence.hasLowerCase(): Boolean {
        val pattern: Pattern = Pattern.compile("[a-z]")
        val hasLowerCase: Matcher = pattern.matcher(this)
        return hasLowerCase.find()
    }

    private fun CharSequence.hasUpperCase(): Boolean {
        val pattern: Pattern = Pattern.compile("[A-Z]")
        val hasUpperCase: Matcher = pattern.matcher(this)
        return hasUpperCase.find()
    }

    private fun CharSequence.hasDigit(): Boolean {
        val pattern: Pattern = Pattern.compile("[0-9]")
        val hasDigit: Matcher = pattern.matcher(this)
        return hasDigit.find()
    }
}