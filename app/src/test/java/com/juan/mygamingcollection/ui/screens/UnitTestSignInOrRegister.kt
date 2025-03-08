package com.juan.mygamingcollection.ui.screens

import org.apache.commons.validator.routines.EmailValidator
import org.junit.Assert
import org.junit.Test

const val REGEX_STRONG_PASSWORD = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9])(?=.{8,})"

class UnitTestSignInOrRegister {

    enum class StrengthPasswordTypes {
        STRONG,
        WEAK
    }

    @Test
    fun `validate email address`() {
        val emailN = "myemail" //DOESNT PASS
        val emailY = "itsmyemal21@gmail.com" //PASS
        Assert.assertTrue(EmailValidator.getInstance().isValid(emailY))
    }

    @Test
    fun `is password strong test`() {
        val passwordN = "abcdE"
        val passwordY = "Password_231"

        Assert.assertEquals(isPasswordStrong(passwordN), StrengthPasswordTypes.WEAK)
        Assert.assertEquals(isPasswordStrong(passwordY), StrengthPasswordTypes.STRONG)
    }

    fun isPasswordStrong(password:String) : StrengthPasswordTypes {
        return when {
            REGEX_STRONG_PASSWORD.toRegex().containsMatchIn(password) -> StrengthPasswordTypes.STRONG
            else -> StrengthPasswordTypes.WEAK
        }
    }
}