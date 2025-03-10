package com.juan.mygamingcollection.data.authentication

import android.app.Activity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.juan.mygamingcollection.data.preferences.MyPreferences
import com.juan.mygamingcollection.data.viewmodel.UserViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.lang.Exception
import java.util.concurrent.Executor

@RunWith(MockitoJUnitRunner::class)
class UnitTestAuthenticationImpl {

    var auth: FirebaseAuth = mockk<FirebaseAuth>()
    var user: FirebaseUser = mockk<FirebaseUser>()
    var userViewModel = mock<UserViewModel>()
    lateinit var successTask: Task<AuthResult>

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun init() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun checkUserIsLoggedIn() {
        every { user.email } returns { "dave"}.toString()
        val result = user.email
        verify { user.email }
        Assert.assertEquals(user.email, result)
    }

    @Test
    fun `verify attempt To Login`() {
        val userName = "user"
        val userPassword = "password_1"
        val successTask = object : Task<AuthResult>() {
            override fun addOnFailureListener(p0: OnFailureListener): Task<AuthResult> { TODO() }
            override fun addOnFailureListener(p0: Activity, p1: OnFailureListener): Task<AuthResult> {  TODO() }
            override fun addOnFailureListener(p0: Executor, p1: OnFailureListener): Task<AuthResult> {TODO()}
            override fun getException(): Exception? {return Exception("") }
            override fun getResult(): AuthResult { TODO() }
            override fun <X : Throwable?> getResult(p0: Class<X>): AuthResult {TODO()}
            override fun isCanceled(): Boolean {return false}
            override fun addOnSuccessListener(p0: Executor, p1: OnSuccessListener<in AuthResult>): Task<AuthResult> {TODO()}
            override fun addOnSuccessListener(p0: Activity, p1: OnSuccessListener<in AuthResult>): Task<AuthResult> { TODO() }
            override fun addOnSuccessListener(p0: OnSuccessListener<in AuthResult>): Task<AuthResult> {TODO()}
            override fun isComplete(): Boolean = true
            override fun isSuccessful(): Boolean = true
            override fun addOnCompleteListener(executor: Executor, onCompleteListener: OnCompleteListener<AuthResult>): Task<AuthResult> {
                onCompleteListener.onComplete(successTask)
                return successTask
            }
        }
        every { auth.signInWithEmailAndPassword(userName, userPassword).isSuccessful} returns true
        val result = auth.signInWithEmailAndPassword(userName, userPassword).isSuccessful
        System.out.println("IS SUCCESSFUL>: "+ (result == successTask.isSuccessful))
        Assert.assertEquals(result, successTask.isSuccessful)
    }

    @Test
    fun `test register New User`() {
        val successTask = object : Task<AuthResult>() {
            override fun addOnFailureListener(p0: OnFailureListener): Task<AuthResult> {  TODO() }
            override fun addOnFailureListener(p0: Activity, p1: OnFailureListener): Task<AuthResult> { TODO() }
            override fun addOnFailureListener(p0: Executor, p1: OnFailureListener): Task<AuthResult> {TODO()}
            override fun getException(): Exception {return Exception("") }
            override fun getResult(): AuthResult { TODO() }
            override fun <X : Throwable?> getResult(p0: Class<X>): AuthResult {TODO()}
            override fun isCanceled(): Boolean {return false}
            override fun addOnSuccessListener(p0: Executor, p1: OnSuccessListener<in AuthResult>): Task<AuthResult> {TODO()}
            override fun addOnSuccessListener(p0: Activity, p1: OnSuccessListener<in AuthResult>): Task<AuthResult> { TODO() }
            override fun addOnSuccessListener(p0: OnSuccessListener<in AuthResult>): Task<AuthResult> {TODO()}
            override fun isComplete(): Boolean = true
            override fun isSuccessful(): Boolean = true
            override fun addOnCompleteListener(executor: Executor, onCompleteListener: OnCompleteListener<AuthResult>): Task<AuthResult> {
                return successTask
            }
        }
        val firebaseCredential = GoogleAuthProvider.getCredential("idToken", null)
        every { auth.signInWithCredential(firebaseCredential).isSuccessful} returns true
        val result = auth.signInWithCredential(firebaseCredential).isSuccessful
        Assert.assertEquals(result, successTask.isSuccessful)
    }

    @Test
    fun `test preferences`() {
        val dispatcher = StandardTestDispatcher()
        val dispatcher2 = StandardTestDispatcher()
        val email = "Its_myexample01@hotmail.com"
        val preferences = spyk<MyPreferences.Companion>()
        //WHEN for both shared preferences tests
        coEvery { preferences.shared.setCurrentUserEmail(any<String>()) } returns true
        coEvery { preferences.shared.setIsGoogleLogin(any()) } returns true
        //Test we set a username using the email on preferences
        var isCurrentUserEmailSet = false
        GlobalScope.launch(dispatcher) { isCurrentUserEmailSet = preferences.shared.setCurrentUserEmail(email.split("@").get(0)) }
        dispatcher.scheduler.advanceUntilIdle()
        Assert.assertEquals(isCurrentUserEmailSet, true)
        //Test we do login with google
        var isGoogleLogin = false
        GlobalScope.launch(dispatcher2) { isGoogleLogin = runBlocking { preferences.shared.setIsGoogleLogin(true) } }
        dispatcher2.scheduler.advanceUntilIdle()
        Assert.assertEquals(true,isGoogleLogin)
    }

    @Test
    fun `verify registerNewUser`() {
        val userName = "a valid username"
        val userPassword = "a valid password"
        val successTask = object : Task<AuthResult>() {
            override fun addOnFailureListener(p0: OnFailureListener): Task<AuthResult> { TODO("Not yet implemented") }
            override fun addOnFailureListener(p0: Activity, p1: OnFailureListener): Task<AuthResult> { TODO("Not yet implemented") }
            override fun addOnFailureListener(p0: Executor, p1: OnFailureListener): Task<AuthResult> {TODO()}
            override fun getException(): Exception {return Exception("") }
            override fun getResult(): AuthResult { TODO() }
            override fun <X : Throwable?> getResult(p0: Class<X>): AuthResult {TODO()}
            override fun isCanceled(): Boolean {return false}
            override fun addOnSuccessListener(p0: Executor, p1: OnSuccessListener<in AuthResult>): Task<AuthResult> {TODO()}
            override fun addOnSuccessListener(p0: Activity, p1: OnSuccessListener<in AuthResult>): Task<AuthResult> { TODO() }
            override fun addOnSuccessListener(p0: OnSuccessListener<in AuthResult>): Task<AuthResult> {TODO()}
            override fun isComplete(): Boolean = true
            override fun isSuccessful(): Boolean = true
            override fun addOnCompleteListener(executor: Executor, onCompleteListener: OnCompleteListener<AuthResult>): Task<AuthResult> {
                return successTask
            }
        }
        every {auth.createUserWithEmailAndPassword(userName, userPassword).isSuccessful} returns successTask.isSuccessful
        val result = auth.createUserWithEmailAndPassword(userName, userPassword).isSuccessful
        Assert.assertEquals(result, true)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun afterEach() {
        Dispatchers.resetMain()
    }
}