package com.juan.mygamingcollection.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.Observer
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.juan.mygamingcollection.MainActivity
import com.juan.mygamingcollection.R
import com.juan.mygamingcollection.data.firebaseDB.FirebaseDBConnectImpl
import com.juan.mygamingcollection.data.viewmodel.ItemsViewModel
import com.juan.mygamingcollection.data.viewmodel.ScreenViewModel
import com.juan.mygamingcollection.data.viewmodel.UserViewModel
import com.juan.mygamingcollection.logic.InternetChecker
import com.juan.mygamingcollection.model.Item
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val backgroundColor = Color(0.85490197f, 0.8862745f, 1.0f, 1.0f, ColorSpaces.Srgb)

@SuppressLint("SuspiciousIndentation")
@Composable
fun SeeFullCollection(userViewModel: UserViewModel, context: Context, itemsViewModel: ItemsViewModel, firebaseDBConnectImpl: FirebaseDBConnectImpl, screenViewModel: ScreenViewModel, drawerState: DrawerState) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val itemList = remember { mutableStateOf<List<Item>>(listOf())}
    var searchItemList = rememberSaveable{ mutableListOf<Item>() }
    var isCollectionLoading by rememberSaveable{ mutableStateOf(true) }
    var timeLeft by remember { mutableStateOf(2) }
    timeLeft = 2
    var searchTerm by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val lazyState = rememberLazyListState()
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var syncTimes by rememberSaveable { mutableStateOf(0) }

    firebaseDBConnectImpl.itemListViewModel.searchItemViewModel.observe((context as MainActivity), object : Observer<List<Item>> {
        override fun onChanged(value: List<Item>) {
            searchItemList = value.toMutableList()
        }
    })

    firebaseDBConnectImpl.itemListViewModel.itemsList.observe((context), object : Observer<List<Item>> {
        override fun onChanged(value: List<Item>) {
            itemList.value = value.toMutableList()
        }
    })

    //SYNCS UPON STARTUP
    syncTimes = userViewModel.syncTimesLiveData.observeAsState().value ?: 0
    if (syncTimes == 0) {
        firebaseDBConnectImpl.syncOnlineItems(itemsViewModel, context, screenViewModel)
        syncTimes++
        userViewModel.setSyncTimes(syncTimes)
    }
    //////////////////
    firebaseDBConnectImpl.readAllRegistries(itemsViewModel)
    itemsViewModel.itemsList.observe(context, object : Observer<List<Item>> {
        override fun onChanged(value: List<Item>) {
            itemList.value = value
        }
    })

    screenViewModel.isSyncingEnabled.observe(context, object: Observer<Boolean> {
        override fun onChanged(value: Boolean) {
            isCollectionLoading = value
        }
    })

    BackHandler {
        if (drawerState.isOpen)
            scope.launch { drawerState.close() }
        if (drawerState.isClosed)
            showExitDialog = true
    }
        Column(modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)

            ) {
                OutlinedTextField(value = searchTerm, modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .weight(0.75f)
                    .padding(start = 20.dp, end = 0.dp, top = 65.dp, bottom = 10.dp),
                    minLines = 1,
                    maxLines = 1,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors().copy(
                        unfocusedContainerColor = colorResource(id = R.color.clear_purple_juan)
                    ).copy(focusedContainerColor = Color.LightGray),
                    onValueChange = {
                        if (it.length < 50) {
                            searchTerm = it
                        }
                        if (it.isEmpty())
                            searchTerm = ""
                        firebaseDBConnectImpl.searchItem(searchTerm)
                    },
                    shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp),
                    label = {
                        Text(
                            text = stringResource(id = R.string.enter_search_term),
                            color = Color.Black
                        )
                    })
                    Button(modifier = Modifier
                        .weight(0.25f)
                        .padding(top = 70.dp, end = 5.dp,)
                        .shadow(0.dp, RoundedCornerShape(2.dp, 2.dp, 2.dp, 2.dp))
                        .padding(start = 5.dp, end = 15.dp, top = 5.dp),
                            colors = ButtonDefaults.buttonColors()
                                .copy(containerColor = Color(context.getColor(R.color.purple_juan))),
                            onClick = {
                                Toast.makeText(context, "Syncing...", Toast.LENGTH_SHORT).show()
                                isCollectionLoading = true
                                firebaseDBConnectImpl.syncOnlineItems(itemsViewModel, context, screenViewModel)
                            }) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.update_item_list),
                                    contentDescription = "update item list",
                                    modifier = Modifier.height(20.dp).width(20.dp)
                                )
                                Spacer(Modifier.height(5.dp))
                                Text(
                                    stringResource(id = R.string.synchronize_database),
                                    style = TextStyle(fontWeight = FontWeight.Bold),
                                    textAlign = TextAlign.Center,
                                    fontSize = 10.sp,
                                    color = Color.Black
                                )
                            }
                        }
            }

            if (searchItemList.size > 0) {
                Surface(modifier = Modifier
                    .clickable {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                    .background(backgroundColor)
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 80.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(2.dp, Color.LightGray),
                    shadowElevation = 5.dp,
                    tonalElevation = 5.dp,
                    content = {
                        LazyColumn(
                            modifier = Modifier
                                .padding(vertical = 0.dp, horizontal = 0.dp)
                                .background(colorResource(id = R.color.clear_purple_juan)),
                            state = lazyState,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            items(count = searchItemList.size, itemContent = { index ->
                                ItemCollection(
                                    context,
                                    searchItemList[index],
                                    firebaseDBConnectImpl
                                )
                            })
                        }
                    })
               }
        else {
            Surface(modifier = Modifier
                .clickable {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
                .background(backgroundColor)
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 80.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(2.dp, Color.LightGray),
                shadowElevation = 5.dp,
                tonalElevation = 5.dp,
                content = {
                    LaunchedEffect(null) {
                        while (timeLeft > 0) {
                            delay(100L)
                            timeLeft--
                        }
                        if (timeLeft == 0)
                            isCollectionLoading = false
                    }
                    firebaseDBConnectImpl.readAllRegistries(itemsViewModel)

                    itemsViewModel.itemsList.observe(context, object : Observer<List<Item>> {
                            override fun onChanged(value: List<Item>) {
                                itemList.value = value
                            }
                        })

                    if (isCollectionLoading) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 50.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .width(200.dp)
                                    .padding(top = 200.dp)
                                    .align(Alignment.CenterHorizontally),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                trackColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                            Text(
                                modifier = Modifier
                                    .width(200.dp)
                                    .padding(top = 200.dp, start = 25.dp)
                                    .align(Alignment.CenterHorizontally),
                                text = stringResource(id = R.string.loading_text),
                                color = Color.Red,
                                fontSize = 25.sp,
                                fontFamily = FontFamily(Font(R.font.super_mario_bros_font))
                            )
                        }
                    }
                    else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    colorResource(id = R.color.clear_purple_juan)
                                )
                        ) {
                            items(count = itemList.value.size, itemContent = { index ->
                                ItemCollection(
                                    context,
                                    itemList.value.get(index),
                                    firebaseDBConnectImpl
                                )
                            })
                        }
                    }
                })
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
                    context.finish()
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
private fun ItemCollection(
    context: MainActivity,
    item: Item,
    firebaseDBConnectImpl: FirebaseDBConnectImpl
) {
    var isEditingEnabled by rememberSaveable { mutableStateOf(false) }
    val editedItem by remember { mutableStateOf(item.copy()) }
    var shouldShowUpdateDialog by rememberSaveable{mutableStateOf(false)}
    var shouldEraseRegistryDialog by rememberSaveable { mutableStateOf(false) }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        shape = RoundedCornerShape(3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column {
          if (!isEditingEnabled) {
            OutlinedTextField(value = item.itemName,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 8.dp, end = 8.dp, bottom = 5.dp),
                minLines = 1,
                maxLines = 1,
                singleLine = true,
                colors = TextFieldDefaults.colors().copy(disabledTextColor = Color.Black),
                enabled = isEditingEnabled,
                onValueChange = { if (it.length < 50) item.itemName = it },
                shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp),
                label = {
                    Text(
                        text = stringResource(id = R.string.item_name),
                        color = Color.Black
                    )
                }
            )
            OutlinedTextField(value = item.itemModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 8.dp, end = 8.dp, bottom = 5.dp),
                minLines = 1,
                maxLines = 1,
                singleLine = true,
                colors = TextFieldDefaults.colors().copy(disabledTextColor = Color.Black),
                enabled = isEditingEnabled,
                onValueChange = { if (it.length < 50) item.itemModel = it },
                shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp),
                label = {
                    Text(
                        text = stringResource(id = R.string.item_model),
                        color = Color.Black
                    )
                }
            )
            OutlinedTextField(value = item.itemType,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 8.dp, end = 8.dp, bottom = 5.dp),
                minLines = 1,
                maxLines = 1,
                singleLine = true,
                colors = TextFieldDefaults.colors().copy(disabledTextColor = Color.Black),
                enabled = isEditingEnabled,
                onValueChange = { if (it.length < 50) item.itemType = it },
                shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp),
                label = {
                    Text(
                        text = stringResource(id = R.string.type_of_item),
                        color = Color.Black
                    )
                }
            )
            OutlinedTextField(value = item.itemBrand,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 8.dp, end = 8.dp, bottom = 5.dp),
                minLines = 1,
                maxLines = 1,
                singleLine = true,
                colors = TextFieldDefaults.colors().copy(disabledTextColor = Color.Black),
                enabled = isEditingEnabled,
                onValueChange = { if (it.length < 50) item.itemBrand = it },
                shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp),
                label = {
                    Text(
                        text = stringResource(id = R.string.item_brand),
                        color = Color.Black
                    )
                }
            )
            OutlinedTextField(value = item.itemCompleteness,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 8.dp, end = 8.dp, bottom = 5.dp),
                minLines = 1,
                maxLines = 1,
                singleLine = true,
                colors = TextFieldDefaults.colors().copy(disabledTextColor = Color.Black),
                enabled = isEditingEnabled,
                onValueChange = { if (it.length < 50) item.itemCompleteness = it },
                shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp),
                label = {
                    Text(
                        text = stringResource(id = R.string.item_complete_state),
                        color = Color.Black
                    )
                }
            )
            OutlinedTextField(value = item.itemRegion,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 8.dp, end = 8.dp, bottom = 5.dp),
                minLines = 1,
                maxLines = 1,
                singleLine = true,
                colors = TextFieldDefaults.colors().copy(disabledTextColor = Color.Black),
                enabled = isEditingEnabled,
                onValueChange = { if (it.length < 50) item.itemRegion = it },
                shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp),
                label = {
                    Text(
                        text = stringResource(id = R.string.item_region),
                        color = Color.Black
                    )
                }
            )
            OutlinedTextField(value = item.itemModded,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 8.dp, end = 8.dp, bottom = 5.dp),
                minLines = 1,
                maxLines = 1,
                singleLine = true,
                colors = TextFieldDefaults.colors().copy(disabledTextColor = Color.Black),
                enabled = isEditingEnabled,
                onValueChange = { if (it.length < 50) item.itemModded = it },
                shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp),
                label = {
                    Text(
                        text = stringResource(id = R.string.item_is_modded),
                        color = Color.Black
                    )
                }
            )
            OutlinedTextField(value = item.itemPrice,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 8.dp, end = 8.dp, bottom = 5.dp),
                minLines = 1,
                maxLines = 1,
                singleLine = true,
                colors = TextFieldDefaults.colors().copy(disabledTextColor = Color.Black),
                enabled = isEditingEnabled,
                onValueChange = { if (it.length < 50) item.itemPrice = it },
                shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp),
                label = {
                    Text(
                        text = stringResource(id = R.string.price_string),
                        color = Color.Black
                    )
                }
            )
            OutlinedTextField(value = item.itemPriceCurrency,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 8.dp, end = 8.dp, bottom = 5.dp),
                minLines = 1,
                maxLines = 1,
                singleLine = true,
                colors = TextFieldDefaults.colors().copy(disabledTextColor = Color.Black),
                enabled = isEditingEnabled,
                onValueChange = { if (it.length < 50) item.itemPriceCurrency = it },
                shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp),
                label = {
                    Text(
                        text = stringResource(id = R.string.item_purchase_currency),
                        color = Color.Black
                    )
                }
            )
            OutlinedTextField(value = item.itemYear,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 8.dp, end = 8.dp, bottom = 5.dp),
                minLines = 1,
                maxLines = 1,
                singleLine = true,
                colors = TextFieldDefaults.colors().copy(disabledTextColor = Color.Black),
                enabled = isEditingEnabled,
                onValueChange = { if (it.length < 50) item.itemYear = it },
                shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp),
                label = {
                    Text(
                        text = stringResource(id = R.string.year_text),
                        color = Color.Black
                    )
                }
            )
        }
        else {
              val itemNameErrorTextVisibility by rememberSaveable { mutableStateOf(false) }
              val itemModelErrorTextVisibility by rememberSaveable { mutableStateOf(false) }
              var openTrigger by rememberSaveable{ mutableStateOf(false) }

              val dropMenuTextFieldTexts = listOf(
                  R.string.type_of_item,
                  R.string.item_brand,
                  R.string.item_complete_state,
                  R.string.item_region,
                  R.string.item_is_modded,
                  R.string.item_purchase_currency
              )

              var tempName by rememberSaveable { mutableStateOf (editedItem.itemName) }
              Column(modifier = Modifier
                  .fillMaxSize()
                  .background(
                      colorResource(id = R.color.clear_purple_juan)
                  )) {
                      OutlinedTextField(value = tempName,
                          modifier = Modifier
                              .fillMaxWidth()
                              .padding(top = 15.dp, start = 8.dp, end = 8.dp, bottom = 5.dp),
                          minLines = 1,
                          maxLines = 1,
                          singleLine = true,
                          onValueChange = { if (it.length<50) {
                              editedItem.itemName = it
                              tempName = it
                          }},
                          shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp),
                          label = { Text(text = stringResource(id = R.string.item_name), color = Color.Black) },
                      )
                      if (itemNameErrorTextVisibility)
                          Text(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
                              text = stringResource(id = R.string.must_enter_name), fontSize = 18.sp, color = Color.Red)

                      var tempItemType by rememberSaveable { mutableStateOf(editedItem.itemType) }
                      if (tempItemType.equals("Console")) {
                          val modelList = when (editedItem.itemBrand) {
                              "Nintendo" -> getConsoleModelList(0)
                              "Sega" -> getConsoleModelList(1)
                              "Sony" -> getConsoleModelList(2)
                              "Microsoft" -> getConsoleModelList(3)
                              "Snk" -> getConsoleModelList(4)
                              else -> getConsoleModelList(5)
                          }
                          val text by rememberSaveable { mutableStateOf(modelList.get(0)) }
                          var tempModel by rememberSaveable{ mutableStateOf( text) }
                          var modelOpenTrigger by rememberSaveable { mutableStateOf(false) }

                          Text(text = stringResource(id = R.string.item_model), modifier = Modifier.padding(start = 8.dp, top = 8.dp))
                          ExposedDropdownMenuBox(modifier = Modifier
                              .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 5.dp), expanded = modelOpenTrigger, onExpandedChange = {modelOpenTrigger = !modelOpenTrigger}) {
                              TextField(
                                  modifier = Modifier
                                      .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                                      .fillMaxWidth(),
                                  value = tempModel,
                                  onValueChange = {},
                                  readOnly = true,
                                  singleLine = true,
                                  label = { Text(text = stringResource(id = R.string.pick_an_option))},
                                  trailingIcon = {ExposedDropdownMenuDefaults.TrailingIcon(expanded = modelOpenTrigger)},
                                  colors = ExposedDropdownMenuDefaults.textFieldColors())
                              ExposedDropdownMenu(expanded = modelOpenTrigger, onDismissRequest = { modelOpenTrigger = false }) {
                                  modelList.forEach {nextItem ->
                                      DropdownMenuItem(
                                          text = { Text(nextItem, style= MaterialTheme.typography.bodyLarge) },
                                          onClick = {
                                              tempModel = nextItem
                                              modelOpenTrigger = false },
                                          contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
                                  }
                              }
                          }
                      }
                      else {
                          var tempText by rememberSaveable { mutableStateOf("") }
                          if (editedItem.itemType.equals("Console"))
                              tempText = item.itemModel
                          else
                              tempText = editedItem.itemModel
                          
                          OutlinedTextField(value = tempText,
                              modifier = Modifier
                                  .fillMaxWidth()
                                  .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 5.dp),
                              minLines = 1,
                              maxLines = 1,
                              singleLine = true,
                              onValueChange = { if (it.length<50 && it.trim().isNotEmpty())
                                  tempText = it
                                  editedItem.itemModel = it
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
                              0 -> {
                                  editedItem.itemType = values
                                  tempItemType = values
                              }
                              1 -> { editedItem.itemBrand = values }
                              2 -> { editedItem.itemCompleteness = values }
                              3 -> { editedItem.itemRegion = values }
                              4 -> { editedItem.itemModded = values }
                          }
                      }
                      val price = SetupPriceDropDownMenu()
                      editedItem.itemPrice = price.split("/").get(0)
                      editedItem.itemPriceCurrency = price.split("/").get(1)
                      editedItem.itemYear = DatePickerDropDown()
              }
              var datePicked by rememberSaveable{ mutableStateOf("") }
              OutlinedTextField(value = if (datePicked.isEmpty())  item.itemYear else datePicked,
                  modifier = Modifier
                      .weight(0.3f)
                      .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 5.dp),
                  minLines = 1,
                  maxLines = 1,
                  singleLine = true,
                  enabled = false,
                  onValueChange = { },
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
                      value = if (datePicked.isEmpty())  item.itemYear else datePicked,
                      onValueChange = {},
                      readOnly = true,
                      singleLine = true,
                      label = { Text(text = stringResource(id = R.string.pick_an_option))},
                      trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = openTrigger)},
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
            ///////////////IMAGES////////////////////
            val lazyRowState = rememberLazyListState()
            LazyRow (modifier = Modifier
                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                .background(color = backgroundColor), state = lazyRowState){
                items (count = 1, itemContent =  {
                    if (!InternetChecker.isNetworkAvailable(context))
                        Toast.makeText(context, R.string.no_network_available_for_images, Toast.LENGTH_SHORT).show()

                    val imaTopSize = Pair(LocalConfiguration.current.screenWidthDp, LocalConfiguration.current.screenHeightDp)
                    //IMAGE 1/////
                    val urlFront = item.pictureFrontBox
                    var imaFrontDrawable: Drawable? = null
                    var popUpFrontImage by rememberSaveable { mutableStateOf(false) }
                    if (popUpFrontImage) {
                        Popup(
                            alignment = Alignment.Center,
                            onDismissRequest = { popUpFrontImage = !popUpFrontImage }) {
                            Image(
                                painter = rememberDrawablePainter(imaFrontDrawable),
                                contentDescription = "popup image 1",
                                modifier = Modifier
                                    .size(imaTopSize.first.dp, imaTopSize.second.dp)
                                    .background(TextFieldDefaults.colors().disabledTextColor)
                                    .border(5.dp, Color.Black, RectangleShape)
                                    .clickable { popUpFrontImage = !popUpFrontImage }
                            )
                        }
                    }
                    GlideImage(
                        failure = placeholder(R.drawable.image_item_unavailable),
                        loading = placeholder(R.drawable.image_item_unavailable),
                        alignment = Alignment.Center,
                        model = urlFront,
                        contentDescription = "front box",
                        modifier = Modifier
                            .size(200.dp, 200.dp)
                            .background(Color.Black)
                            .clickable {
                                popUpFrontImage = !popUpFrontImage
                            }
                    ) {
                        GlobalScope.launch {
                            try {
                                imaFrontDrawable = it.submit().get()
                            }
                            catch (e: Exception) {
                                Log.e("EXCEPTION", e.message.toString())
                            }
                        }
                        it
                    }
                    //////////////
                    //IMAGE 2/////
                    val urlBack = item.pictureBackBox
                    var imaBackDrawable: Drawable? = null
                    var popUpBackImage by rememberSaveable { mutableStateOf(false) }
                    VerticalDivider(modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 10.dp,bottom = 10.dp), thickness = 3.dp, color = colorResource(id = R.color.clear_purple_juan))
                    if (popUpBackImage) {
                        Popup(
                            alignment = Alignment.Center,
                            onDismissRequest = { popUpBackImage = !popUpBackImage }) {
                            Image(
                                painter = rememberDrawablePainter(imaBackDrawable),
                                contentDescription = "popup back image ",
                                modifier = Modifier
                                    .size(imaTopSize.first.dp, imaTopSize.second.dp)
                                    .background(TextFieldDefaults.colors().disabledTextColor)
                                    .border(5.dp, Color.Black, RectangleShape)
                                    .clickable { popUpBackImage = !popUpBackImage }
                            )
                        }
                    }
                    GlideImage(
                        failure = placeholder(R.drawable.image_item_unavailable),
                        loading = placeholder(R.drawable.image_item_unavailable),
                        alignment = Alignment.Center,
                        model = urlBack,
                        contentDescription = "back box",
                        modifier = Modifier
                            .size(200.dp, 200.dp)
                            .background(Color.Black)
                            .clickable {
                                popUpBackImage = !popUpBackImage
                            }
                    ) {
                        GlobalScope.launch {
                            try {
                                imaBackDrawable = it.submit().get()
                            }
                            catch (e: Exception) {
                                Log.e("EXCEPTION", e.message.toString())
                            }
                        }
                        it
                    }
                    VerticalDivider(modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 10.dp,bottom = 10.dp), thickness = 3.dp, color = colorResource(id = R.color.clear_purple_juan))
                    //IMAGE 3//
                    val urlGame = item.pictureGame
                    var imaGameDrawable: Drawable? = null
                    var popUpGameImage by rememberSaveable { mutableStateOf(false) }
                    if (popUpGameImage) {
                        Popup(
                            alignment = Alignment.Center,
                            onDismissRequest = { popUpGameImage = !popUpGameImage }) {
                            Image(
                                painter = rememberDrawablePainter(imaGameDrawable),
                                contentDescription = "popup back image ",
                                modifier = Modifier
                                    .size(imaTopSize.first.dp, imaTopSize.second.dp)
                                    .background(TextFieldDefaults.colors().disabledTextColor)
                                    .border(5.dp, Color.Black, RectangleShape)
                                    .clickable { popUpGameImage = !popUpGameImage }
                            )
                        }
                    }
                    GlideImage(
                        failure = placeholder(R.drawable.image_item_unavailable),
                        loading = placeholder(R.drawable.image_item_unavailable),
                        alignment = Alignment.Center,
                        model = urlGame,
                        contentDescription = "game label",
                        modifier = Modifier
                            .size(200.dp, 200.dp)
                            .background(Color.Black)
                            .clickable {
                                popUpGameImage = !popUpGameImage
                            }
                    ) {
                        GlobalScope.launch {
                            try {
                                imaGameDrawable = it.submit().get()
                            }
                            catch (e: Exception) {
                                Log.e("EXCEPTION", e.message.toString())
                            }
                        }
                        it
                    }
                })
            }
            /////REGISTRY EDITION////////
            ////DISABLE EDITION BUTTON///
            Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                if (isEditingEnabled) {
                    Button(modifier = Modifier
                        .padding(top = 5.dp, bottom = 5.dp, end = 5.dp, start = 5.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.lime)),
                        onClick = {
                            isEditingEnabled = false
                        }){
                        Image(painter = painterResource(id = R.drawable.cancel_icon), contentDescription = "edition_enabled")
                    }
                }
                ///DELETE ITEM//////////
                Button(modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp, end = 5.dp, start = 5.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.lime)),
                    onClick = {
                        shouldEraseRegistryDialog = true
                    }){
                    Image(painter = painterResource(id = R.drawable.delete_icon), contentDescription = "erasing_button")
                }
                if (shouldEraseRegistryDialog) {
                    AlertDialog(
                        modifier = Modifier.background(Color.LightGray),
                        onDismissRequest = { shouldEraseRegistryDialog = false },
                        confirmButton = {
                            TextButton(onClick = {
                                shouldEraseRegistryDialog = false
                                firebaseDBConnectImpl.eraseOneRegistry(item, context)
                            })
                            { Text(text = stringResource(id = android.R.string.ok)) }
                        },
                        dismissButton = {
                            TextButton(onClick = { shouldEraseRegistryDialog = false })
                            { Text(text = stringResource(id = android.R.string.cancel)) }
                        },
                        title = {
                            Text(textDecoration = TextDecoration.Underline, text = stringResource(id = R.string.erase_db_string))
                        },
                        text = {
                            Text(textAlign = TextAlign.Center, text = stringResource(id = R.string.wish_to_erase_db_string))
                        },
                        shape = RoundedCornerShape(8.dp),
                        icon = { Icons.Rounded.Close }
                    )
                }
                /////UPDATE REGISTRY //////////
                if (shouldShowUpdateDialog) {
                    AlertDialog(
                        modifier = Modifier.background(Color.LightGray),
                        onDismissRequest = { shouldShowUpdateDialog = false },
                        confirmButton = {
                            TextButton(onClick = {
                                editedItem.googleDBId = item.googleDBId
                                editedItem.itemId = editedItem.itemBrand+"-"+editedItem.itemName+"-"+editedItem.itemRegion
                                shouldShowUpdateDialog = false
                                isEditingEnabled = false
                                editedItem.pictureFrontBox = (BASE_STORAGE_ADDRESS+editedItem.itemBrand +"_"+editedItem.itemName+"_"+editedItem.itemRegion+"_front.jpg").lowercase()
                                editedItem.pictureBackBox = (BASE_STORAGE_ADDRESS+editedItem.itemBrand+"_"+editedItem.itemName+"_"+editedItem.itemRegion+"_back.jpg").lowercase()
                                editedItem.pictureGame = (BASE_STORAGE_ADDRESS+editedItem.itemBrand+"_"+editedItem.itemName+"_"+editedItem.itemRegion+"_cart.jpg").lowercase()
                                firebaseDBConnectImpl.onUpdateRegistry(editedItem, context)
                            })
                            { Text(text = stringResource(id = android.R.string.ok)) }
                        },
                        dismissButton = {
                                TextButton(onClick = { shouldShowUpdateDialog = false })
                            { Text(text = stringResource(id = android.R.string.cancel)) }
                        },
                        title = {
                            Text(textDecoration = TextDecoration.Underline, text = stringResource(id = R.string.update_db_string))
                        },
                        text = {
                            Text(textAlign = TextAlign.Center, text = stringResource(id = R.string.wish_to_update_db_string))
                        },
                        shape = RoundedCornerShape(8.dp),
                        icon = { Icons.Rounded.Close }
                    )
                }
                ///ENABLE EDITION MODE///////
                Button(modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp, end = 5.dp, start = 5.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.lime)),
                    onClick = {
                        if (isEditingEnabled == false) {
                            isEditingEnabled = true
                        }
                        else
                            shouldShowUpdateDialog = true
                    }){
                        Image(painter = painterResource(id =
                        if (isEditingEnabled)
                            R.drawable.accept_icon
                        else
                            R.drawable.edit_item
                        ), contentDescription = "Edition_enabled")
                }
            }
            HorizontalDivider(modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 10.dp,bottom = 10.dp), thickness = 3.dp, color = colorResource(id = R.color.black))
        }
    }
}