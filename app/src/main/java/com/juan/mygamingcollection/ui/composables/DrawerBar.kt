package com.juan.mygamingcollection.ui.composables

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.juan.mygamingcollection.MainActivity
import com.juan.mygamingcollection.R
import com.juan.mygamingcollection.data.firebaseDB.FirebaseDBConnectImpl
import com.juan.mygamingcollection.data.preferences.MyPreferences
import com.juan.mygamingcollection.data.viewmodel.ItemsViewModel
import com.juan.mygamingcollection.data.viewmodel.UserViewModel
import com.juan.mygamingcollection.ui.screens.StrengthPasswordTypes
import com.juan.mygamingcollection.ui.screens.backgroundColor
import com.juan.mygamingcollection.ui.screens.isPasswordStrong
import com.juan.mygamingcollection.ui.theme.PurpleGrey80
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

lateinit var shouldShowLogoutDialog: MutableState<Boolean>

@Composable
fun DrawerBar(
    drawerState: DrawerState,
    user: MutableState<FirebaseUser?>,
    context: Context,
    userViewModel: UserViewModel,
    itemsViewModel: ItemsViewModel,
    firebaseDBConnect: FirebaseDBConnectImpl
) {
    val items = listOf(
        Pair(R.drawable.delete_all_db, R.string.erase_everything_in_db),
        Pair(R.drawable.change_password_icon, R.string.change_password_text),
        Pair(R.drawable.logout_icon, R.string.logout_string)
    )
    val selectedItem = remember { mutableStateOf(items[0]) }
    var shouldShowWipeDBDialog by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val currentUser = user.value
    shouldShowLogoutDialog = remember { mutableStateOf(false) }
    var shouldShowChangePasswordDialog by remember { mutableStateOf(false) }
    val isGoogleLogin = MyPreferences.shared.getIsGoogleLogin()

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch {
            drawerState.close()
        }
    }

    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = stringResource(id = R.string.choose_option),
            modifier = Modifier
                .wrapContentSize(Alignment.TopStart)
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp, bottom = 4.dp),
            fontFamily = FontFamily(Font(R.font.super_mario_bros_font)),
            textAlign = TextAlign.Center,
            color = colorResource(id = R.color.black),
            fontSize = 20.sp
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
            color = PurpleGrey80,
            thickness = 2.dp
        )
        if (currentUser?.email.isNullOrEmpty()) {
            LaunchedEffect(key1 = "") {
                GlobalScope.launch {
                    drawerState.close()
                }
            }
        }
        else {
            items.forEach { item ->
                    if (!item.first.equals(R.drawable.upload_new_pictures)) {
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    painter = painterResource(id = item.first),
                                    contentDescription = context.getString(item.first)
                                )
                            },
                            label = {
                                Text(text = stringResource(id = item.second))
                            },
                            selected = item == selectedItem.value,
                            onClick = {
                                if (context.getString(item.second).equals(context.getString(R.string.erase_everything_in_db))) {
                                    shouldShowWipeDBDialog = true
                                }
                                if (context.getString(item.second).equals(context.getString(R.string.change_password_text))) {
                                    if (!isGoogleLogin) {
                                        shouldShowChangePasswordDialog = true
                                    }
                                    else {
                                        Toast.makeText(context, "Unavailable. You didn't login using a password.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                if (context.getString(item.second).equals(context.getString(R.string.logout_string))) {
                                    shouldShowLogoutDialog.value = true
                                }
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp)
                        )
                    }
                }
            }
    }

    if (shouldShowLogoutDialog.value) {
        AlertDialog(
            modifier = Modifier,
            title = {
                Text(text = stringResource(id = R.string.logout_string))
            },
            text = {
                Text(text = stringResource(id = R.string.would_you_logout))
            },
            shape = RectangleShape,
            containerColor = backgroundColor,
            onDismissRequest = {
                shouldShowLogoutDialog.value = false
            },
            dismissButton = {
                Button(onClick = {
                    shouldShowLogoutDialog.value = false
                }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch { drawerState.close() }
                    shouldShowLogoutDialog.value = false
                    userViewModel.logoutUser()
                    (context as MainActivity).finish()
                    val intent = Intent(context, MainActivity::class.java)
                    ContextCompat.startActivity(context, intent, null)
                }) {
                    Text(stringResource(android.R.string.ok))
                }
            }
        )
    }

    if (shouldShowChangePasswordDialog) {
        AlertDialog(
            modifier = Modifier,
            title = {
                Text(text = stringResource(id = R.string.change_password_text))
            },
            text = {
                Text(text = stringResource(id = R.string.would_you_change_password))
            },
            shape = RectangleShape,
            containerColor = backgroundColor,
            onDismissRequest = {
                shouldShowChangePasswordDialog = false
            },
            dismissButton = {
                Button(onClick = {
                    shouldShowChangePasswordDialog = false
                }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch { drawerState.close() }
                    shouldShowChangePasswordDialog = false
                    showNameChangeDialog(context)
                }) {
                    Text(stringResource(android.R.string.ok))
                }
            }
        )
    }

    if (shouldShowWipeDBDialog) {
        AlertDialog(
            modifier = Modifier,
            title = {
                Text(text = stringResource(id = R.string.erase_collection_string))
            },
            text = {
                Text(text = stringResource(id = R.string.would_you_erase_full_collection))
            },
            shape = RectangleShape,
            containerColor = backgroundColor,
            onDismissRequest = {
                shouldShowWipeDBDialog = false
            },
            dismissButton = {
                Button(onClick = {
                    shouldShowWipeDBDialog = false
                }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch { drawerState.close() }
                    shouldShowWipeDBDialog = false
                    firebaseDBConnect.eraseRemoteDatabase(itemsViewModel, context)
                }) {
                    Text(stringResource(android.R.string.ok))
                }
            }
        )
    }
}

fun showNameChangeDialog(context: Context) {
    //Validate passwords then upload
    val builder = AlertDialog.Builder(context)
    val customLayout: View = (context as MainActivity).layoutInflater.inflate(R.layout.change_password_dialog_layout, null)

    builder.setView(customLayout)
    val createdBuilder = builder.create()
    createdBuilder.show()
    var newPassword = ""
    var reenterNewPassword = ""

    val newPasswordEditText = customLayout.findViewById<EditText>(R.id.enter_new_password_edittext)
    val newRepeatPasswordEditText = customLayout.findViewById<EditText>(R.id.reenter_new_password_edittext)
    val passwordVisibility = customLayout.findViewById<CheckBox>(R.id.checkbox_password_visible)
    val cancelButton = customLayout.findViewById<Button>(R.id.cancel_button)
    val acceptButton = customLayout.findViewById<Button>(R.id.accept_button)

    newPasswordEditText.addTextChangedListener(object : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            newPassword = p0.toString()
        }
        override fun afterTextChanged(p0: Editable?) {}
    })

    newRepeatPasswordEditText.addTextChangedListener(object : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            reenterNewPassword = p0.toString()
        }
        override fun afterTextChanged(p0: Editable?) {}
    })
    passwordVisibility.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
        override fun onCheckedChanged(checkBox: CompoundButton?, isChecked: Boolean) {
            if (isChecked) {
                //ShowPassword
                newPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
                newRepeatPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
            }
            else {
                //Hide Password
                newPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance())
                newRepeatPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance())
            }
        }
    })

    cancelButton.setOnClickListener {
        createdBuilder.dismiss()
    }

    acceptButton.setOnClickListener {
       if (newPassword.equals(reenterNewPassword)) {
          val isPasswordStrong =isPasswordStrong(newPassword)
           if (isPasswordStrong.equals(StrengthPasswordTypes.STRONG)) {
               //UPDATE PASSWORD
               val user = Firebase.auth.currentUser
               user!!.updatePassword(newPassword)
                   .addOnCompleteListener { task ->
                       if (!task.isSuccessful) {
                           Toast.makeText(context, R.string.failed_to_update_password, Toast.LENGTH_SHORT).show()
                           createdBuilder.dismiss()
                       }
                   }.addOnSuccessListener {
                       Toast.makeText(context, R.string.password_updated, Toast.LENGTH_SHORT).show()
                       createdBuilder.dismiss()
                   }.addOnFailureListener {
                       Toast.makeText(context, R.string.failed_to_update_password, Toast.LENGTH_SHORT).show()
                       createdBuilder.dismiss()
                   }
           }
           else {
               Toast.makeText(context, R.string.weak_password, Toast.LENGTH_SHORT).show()
           }
       }
       else {
            Toast.makeText(context, R.string.passwords_are_different, Toast.LENGTH_SHORT).show()
       }
    }
}