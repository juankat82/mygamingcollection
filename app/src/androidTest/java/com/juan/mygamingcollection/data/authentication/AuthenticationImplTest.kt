package com.juan.mygamingcollection.data.authentication

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.juan.mygamingcollection.data.preferences.MyPreferences
import io.mockk.coEvery
import io.mockk.mockkObject
import io.mockk.spyk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthenticationImplTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    lateinit var auth: FirebaseAuth
    var user:FirebaseUser? = null
    val userEmail = "myemailR01_@gmail.com"
    @Before
    fun setup() {
        mockkObject(MyPreferences.shared)
        auth = spyk<FirebaseAuth>(Firebase.auth)
        user = auth.currentUser
    }

    @Test
    fun `test check if user is logged in`() {
        var resultEmail: String? = ""
        coEvery { auth.currentUser?.email } returns userEmail
        runTest{
           resultEmail = auth.currentUser?.email
        }
        Log.i("RESULTEMAIL IS: ", resultEmail ?: "")
        Assert.assertEquals(resultEmail, userEmail)
    }
}