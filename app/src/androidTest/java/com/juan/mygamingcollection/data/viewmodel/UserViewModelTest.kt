package com.juan.mygamingcollection.data.viewmodel

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.juan.mygamingcollection.data.authentication.AuthenticationImpl
import com.juan.mygamingcollection.data.preferences.MyPreferences
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    var context = InstrumentationRegistry.getInstrumentation().context
    lateinit var userViewModel:UserViewModel
    val username = "Usernametest_1@mydomain.com"
    lateinit var preferences: MyPreferences
    lateinit var authentication: AuthenticationImpl
    lateinit var screenViewModel: ScreenViewModel
    lateinit var auth: FirebaseAuth
    lateinit var googleIdTokenCredential: GoogleIdTokenCredential
    val credentialManager = CredentialManager.create(context)

    @Before
    fun setup() {
        authentication = spyk<AuthenticationImpl>()
        preferences = spyk<MyPreferences>()
        userViewModel = spyk(UserViewModel(authentication))
        screenViewModel = spyk(ScreenViewModel())
        mockkObject(MyPreferences.shared)
        auth =  spyk(Firebase.auth)
    }

    @Test
    fun `test setUser`() {
        val result = userViewModel.setUser(username)
        Log.i("USERNAMEIS: ",result)
        Assert.assertEquals(result,"Usernametest_1")
    }

    @Test
    fun `test login user`() {
        every { authentication.attemptToLogin(userViewModel, username.split("@").get(0), "contrasena_1", context, screenViewModel) } returns 1
        authentication.attemptToLogin(userViewModel, username.split("@").get(0), "contrasena_1", context, screenViewModel)
        verify{authentication.attemptToLogin(userViewModel, username.split("@").get(0), "contrasena_1", context, screenViewModel)}
        confirmVerified(authentication)
    }

    @Test
    fun `user is logout`() {
        userViewModel.logoutUser()
        verify{userViewModel.logoutUser()}
    }

    @Test
    fun `test login with credentials`() {

        val signInWithGoogleOptionButton =
            GetSignInWithGoogleOption.Builder("120915935901-hph0kd14v3pbammcs4urb0ctifuucqsj.apps.googleusercontent.com")
                .build()
        val request: GetCredentialRequest = GetCredentialRequest.Builder().addCredentialOption(signInWithGoogleOptionButton).build()
        var credential: Credential
        var useremail = ""
        runTest (StandardTestDispatcher()) {
            val result = credentialManager.getCredential(request = request, context = context)
            credential = result.credential
            googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            useremail = googleIdTokenCredential.id
            userViewModel.loginUserWithGoogleCredentials(googleIdTokenCredential, context, useremail)
            Log.i("USEREMAIL", useremail)
        }

        coVerify  { userViewModel.loginUserWithGoogleCredentials(googleIdTokenCredential, context, useremail) }
        coVerify{ MyPreferences.shared.setCurrentUserEmail(any())}
    }

    @Test
    fun `register user`() {
        userViewModel.registerUser(context, username, "Contrasena_1")
        coVerify{MyPreferences.shared.setCurrentUserEmail(any())}
    }

    @Test
    fun  `reset password`() {
        authentication.resetPassword(username, context)
        verify{authentication.resetPassword(any(), context)}
    }
}