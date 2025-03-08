package com.juan.mygamingcollection.data.firebaseDB

import android.content.Context
import com.juan.mygamingcollection.data.viewmodel.ItemsViewModel
import com.juan.mygamingcollection.data.viewmodel.ScreenViewModel
import com.juan.mygamingcollection.data.viewmodel.UserViewModel
import com.juan.mygamingcollection.model.Item

interface FirebaseDBConnect {

    fun createConnection()

    fun readAllRegistries(itemsViewModel: ItemsViewModel)

    fun syncOnlineItems(itemsViewModel: ItemsViewModel, context: Context, screenViewModel: ScreenViewModel)

    fun eraseOneRegistry(item: Item, context: Context)

    fun eraseRemoteDatabase(itemsViewModel: ItemsViewModel, context: Context)

    fun writeNewRegistry(
        newItem: Item,
        context: Context,
        userViewModel: UserViewModel
    )

    fun onUpdateRegistry(item: Item, context: Context)
}