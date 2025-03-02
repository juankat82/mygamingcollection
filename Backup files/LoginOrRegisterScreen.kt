package com.juan.mygamingcollection.ui.screens

import android.content.Context
import android.util.Log
import androidx.credentials.GetCredentialRequest
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.navigation.NavHostController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.juan.mygamingcollection.MainActivity
import com.juan.mygamingcollection.R
import com.juan.mygamingcollection.data.viewmodel.UserViewModel
import com.juan.mygamingcollection.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.apache.commons.validator.routines.EmailValidator

private enum class StrengthPasswordTypes {
    STRONG,
    WEAK
}
private const val REGEX_STRONG_PASSWORD = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9])(?=.{8,})"
lateinit var keyboardController:SoftwareKeyboardController

//lateinit var oneTapClient: SignInClient = Identity.getSignInClient(context.applicationContext)

@Composable
fun SignInOrRegister(
    navHostController: NavHostController
)  {
    val context = LocalContext.current
    //Pass this request to getCredential()///////////////
    val signWithGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(context.getString(R.string.servers_client_id))
        .setAutoSelectEnabled(true).build()
    val signInWithGoogleOptionButton = GetSignInWithGoogleOption.
        Builder(context.getString(R.string.servers_client_id)).build()
    val registerWithGoogle: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(context.getString(R.string.servers_client_id))
        .build()

    ////////////////////////////////////////////////////
    //True==login mode, False==register mode
    val isLoginModeOrRegisterMode = rememberSaveable { mutableStateOf(true) }

    val userViewModel = UserViewModel()
    keyboardController = LocalSoftwareKeyboardController.current!!
    val shouldShowExitDialog = rememberSaveable{mutableStateOf(false)}


    if (shouldShowExitDialog.value)
        ExitAlertDialog(context = context, isLoginModeOrRegister = isLoginModeOrRegisterMode, shouldShowExitDialog)
    else
        shouldShowExitDialog.value = false

    BackHandler {
        if (isLoginModeOrRegisterMode.value)
            shouldShowExitDialog.value = true
        else
            isLoginModeOrRegisterMode.value = true
    }
    Box( modifier = Modifier
        .clickable { keyboardController?.hide() }
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primaryContainer)) {
        Row (modifier = Modifier
            .background(colorResource(id = R.color.lime))
            .fillMaxWidth()
            .height(45.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.Center){
            Text(modifier = Modifier.align(Alignment.CenterVertically), fontSize = 15.sp, text = stringResource(id = R.string.login_signin_title), color = Color.Black, fontFamily = FontFamily(
                Font(R.font.super_mario_bros_font)
            ))
        }
        Surface(
            modifier = Modifier
                .clickable { keyboardController.hide() }
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 80.dp),
            color = colorResource(id = R.color.lime),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(2.dp, Color.LightGray),
            shadowElevation = 5.dp,
            tonalElevation = 5.dp,
            content = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isLoginModeOrRegisterMode.value)
                        LoginScreen( signInWithGoogleOptionButton, navHostController, isLoginModeOrRegisterMode, userViewModel, context)
                    else
                        RegisterScreen(navHostController, isLoginModeOrRegisterMode,userViewModel)
                }
            }
        )
    }
}

@Composable
private fun LoginScreen(
    signInWithGoogleOptionButton: GetSignInWithGoogleOption,
    navHostController: NavHostController,
    isLoginMode: MutableState<Boolean>,
    userViewModel: UserViewModel,
    context: Context
) {
    val isUserNameErrorTextHidden = rememberSaveable { mutableStateOf(0) }
    val isUserPasswordErrorTextHidden = rememberSaveable { mutableStateOf(0) }
    val userName = rememberSaveable { mutableStateOf("") }
    val userPassword = rememberSaveable { mutableStateOf("") }
    val isPasswordVisible = rememberSaveable{ mutableStateOf(true) }
    val visualTransformationHidden = PasswordVisualTransformation()
    val visualTransformationOpen = VisualTransformation.None
    val coroutineScope = rememberCoroutineScope()


    OutlinedTextField(
        value = userName.value,
        singleLine = true,
        minLines = 1,
        maxLines = 1,
        onValueChange = {
            userName.value = it
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        label = {
            Text(text = stringResource(id = R.string.user_email), color = Color.Black)
        })
    //0-hidden, 1-Field is Required, 2-Password is too Weak
    Text(text =
    when (isUserNameErrorTextHidden.value) {
        1 -> {
            stringResource(id = R.string.empty_username_text)}
        2 -> {
            stringResource(id = R.string.invalid_username_text)}
        else -> ""
    }, color = Color.Red, fontStyle = FontStyle.Italic, modifier = Modifier.padding(top = 2.dp, bottom = 2.dp))
    OutlinedTextField(
        value = userPassword.value,
        singleLine = true,
        minLines = 1,
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        visualTransformation =
        if (isPasswordVisible.value)
            visualTransformationHidden
        else
            visualTransformationOpen,
        onValueChange = { userPassword.value = it },
        shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp),
        label = { Text(text = stringResource(id = R.string.user_password), color = Color.Black) },
        trailingIcon = {
            IconButton(onClick = { isPasswordVisible.value = !isPasswordVisible.value }) {
                Icon(painterResource(id =
                if (isPasswordVisible.value)
                    R.drawable.visibility_on
                else
                    R.drawable.visibility_off),
                    contentDescription = "password vivibility")
            }
        }
    )
    //0-hidden, 1-Field is Required, 2-Password is too Weak
    Text(text = when(isUserPasswordErrorTextHidden.value) {
        1 -> {
            stringResource(id = R.string.password_is_empty)}
        2 -> {
            stringResource(id = R.string.weak_password)}
        else -> ""
    }, color = Color.Red, textAlign = TextAlign.Center, fontStyle = FontStyle.Italic, modifier = Modifier.padding(vertical = 2.dp))

    ElevatedButton(onClick = {
        val isUserNameValid = isEmailValid(userName.value.trim())
        val isUserPasswordValidCode = isPasswordStrong(userPassword.value)

        if (userName.value.trim().isEmpty()) {
            isUserNameErrorTextHidden.value = 1
        }
        else {
            if (!isUserNameValid)
                isUserNameErrorTextHidden.value = 2
            else
                isUserNameErrorTextHidden.value = 0
        }

        if (userPassword.value.trim().isEmpty())
            isUserPasswordErrorTextHidden.value = 1
        else {
            if (isUserPasswordValidCode == StrengthPasswordTypes.WEAK)
                isUserPasswordErrorTextHidden.value = 2
            else
                isUserPasswordErrorTextHidden.value = 0
        }

        if (isUserNameErrorTextHidden.value == 0 && isUserPasswordErrorTextHidden.value ==0) {
            userViewModel.loginUser(navHostController.context, User(userName.value, userPassword.value, false))
        }
    }, modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = stringResource(id = R.string.login))
    }
    Row( modifier = Modifier.padding(vertical = 8.dp)) {
        TextButton(onClick = {
            isLoginMode.value = false
        }) {
            Text(text = stringResource(id = R.string.register_a))
            Text(text = stringResource(id = R.string.register_b), textDecoration = TextDecoration.Underline)
        }
    }


    ElevatedButton(
        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        elevation = ButtonDefaults.elevatedButtonElevation(4.dp),
        onClick = {
            getGoogleCredentials(context, signInWithGoogleOptionButton, coroutineScope, userViewModel)

        },
        shape = RoundedCornerShape(6.dp)) {
        Row (modifier = Modifier, horizontalArrangement = Arrangement.Center){
            Image(painter = painterResource(id = R.drawable.goog_icon), contentDescription = "login_google", modifier = Modifier.padding(end = 8.dp, top = 3.dp))
            Text(text = stringResource(id = R.string.sign_in), modifier = Modifier, textAlign = TextAlign.Left)
        }
    }
}
private fun getGoogleCredentials(
    context: Context,
    signInWithGoogleOptionButton: GetSignInWithGoogleOption,
    coroutineScope: CoroutineScope,
    userViewModel: UserViewModel
) {
    val request: GetCredentialRequest = GetCredentialRequest
        .Builder().addCredentialOption(signInWithGoogleOptionButton).build()
    val credentialManager = CredentialManager.create(context)
    coroutineScope.launch {
        try {
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )
            handleSignIn(result, userViewModel, context)
        } catch(e: Exception) {
            handleFailure(e)
        }
    }
}

private fun handleSignIn(
    result: GetCredentialResponse,
    userViewModel: UserViewModel,
    context: Context
) {
    val credential = result.credential
    when(credential) {
        is PublicKeyCredential -> {
            val responseJson = credential.authenticationResponseJson
        }
        is PasswordCredential -> {
            val user = credential.id
            val password = credential.password
            userViewModel.loginUser(context, User(user, password, true))
        }
        is CustomCredential -> {
            if (credential.type ==  GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                // Use googleIdTokenCredential and extract id to validate and
                // authenticate on your server.
                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    userViewModel.loginUserWithGoogleCredentials(googleIdTokenCredential)

                } catch (e: Exception) {
                    // Catch any unrecognized custom credential type here.
                    Log.e("Received an invalid google id token response", e.stackTraceToString())
                }
            } else {
                Log.e("UNEXPECTED_EXCEPTION", "Unexpected type of credential")
            }
        }
        else -> {
            // Catch any unrecognized credential type here.
            Log.e("UNEXPECTED_CREDENTIAL", "Unexpected type of credential")
        }
    }
}

private fun handleFailure(e: Exception) {
    Log.i("FALED ATTEMPT", e.stackTraceToString())
}
@Composable
private fun RegisterScreen(
    navHostController: NavHostController,
    isRegisterMode: MutableState<Boolean>,
    userViewModel: UserViewModel
) {

    val visualTransformationHidden = PasswordVisualTransformation()
    val visualTransformationOpen = VisualTransformation.None
    val newEmail = rememberSaveable { mutableStateOf("") }
    val newPassword = rememberSaveable { mutableStateOf("") }
    val newPasswordRepeat = rememberSaveable { mutableStateOf("") }
    val isPasswordVisible = rememberSaveable{ mutableStateOf(false) }
    val isNewUserNameErrorTextHidden = rememberSaveable { mutableStateOf(0) }
    val isNewUserPasswordErrorTextHidden = rememberSaveable { mutableStateOf(-1) }

    OutlinedTextField(
        value = newEmail.value,
        minLines = 1,
        maxLines = 1,
        singleLine = true,
        onValueChange = {
            if (it.length<50)
                newEmail.value = it
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        label = {
            Text(text = stringResource(id = R.string.new_user_email), color = Color.Black)
        })
    Text(text = when (isNewUserNameErrorTextHidden.value) {
                    1 -> {
                        stringResource(id = R.string.empty_username_text)
                    }

                    2 -> {
                        stringResource(id = R.string.invalid_username_text)
                    }
                    else -> ""
                },
        color = Color.Red,
        fontStyle = FontStyle.Italic,
        modifier = Modifier.padding(top = 2.dp, bottom = 2.dp))

    OutlinedTextField(
        value = newPassword.value,
        minLines = 1,
        maxLines = 2,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        visualTransformation =
            if (isPasswordVisible.value)
                visualTransformationOpen
            else
                visualTransformationHidden
        ,
        onValueChange = { if (it.length<50) newPassword.value = it },
        shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp),
        label = { Text(text = stringResource(id = R.string.new_user_password), color = Color.Black) },
        trailingIcon = {
            IconButton(onClick = { isPasswordVisible.value = !isPasswordVisible.value }) {
                Icon(painterResource(id =
                    if (isPasswordVisible.value)
                        R.drawable.visibility_on
                    else
                        R.drawable.visibility_off),
                    contentDescription = "password wisibility")
            }
        }
    )
    Text(text = when (isNewUserPasswordErrorTextHidden.value) {
                1 -> {
                    stringResource(id = R.string.password_is_empty)}
                2 -> {
                    stringResource(id = R.string.weak_password)}
                else -> ""
            },
        color = Color.Red,
        fontStyle = FontStyle.Italic,
        modifier = Modifier.padding(top = 2.dp, bottom = 2.dp))

    OutlinedTextField(
        value = newPasswordRepeat.value,
        singleLine = true,
        minLines = 1,
        maxLines = 1,
        textStyle = TextStyle(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        visualTransformation =
            if (isPasswordVisible.value)
                visualTransformationOpen
            else
                visualTransformationHidden,
        onValueChange = { if (it.length<27) newPasswordRepeat.value = it },
        shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp),
        label = { Text(text = stringResource(id = R.string.new_user_repeat_password), color = Color.Black) },
        trailingIcon = {
        }
    )
    Text(text = when (isNewUserPasswordErrorTextHidden.value) {
        0,1,2-> ""
        3 -> stringResource(id = R.string.new_user_repeat_password_doesnt_match)
        else -> ""
    },
        color = Color.Red,
        fontStyle = FontStyle.Italic,
        modifier = Modifier.padding(top = 2.dp, bottom = 2.dp))

    ElevatedButton(onClick = {
        //0-hidden, 1-Field is Required, 2-Password is too Weak, 3-(Passworsds are different)
        val isUserNameValid = isEmailValid(newEmail.value.trim())
        val isUserPasswordValidCode = isPasswordStrong(newPassword.value)
        if (newEmail.value.trim().isEmpty()) {
            isNewUserNameErrorTextHidden.value = 1
        }
        else {
            if (!isUserNameValid)
                isNewUserNameErrorTextHidden.value = 2
            else
                isNewUserNameErrorTextHidden.value = 0
        }

        if (newPassword.value.trim().isEmpty())
            isNewUserPasswordErrorTextHidden.value = 1
        else {
            if (isUserPasswordValidCode == StrengthPasswordTypes.WEAK)
                isNewUserPasswordErrorTextHidden.value = 2
            else
                isNewUserPasswordErrorTextHidden.value = 0
        }

        if (!newPassword.value.trim().equals(newPasswordRepeat.value.trim())) {
            if (isNewUserPasswordErrorTextHidden.value != 2)
                isNewUserPasswordErrorTextHidden.value = 3
        }


        if (isNewUserNameErrorTextHidden.value == 0 && isNewUserPasswordErrorTextHidden.value == 0) {
            userViewModel.registerUser(navHostController.context, User(newEmail.value, newPassword.value, true))
            keyboardController.hide()
        }
    }, modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = stringResource(id = R.string.new_user_register))
    }
}

@Composable
private fun ExitAlertDialog(
    context: Context,
    isLoginModeOrRegister: MutableState<Boolean>,
    shouldShowDialog: MutableState<Boolean>
) {
    val textExitApp = R.string.want_to_quit_question
    val titleBackToLoginScreen = R.string.want_to_come_back_to_login
    val chosenText = rememberSaveable{mutableStateOf("")}

    if (isLoginModeOrRegister.value)
        chosenText.value = stringResource(id = textExitApp)
    else
        chosenText.value = stringResource(id = titleBackToLoginScreen)

    AlertDialog(
        modifier = Modifier.background(Color.LightGray),
        onDismissRequest = {  shouldShowDialog.value = false },
        confirmButton = {
            TextButton(onClick = {
                if (isLoginModeOrRegister.value) {
                    (context as MainActivity).finish()
                }
                else {
                    isLoginModeOrRegister.value = true
                }
            })
            { Text(text = stringResource(id = android.R.string.ok)) }
        },
        dismissButton = {
            TextButton(onClick = { shouldShowDialog.value = false})
            { Text(text = stringResource(id = android.R.string.cancel)) }
        },
        title = { Text(textDecoration = TextDecoration.Underline, text = stringResource(id = R.string.confirm_title)) },
        text = { Text(textAlign = TextAlign.Center, text = chosenText.value) },
        shape = RoundedCornerShape(8.dp),
        icon = { Icons.Rounded.Close }
    )
}

private fun isEmailValid(email: String) = EmailValidator.getInstance().isValid(email)

private fun isPasswordStrong(password:String) : StrengthPasswordTypes {
    return when {
        REGEX_STRONG_PASSWORD.toRegex().containsMatchIn(password) -> StrengthPasswordTypes.STRONG
        else -> StrengthPasswordTypes.WEAK
    }
}