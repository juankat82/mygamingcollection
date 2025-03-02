@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.juan.mygamingcollection.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.juan.mygamingcollection.MainActivity
import com.juan.mygamingcollection.R
import com.juan.mygamingcollection.data.firebaseDB.FirebaseDBConnectImpl
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun UploadImages(
    localContext: Context,
    firebaseDBConnect: FirebaseDBConnectImpl,
    drawerState: DrawerState
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = localContext
    val backgroundColor = Color(0.85490197f, 0.8862745f, 1.0f, 1.0f, ColorSpaces.Srgb)
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    var picsToUpload by rememberSaveable { mutableStateOf(0) }
    var newItemName by rememberSaveable { mutableStateOf("") }
    var newItemBrand by rememberSaveable { mutableStateOf("") }
    var newItemRegion by rememberSaveable { mutableStateOf("") }

    var itemNameBrandRegionFilled by rememberSaveable { mutableStateOf(false) }
    var showAlertDialog by rememberSaveable { mutableStateOf(false) }
    var shouldEraseAllPictures by remember { mutableStateOf(false) }
    var shouldEraseAllPicturesDialog by remember { mutableStateOf(false) }
    //Front Box Picture
    var imageUriFront by remember { mutableStateOf<Uri?>(null) }
    var imageTempFront by remember { mutableStateOf<Uri?>(null) }
    var imagePathFront by remember { mutableStateOf("") }
    var imageFileFront by remember { mutableStateOf<File?>(null) }
    //Back Box Picture
    var imageUriBack by remember { mutableStateOf<Uri?>(null) }
    var imageTempBack by remember { mutableStateOf<Uri?>(null) }
    var imagePathBack by remember { mutableStateOf("") }
    var imageFileBack by remember { mutableStateOf<File?>(null) }
    //Front Item Picture
    var imageUriItem by remember { mutableStateOf<Uri?>(null) }
    var imageTempItem by remember { mutableStateOf<Uri?>(null) }
    var imagePathItem by remember { mutableStateOf("") }
    var imageFileItem by remember { mutableStateOf<File?>(null) }

    if (newItemName != "" && newItemBrand != "" && newItemRegion != "") {
        itemNameBrandRegionFilled = true
    } else {
        itemNameBrandRegionFilled = false
    }

    if (shouldEraseAllPictures) {
        picsToUpload = 0
        //Front Box Picture
        imageUriFront = null
        imageTempFront = null
        imagePathFront = ""
        imageFileFront = null
        //Back Box Picture
        imageUriBack = null
        imageTempBack = null
        imagePathBack = ""
        imageFileBack = null
        //Front Item Picture
        imageUriItem = null
        imageTempItem = null
        imagePathItem = ""
        imageFileItem = null
    }

    //DIALOG TO ERASE/RESET PICTURES TO SEND
    if (shouldEraseAllPicturesDialog) {
        AlertDialog(
            modifier = Modifier,
            title = {
                Text(text = stringResource(id = R.string.reset_pictures))
            },
            text = {
                Text(text = stringResource(id = R.string.would_you_reset_pictures))
            },
            shape = RectangleShape,
            containerColor = com.juan.mygamingcollection.ui.screens.backgroundColor,
            onDismissRequest = {
                shouldEraseAllPicturesDialog = false
            },
            confirmButton = {
                Button(onClick = {
                   if (imageFileFront?.exists() == true || imageFileBack?.exists() == true || imageFileItem?.exists() == true){
                            if (imageFileFront?.exists() == true) {
                                imageFileFront?.delete()
                                imageUriFront = Uri.EMPTY
                            }
                            if (imageFileBack?.exists() == true) {
                                imageFileBack?.delete()
                                imageUriBack = Uri.EMPTY
                            }
                            if (imageFileItem?.exists() == true) {
                                imageFileItem?.delete()
                                imageUriItem = Uri.EMPTY
                            }
                            shouldEraseAllPictures = true
                            Thread.sleep(50L)
                            shouldEraseAllPictures = false
                            shouldEraseAllPicturesDialog = false
                        } else {
                            Toast.makeText(context, "Take pictures before erasing them.", Toast.LENGTH_SHORT).show()
                            shouldEraseAllPicturesDialog = false
                        }
                    }
                ) {
                        Text(stringResource(android.R.string.ok))
                    }
            },
            dismissButton = {
                Button(onClick = {
                    shouldEraseAllPicturesDialog = false
                }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
            icon = {
                Icon(painterResource(id = R.drawable.reset_icon), "upload alert icon")
            }
        )
    }
    //PERMISSIONS AND CONTRACTS FOR PICTURE TAKING-STARTS HERE-/////////////
    //FRONTSIDE BOX
    val itemFrontLauncherForCamera = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { isSuccess ->
        if(isSuccess) {
            imageTempFront?.let { uri ->
                if (picsToUpload == 3)
                    picsToUpload--
                picsToUpload++
                imageUriFront = uri
                imagePathFront = imageFileFront!!.absolutePath
            }
        }
    }
    val onFrontPictureCameraClick = { fileNameFront: String ->
        imageFileFront = createImageFile(context, fileNameFront)
        imageTempFront = FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFileFront!!)
        itemFrontLauncherForCamera.launch(imageTempFront)
    }
    //////////////
    //BACKSIDE BOX
    val itemBackLauncherForCamera = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { isSuccess ->
        if(isSuccess) {
            imageTempBack?.let { uri ->
                if (picsToUpload == 3)
                    picsToUpload--
                picsToUpload++
                imageUriBack = uri
                imagePathBack = imageFileBack!!.absolutePath
            }
        }
    }
    val onBackPictureCameraClick = { fileNameBack: String ->
        imageFileBack = createImageFile(context, fileNameBack)
        imageTempBack = FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFileBack!!)
        itemBackLauncherForCamera.launch(imageTempBack)
    }
    //////////////
    //FRONTSIDE ITEM
    val itemLauncherForCamera = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { isSuccess ->
        if(isSuccess) {
            imageTempItem?.let { uri ->
                if (picsToUpload == 3)
                    picsToUpload--
                picsToUpload++
                imageUriItem = uri
                imagePathItem = imageFileItem!!.absolutePath
            }
        }
    }
    val onItemPictureCameraClick = { fileNameItem: String ->
        imageFileItem = createImageFile(context, fileNameItem)
        imageTempItem = FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFileItem!!)
        itemLauncherForCamera.launch(imageTempItem)
    }

    ///CONTRACTS FOR PICTURE CHOOSING STARTS HERE////////////////
    val onFrontPictureChooseClickStartForResult = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data.also { uri ->
                imageUriFront = uri
                imagePathFront = uri?.path.toString()
            }
        }
    }
    val onFrontPictureChooseClick = { fileNameFront: String ->
        imageFileFront = createImageFile(context, fileNameFront)
        imageTempFront = FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFileFront!!)

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        onFrontPictureChooseClickStartForResult.launch(intent)
    }

    val onBackPictureChooseClickStartForResult = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data.also {uri ->
                imageUriBack = uri
                imagePathBack = uri?.path.toString()
            }
        }
    }
    val onBackPictureChooseClick = { fileNameBack: String ->
        imageFileBack = createImageFile(context, fileNameBack)
        imageTempBack = FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFileBack!!)

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        onBackPictureChooseClickStartForResult.launch(intent)
    }
    val onItemPictureChooseClickStartForResult = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data.also {uri ->
                imageUriItem = uri
                imagePathItem = uri?.path.toString()
            }
        }
    }
    val onItemPictureChooseClick = { fileNameItem: String ->
        imageFileItem = createImageFile(context, fileNameItem)
        imageTempItem = FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFileItem!!)

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        onItemPictureChooseClickStartForResult.launch(intent)
    }
    //CONTRACTS FOR PICTURE CHOOSING-ENDS HERE-/////////////
    //////////////
    if (showAlertDialog) {
            AlertDialog(
                modifier = Modifier,
                title = {
                    Text(text = stringResource(id = R.string.do_you_upload_new_pictures))
                },
                text = {
                    Text(text = stringResource(id = R.string.would_you_upload_new_pictures))
                },
                shape = RectangleShape,
                containerColor = com.juan.mygamingcollection.ui.screens.backgroundColor,
                onDismissRequest = {
                    showAlertDialog = false
                },
                confirmButton = {
                    Button(onClick = {
                        if (imageFileFront?.exists() == true || imageFileBack?.exists() == true || imageFileItem?.exists() == true){
                            firebaseDBConnect.uploadFile(
                                listOf(
                                    imageFileFront,
                                    imageFileBack,
                                    imageFileItem
                                ), context
                            )
                            showAlertDialog = false
                        }else {
                            Toast.makeText(context, "Take pictures before uploading.", Toast.LENGTH_SHORT).show()
                            showAlertDialog = false
                        }
                    }) {
                        Text(stringResource(android.R.string.ok))
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        shouldEraseAllPictures = true
                        showAlertDialog = false
                    }) {
                        Text(stringResource(android.R.string.cancel))
                    }
                },
                icon = {
                    Icon(painterResource(id = R.drawable.upload_alert_icon), "upload alert icon")
                }
            )
        }

    BackHandler {
        if (drawerState.isOpen)
            scope.launch { drawerState.close() }
        if (drawerState.isClosed)
            showExitDialog = true
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (cameraPermissionState.status.isGranted) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = backgroundColor)
            ) {
                Surface(modifier = Modifier
                    .clickable { keyboardController?.hide() }
                    .background(color = backgroundColor)
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 80.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(2.dp, Color.LightGray),
                    shadowElevation = 5.dp,
                    tonalElevation = 5.dp,
                    content = {
                        LazyColumn(
                            modifier = Modifier
                                .padding(vertical = 0.dp, horizontal = 0.dp)
                                .background(colorResource(id = R.color.clear_purple_juan)),
                            state = lazyListState,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ){
                            items(1){
                                Column {
                                    newItemName = enterItemName()
                                    newItemBrand = EnterItemBrand()
                                    newItemRegion = EnterItemRegion()
                                    HorizontalDivider(Modifier.padding(horizontal = 10.dp, vertical = 10.dp))
                                    Text(modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .clickable { keyboardController?.hide() }
                                        .fillMaxWidth()
                                        .padding(
                                            top = 10.dp,
                                            bottom = 3.dp,
                                            start = 10.dp,
                                            end = 10.dp
                                        ),
                                        textAlign = TextAlign.Center,
                                        text = stringResource(id = R.string.click_on_each_image_to_open_picture))
                                    Row(modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(start = 10.dp, end = 10.dp, bottom = 25.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.arrow_down),
                                            contentDescription = "point down1"
                                        )
                                        Icon(
                                            painter = painterResource(id = R.drawable.arrow_down),
                                            contentDescription = "point down2"
                                        )
                                        Icon(
                                            painter = painterResource(id = R.drawable.arrow_down),
                                            contentDescription = "point down"
                                        )
                                    }
                                    Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                        val frontPictureName = newItemBrand + "_" + newItemName + "_" + newItemRegion + "_" + "frnt"
                                        Box(modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(horizontal = 10.dp, vertical = 10.dp)){
                                            Text(text = stringResource(id = R.string.front_picture_text))
                                        }

                                        PictureBoxItem(context, frontPictureName, onFrontPictureCameraClick, imageUriFront, itemNameBrandRegionFilled, onFrontPictureChooseClick)
                                        val backPictureName = newItemBrand + "_" + newItemName + "_" + newItemRegion + "_" + "bck"
                                        Box(modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(horizontal = 10.dp, vertical = 10.dp)) {
                                            Text(text = stringResource(id = R.string.back_picture_text))
                                        }

                                        PictureBoxItem(context, backPictureName, onBackPictureCameraClick, imageUriBack, itemNameBrandRegionFilled, onBackPictureChooseClick)
                                        val cartPictureName = newItemBrand + "_" + newItemName + "_" + newItemRegion + "_" + "crt"
                                        Box(modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(horizontal = 10.dp, vertical = 10.dp)) {
                                            Text(text = stringResource(id = R.string.cart_picture_text))
                                        }
                                        PictureBoxItem(context, cartPictureName, onItemPictureCameraClick, imageUriItem, itemNameBrandRegionFilled, onItemPictureChooseClick)
                                    }
                                    //////////////ICON AND TEXT BUTTON/////////
                                    Column(modifier = Modifier
                                        .fillMaxSize()
                                        .fillMaxHeight()
                                        .fillMaxWidth()
                                        .padding(15.dp, 15.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.SpaceAround
                                    ) {
                                        Row (modifier = Modifier
                                            .padding(horizontal = 10.dp, vertical = 10.dp)
                                            .align(Alignment.CenterHorizontally)) {
                                            Button(modifier = Modifier
                                                .weight(0.5f)
                                                .shadow(
                                                    0.dp,
                                                    RoundedCornerShape(2.dp, 2.dp, 2.dp, 2.dp)
                                                )
                                                .padding(end = 10.dp),
                                                colors = ButtonDefaults.buttonColors().copy(containerColor = backgroundColor),
                                                onClick = { showAlertDialog = true }) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.upload_icon),
                                                    contentDescription = "upload images",
                                                    modifier = Modifier
                                                        .height(30.dp)
                                                        .width(60.dp)
                                                )
                                                Spacer(Modifier.height(5.dp))
                                                Text(
                                                    stringResource(id = R.string.upload_new_pictures),
                                                    style = TextStyle(fontWeight = FontWeight.Bold),
                                                    textAlign = TextAlign.Center,
                                                    fontSize = 15.sp,
                                                    color = Color.Black
                                                )
                                            }
                                        }
                                        Button(modifier = Modifier
                                            .weight(0.5f)
                                            .shadow(
                                                0.dp,
                                                RoundedCornerShape(2.dp, 2.dp, 2.dp, 2.dp)
                                            )
                                            .padding(start = 10.dp),
                                            colors = ButtonDefaults.buttonColors().copy(containerColor = backgroundColor),
                                            onClick = {
                                                shouldEraseAllPicturesDialog = true
                                            }) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.reset_icon),
                                                    contentDescription = "reset images",
                                                    modifier = Modifier
                                                        .height(30.dp)
                                                        .width(60.dp)
                                                )
                                                Spacer(Modifier.height(5.dp))
                                                Text(
                                                    stringResource(id = R.string.reset_pictures),
                                                    style = TextStyle(fontWeight = FontWeight.Bold),
                                                    textAlign = TextAlign.Center,
                                                    fontSize = 15.sp,
                                                    color = Color.Black
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                })
            }
        }
        else {
            if (cameraPermissionState.status.shouldShowRationale)
                Toast.makeText(context, R.string.you_need_to_enable_camera_permissions, Toast.LENGTH_SHORT).show()
            else
                LaunchedEffect(key1 = Unit) {
                    cameraPermissionState.launchPermissionRequest()
                }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            modifier = Modifier,
            title = {
                Text(text = stringResource(id = R.string.leave_title))
            },
            text = {
                Text(text = stringResource(id = R.string.would_you_leave))
            },
            shape = RectangleShape,
            containerColor = backgroundColor,
            onDismissRequest = {
                showExitDialog = false
            },
            confirmButton = {
                Button(onClick = {
                    showExitDialog = false
                    (context as MainActivity).finish()
                }
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                Button(onClick = {
                    showExitDialog = false
                }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
            icon = {
                Icon(painterResource(id = R.drawable.logout_icon), "exit_icon")
            }
        )
    }
}

fun createImageFile(context: Context, pictureName: String): File {
    val storageDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)!!
    val fileName = File.createTempFile(pictureName, ".jpg", storageDir)
    var fileName2: File? = null
    if (pictureName.contains("frnt")) {
        val renamingName = pictureName.split("frnt").get(0)+"frnt.jpg"
        val reFile = storageDir.absolutePath+"/"+renamingName
        fileName2 = File(reFile)
        fileName.renameTo(fileName2)
    }
    if (pictureName.contains("bck")) {
        val renamingName = pictureName.split("bck").get(0)+"bck.jpg"
        val reFile = storageDir.absolutePath+"/"+renamingName
        fileName2 = File(reFile)
        fileName.renameTo(fileName2)
    }
    if (pictureName.contains("crt")) {
        val renamingName = pictureName.split("crt").get(0)+"crt.jpg"
        val reFile = storageDir.absolutePath+"/"+renamingName
        fileName2 = File(reFile)
        fileName.renameTo(fileName2)
    }
    return fileName2!!
}
@Composable
fun enterItemName() : String {
    var itemName by rememberSaveable { mutableStateOf("") }
    OutlinedTextField(
        value = itemName,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, start = 8.dp, end = 8.dp, bottom = 5.dp),
        minLines = 1,
        maxLines = 1,
        singleLine = true,
        onValueChange = { if (it.length < 50) itemName = it },
        shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp),
        label = {
            Text(
                text = stringResource(id = R.string.enter_item_name),
                color = Color.Black,
                fontSize = 16.sp
            )
        },
    )
    return itemName
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterItemBrand() : String {
    val brandList = listOf("Nintendo", "Sega", "Sony", "Microsoft", "Snk", "NEC")
    var openTrigger by rememberSaveable { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf(brandList.get(0)) }
    Column {
        ExposedDropdownMenuBox(modifier = Modifier
            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 5.dp),
            expanded = openTrigger,
            onExpandedChange = { openTrigger = it }) {
            TextField(
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                    .fillMaxWidth(),
                value = text,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = { Text(text = stringResource(id = R.string.pick_a_brand)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = openTrigger) },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                expanded = openTrigger,
                onDismissRequest = { openTrigger = false }) {
                brandList.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item, style = MaterialTheme.typography.bodyLarge) },
                        onClick = {
                            text = item
                            openTrigger = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
    return text
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterItemRegion() : String {
    var openTrigger by rememberSaveable { mutableStateOf(false) }
    val regionList = listOf("PAL", "USA-NTSC", "JPN-NTSC", "WORLD")
    var text by rememberSaveable { mutableStateOf(regionList.get(0)) }

    Column {
        ExposedDropdownMenuBox(modifier = Modifier
            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 5.dp),
            expanded = openTrigger,
            onExpandedChange = { openTrigger = it }) {
            TextField(
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                    .fillMaxWidth(),
                value = text,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = { Text(text = stringResource(id = R.string.pick_a_region)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = openTrigger) },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                expanded = openTrigger,
                onDismissRequest = { openTrigger = false }) {
                regionList.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item, style = MaterialTheme.typography.bodyLarge) },
                        onClick = {
                            text = item
                            openTrigger = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
    return text
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PictureBoxItem(
    context: Context,
    imageName: String,
    onCameraClick: (String) -> Unit,
    imageUri: Uri?,
    itemNameBrandRegionFilled: Boolean,
    onItemPictureChooseClick: (String) -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    GlideImage(
        failure = placeholder(R.drawable.image_item_unavailable),
        loading = placeholder(R.drawable.image_item_unavailable),
        alignment = Alignment.Center,
        model = imageUri,
        contentDescription = "back box",
        modifier = Modifier
            .padding(start = 45.dp, bottom = 20.dp)
            .size(200.dp, 200.dp)
            .background(Color.Black)
            .clickable {
                if (itemNameBrandRegionFilled)
                    showBottomSheet = true
                else
                    Toast.makeText(context, R.string.fields_arent_filled, Toast.LENGTH_SHORT).show()
            }
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            shape = MaterialTheme.shapes.medium.copy(
                bottomStart = CornerSize(0),
                bottomEnd = CornerSize(0)
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    text = stringResource(id = R.string.modal_bottom_sheet_title),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp),
                    thickness = 3.dp,
                    color = Color.Black
                )
                Row(
                    modifier = Modifier.padding(start = 50.dp, end = 50.dp, top = 20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
                            //HERE CODE TO UPLOAD PICTURES FROM OUR DEVICE
                            showBottomSheet = false
                            onItemPictureChooseClick.invoke(imageName)
                        }) {
                            Icon(
                                modifier = Modifier.size(50.dp, 50.dp),
                                painter = painterResource(id = R.drawable.open_file),
                                contentDescription = "open picture"
                            )
                        }
                        Text(
                            modifier = Modifier.padding(vertical = 6.dp),
                            text = stringResource(id = R.string.open_picture),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Left
                        )
                    }
                    Column(
                        modifier = Modifier
                            .padding(start = 50.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
                            onCameraClick.invoke(imageName)
                            showBottomSheet = false
                        }) {
                            Icon(
                                modifier = Modifier.size(50.dp, 50.dp),
                                painter = painterResource(id = R.drawable.use_camera),
                                contentDescription = "use camera"
                            )
                        }
                        Text(
                            modifier = Modifier.padding(vertical = 6.dp),
                            text = stringResource(id = R.string.open_camera),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}