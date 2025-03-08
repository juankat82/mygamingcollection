package com.juan.mygamingcollection.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import com.juan.mygamingcollection.MainActivity
import com.juan.mygamingcollection.R
import com.juan.mygamingcollection.data.firebaseDB.FirebaseDBConnectImpl
import com.juan.mygamingcollection.data.viewmodel.ItemsViewModel
import com.juan.mygamingcollection.data.viewmodel.UserViewModel
import com.juan.mygamingcollection.logic.InternetChecker
import com.juan.mygamingcollection.model.Item
import kotlinx.coroutines.launch

const val BASE_STORAGE_ADDRESS = "https://storage.googleapis.com/tour-guide-app-677c8.appspot.com"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewItem(
    context: Context,
    itemsViewModel: ItemsViewModel,
    firebaseDBConnect: FirebaseDBConnectImpl,
    userViewModel: UserViewModel,
    drawerState: DrawerState
) {
    val backgroundColor = Color(0.85490197f, 0.8862745f, 1.0f, 1.0f, ColorSpaces.Srgb)
    val keyboardController = LocalSoftwareKeyboardController.current
    val dropMenuTextFieldTexts = listOf(
        R.string.type_of_item,
        R.string.item_brand,
        R.string.item_complete_state,
        R.string.item_region,
        R.string.item_is_modded,
        R.string.item_purchase_currency
    )
    var itemNameErrorTextVisibility by rememberSaveable { mutableStateOf(false) }
    var itemModelErrorTextVisibility by rememberSaveable { mutableStateOf(false) }


    var item by remember { mutableStateOf(Item()) }
    var itemId by rememberSaveable { mutableStateOf("") }
    var itemName by rememberSaveable { mutableStateOf("") }
    //This on is required as we need an extra variable to handle the value of the "Item Model"
    var modelText by rememberSaveable { mutableStateOf("") }
    var itemModel by rememberSaveable { mutableStateOf("") }
    var itemType by rememberSaveable { mutableStateOf("") }
    var itemBrand by rememberSaveable { mutableStateOf("") }
    var itemCompleteness by rememberSaveable { mutableStateOf("") }
    var itemRegion by rememberSaveable { mutableStateOf("") }
    var isItemModded by rememberSaveable { mutableStateOf("") }
    var itemYear by rememberSaveable { mutableStateOf("") }
    var itemPrice by rememberSaveable { mutableStateOf("/") }

    item.itemId = itemId
    item.itemName = itemName
    item.itemModel = itemModel
    item.itemType = itemType
    item.itemBrand = itemBrand
    item.itemCompleteness = itemCompleteness
    item.itemRegion = itemRegion
    item.itemModded = isItemModded
    item.itemPrice = itemPrice.split("/").get(0)
    item.itemPriceCurrency = itemPrice.split("/").get(1)
    item.itemYear = itemYear

    val scope = rememberCoroutineScope()
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showPriceEqualsZeroDialog by rememberSaveable { mutableStateOf(false) }

    BackHandler {
        if (drawerState.isOpen)
            scope.launch { drawerState.close() }
        if (drawerState.isClosed)
            showExitDialog = true
    }

    Surface (modifier = Modifier
        .clickable { keyboardController?.hide() }
        .background(color = backgroundColor)
        .fillMaxSize()
        .padding(horizontal = 10.dp, vertical = 80.dp),
    shape = RoundedCornerShape(10.dp),
    border = BorderStroke(2.dp, Color.LightGray),
    shadowElevation = 5.dp,
    tonalElevation = 5.dp,
    content = {
        val lazyListState = rememberLazyListState()
        val lazyListScope = rememberCoroutineScope()

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = colorResource(id = R.color.clear_purple_juan)
                )) {
            item {
                OutlinedTextField(value = itemName,
                    modifier = Modifier
                        .fillMaxWidth().semantics { contentDescription = "item_name" }
                        .padding(top = 15.dp, start = 8.dp, end = 8.dp, bottom = 5.dp),
                    minLines = 1,
                    maxLines = 1,
                    singleLine = true,
                    onValueChange = { if (it.length<50) itemName = it },
                    shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp),
                    label = { Text(text = stringResource(id = R.string.item_name), color = Color.Black) },
                )

                if (itemNameErrorTextVisibility)
                    Text(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
                        text = stringResource(id = R.string.must_enter_name), fontSize = 18.sp, color = Color.Red)
                if (itemType.equals("Console")) {
                    var openTrigger by rememberSaveable { mutableStateOf(false) }
                    val modelList = when (itemBrand) {
                        "Nintendo" -> getConsoleModelList(0)
                        "Sega" -> getConsoleModelList(1)
                        "Sony" -> getConsoleModelList(2)
                        "Microsoft" -> getConsoleModelList(3)
                        "Snk" -> getConsoleModelList(4)
                        else -> getConsoleModelList(5)
                    }
                    var text by rememberSaveable { mutableStateOf(modelList.get(0)) }
                    itemModel = text
                    Text(text = stringResource(id = R.string.item_model), modifier = Modifier.padding(start = 8.dp, top = 8.dp))
                    ExposedDropdownMenuBox(modifier = Modifier
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 5.dp), expanded = openTrigger, onExpandedChange = {openTrigger = it}) {
                        TextField(
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                                .fillMaxWidth(),
                            value = text,
                            onValueChange = {},
                            readOnly = true,
                            singleLine = true,
                            label = { Text(text = stringResource(id = R.string.pick_an_option))},
                            trailingIcon = {ExposedDropdownMenuDefaults.TrailingIcon(expanded = openTrigger)},
                            colors = ExposedDropdownMenuDefaults.textFieldColors())
                        ExposedDropdownMenu(expanded = openTrigger, onDismissRequest = { openTrigger = false }) {
                            modelList.forEach {item ->
                                DropdownMenuItem(
                                    text = { Text(item, style= MaterialTheme.typography.bodyLarge) },
                                    onClick = {
                                        itemModel = item
                                        text = item
                                        openTrigger = false },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
                            }
                        }
                    }
                }
                else {
                    OutlinedTextField(value = modelText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 5.dp),
                        minLines = 1,
                        maxLines = 1,
                        singleLine = true,
                        onValueChange = { if (it.trim().length<50)
                            modelText = it
                            itemModel = modelText
                        },
                        shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp),
                        label = { Text(text = stringResource(id = R.string.item_model), color = Color.Black) },
                    )
                }

                if (itemModelErrorTextVisibility)
                    Text(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
                        text = stringResource(id = R.string.must_enter_model), fontSize = 18.sp, color = Color.Red)
                listOf(0,1,2,3,4).forEach { n ->
                    val values = SetupDropDownMenus(n, dropMenuTextFieldTexts)
                    when(n) {
                        0 -> { itemType = values }
                        1 -> { itemBrand = values }
                        2 -> { itemCompleteness = values }
                        3 -> { itemRegion = values }
                        4 -> { isItemModded = values }
                    }
                }
                itemPrice = SetupPriceDropDownMenu()
                itemYear = DatePickerDropDown()
                Row (modifier = Modifier
                    .padding(bottom = 8.dp, top = 8.dp)
                    .fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                    ElevatedButton(modifier = Modifier.padding(all = 8.dp), onClick = {
                       if (item.itemName.trim().isEmpty())
                           itemNameErrorTextVisibility = true
                       else
                            itemNameErrorTextVisibility = false

                       if (item.itemModel.trim().isEmpty())
                           itemModelErrorTextVisibility = true
                       else
                           itemModelErrorTextVisibility = false

                        if(itemNameErrorTextVisibility == false && itemModelErrorTextVisibility == false) {
                            if(!InternetChecker.isNetworkAvailable(context))
                                context.mainExecutor.execute { Toast.makeText(context, R.string.no_network_available, Toast.LENGTH_SHORT).show()}
                            else {
                                item.itemId = itemBrand+"-"+itemModel+"-"+itemRegion
                                item.pictureFrontBox = (BASE_STORAGE_ADDRESS+"/images/"+itemBrand+"_"+itemName+"_"+itemRegion+"_frnt.jpg").lowercase()
                                item.pictureBackBox = (BASE_STORAGE_ADDRESS+"/images/"+itemBrand+"_"+itemName+"_"+itemRegion+"_bck.jpg").lowercase()
                                item.pictureGame = (BASE_STORAGE_ADDRESS+"/images/"+itemBrand+"_"+itemName+"_"+itemRegion+"_crt.jpg").lowercase()
                                Log.i("WHATSTHEPRICE",item.itemPrice)
                                if (item.itemPrice.equals("0")){
                                    showPriceEqualsZeroDialog = true
                                }
                                else {
                                    itemsViewModel.setSingleItem(item)
                                    firebaseDBConnect.writeNewRegistry(item, context, userViewModel)
                                    firebaseDBConnect.hasBeenSent.observe(
                                        (context as MainActivity),
                                        object : Observer<Int> {
                                            override fun onChanged(value: Int) {
                                                //-1 means its did send
                                                if (value != -1) {
                                                    itemId = ""
                                                    itemName = ""
                                                    itemPrice = "0/£"
                                                    itemModel = ""
                                                    modelText = ""
                                                    item = Item()
                                                    lazyListScope.launch { lazyListState.animateScrollToItem(0) }
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }) {
                        Text(text = stringResource(id = R.string.send_buttom_text))
                    }
                }
            }
        }
        if (showPriceEqualsZeroDialog) {
            AlertDialog(
                modifier = Modifier,
                title = {
                    Text(textAlign = TextAlign.Center, text = stringResource(id = R.string.price_zero_title))
                },
                text = {
                    Text(textAlign = TextAlign.Center, text = stringResource(id = R.string.price_zero_text))
                },
                shape = RectangleShape,
                containerColor = backgroundColor,
                onDismissRequest = {
                    showPriceEqualsZeroDialog = false
                },
                confirmButton = {
                    Button(onClick = {
                        showPriceEqualsZeroDialog = false
                        itemsViewModel.setSingleItem(item)
                        firebaseDBConnect.writeNewRegistry(item, context, userViewModel)
                        firebaseDBConnect.hasBeenSent.observe(
                            (context as MainActivity),
                            object : Observer<Int> {
                                override fun onChanged(value: Int) {
                                    //-1 means its did send
                                    if (value != -1) {
                                        itemId = ""
                                        itemName = ""
                                        itemPrice = "0/£"
                                        itemModel = ""
                                        modelText = ""
                                        item = Item()
                                        lazyListScope.launch { lazyListState.animateScrollToItem(0) }
                                    }
                                }
                            }
                        )
                    }
                    ) {
                        Text(stringResource(android.R.string.ok))
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showPriceEqualsZeroDialog = false
                    }) {
                        Text(stringResource(android.R.string.cancel))
                    }
                },
                icon = {
                    Icon(painterResource(id = R.drawable.warning_icon), "exit_icon")
                }
            )

        }
    })

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
                }) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDropDown() : String {
    var datePicked by rememberSaveable { mutableStateOf("1955") }
    var openTrigger by rememberSaveable { mutableStateOf(false) }

    Row (modifier = Modifier
        .padding(bottom = 8.dp, top = 8.dp)
        .fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
        OutlinedTextField(value = datePicked,
            modifier = Modifier
                .weight(0.3f)
                .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 5.dp),
            minLines = 1,
            maxLines = 1,
            singleLine = true,
            enabled = false,
            onValueChange = { datePicked },
            shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp),
            textStyle = TextStyle(color = Color.Black),
            label = { Text(text = stringResource(id = R.string.year_text), color = Color.Black) },
        )
        ExposedDropdownMenuBox(modifier = Modifier
            .weight(0.7f)
            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 5.dp), expanded = openTrigger, onExpandedChange = {openTrigger = it}) {
            TextField(
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                value = datePicked,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = { Text(text = stringResource(id = R.string.pick_an_option))},
                trailingIcon = {ExposedDropdownMenuDefaults.TrailingIcon(expanded = openTrigger)},
                colors = ExposedDropdownMenuDefaults.textFieldColors())
            ExposedDropdownMenu(expanded = openTrigger, onDismissRequest = { openTrigger = false }) {
                (1955..2024).forEach {item ->
                    DropdownMenuItem(
                        text = { Text(item.toString(), style= MaterialTheme.typography.bodyLarge) },
                        onClick = {
                            datePicked = item.toString()
                            openTrigger = false },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
                }
            }
        }
    }
    return datePicked
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupDropDownMenus(n: Int, dropMenuTextFieldTexts: List<Int>) : String{
    var openTrigger by rememberSaveable{ mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf(getLists(n).get(0)) }
        Text(
            text = stringResource(id = dropMenuTextFieldTexts.get(n)),
            modifier = Modifier.padding(start = 8.dp, top = 8.dp)
        )
        ExposedDropdownMenuBox(expanded = openTrigger, onExpandedChange = { openTrigger = it }) {
            TextField(
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                    .fillMaxWidth()
                    .padding(8.dp),
                value = text,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = { Text(text = stringResource(id = R.string.pick_an_option)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = openTrigger) },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                expanded = openTrigger,
                onDismissRequest = { openTrigger = false }) {
                getLists(n).forEach { item ->
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
    return text
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupPriceDropDownMenu() : String{
    var openTrigger by rememberSaveable{ mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf(getLists(5).get(0)) }
    var price by rememberSaveable { mutableStateOf("") }
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp, horizontal = 8.dp),Arrangement.Center){
        OutlinedTextField(value = price,
            modifier = Modifier
                .width(50.dp)
                .weight(0.3f, true),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            minLines = 1,
            maxLines = 1,
            singleLine = true,
            onValueChange = {
                if (it.length<7) {
                    price = it
                }
            },
            shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp),
            label = { Text(text = stringResource(id = R.string.price_string), color = Color.Black) }
        )
        ExposedDropdownMenuBox(modifier = Modifier.weight(0.7f), expanded = openTrigger, onExpandedChange = {openTrigger = it}) {
            TextField(
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                    .padding(8.dp),
                value = text,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = { Text(text = stringResource(id = R.string.pick_an_option))},
                trailingIcon = {ExposedDropdownMenuDefaults.TrailingIcon(expanded = openTrigger)},
                colors = ExposedDropdownMenuDefaults.textFieldColors())
            ExposedDropdownMenu(expanded = openTrigger, onDismissRequest = { openTrigger = false }) {
                getLists(5).forEach {item ->
                    DropdownMenuItem(
                        text = { Text(item, style= MaterialTheme.typography.bodyLarge) },
                        onClick = {
                            text = item
                            openTrigger = false },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
                }
            }
        }
    }
    if (price.equals(""))
        return "0"+"/"+text
    return price+"/"+text
}

fun getLists(listNnumber: Int) : List<String> {
    val itemTypeList = listOf("Game", "Console", "Accesory")
    val itemBrandList = listOf(
        "Nintendo",
        "Sega",
        "Sony",
        "Microsoft",
        "Neogeo",
        "NEC"
        )
    val itemStateList = listOf("CiB", "Item only", "Box only", "Manual only", "Item+Box", "Item+Manual", "Box+Manual")
    val itemRegionList = listOf("PAL", "USA-NTSC", "JPN-NTSC", "WORLD")
    val itemModded = listOf("No","Yes")
    val itemPurchaseCurrencyList = listOf("£", "€", "$", "Other")
    return when(listNnumber) {
        0 -> { itemTypeList }
        1 -> { itemBrandList }
        2 -> { itemStateList }
        3 -> { itemRegionList }
        4 -> { itemModded }
        5 -> { itemPurchaseCurrencyList }
        else -> { listOf() }
    }
}

fun getConsoleModelList(modelId: Int) : List<String> {
    val consoleNintendoModels = listOf("NES/FAMICOM","SNES/SFC", "NINTENDO 64", "VIRTUALBOY", "GAMECUBE", "WII", "WII-U", "SWITCH", "GAMEBOY", "3/DS")
    val consolesSegaModels = listOf("MASTER SYSTEM", "MEGADRIVE", "SEGA CD", "32X", "GAMEGEAR", "SATURN", "DREAMCAST")
    val consoleSonyModels = listOf("PS1", "PS2", "PS3", "PS4", "PS5")
    val consolesMicrosoftModels = listOf("XBOX", "XBOX360", "XBOX ONE", "XBOX ONE S", "XBOX ONE X", "XBOX S", "XBOX S")
    val consoleSnkModels = listOf("NEOGEO AES", "NEOGEO MVS", "NEOGEO POCKET")
    val consoleNecModels = listOf("PC ENGINE", "COREGRAFX", "COREGRAFX II", "TURBO DUO", "TURBO DUO R", "TURBO DUO RX", "SUPERGRAFX", "GT", "TURBOGRAFX")

    return when(modelId) {
        0 -> consoleNintendoModels
        1 -> consolesSegaModels
        2 -> consoleSonyModels
        3 -> consolesMicrosoftModels
        4 -> consoleSnkModels
        else -> consoleNecModels
    }
}