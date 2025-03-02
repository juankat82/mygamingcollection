package com.juan.mygamingcollection.data.firebaseDB

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyListState
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.juan.mygamingcollection.R
import com.juan.mygamingcollection.data.preferences.MyPreferences
import com.juan.mygamingcollection.data.roomDB.ItemsRoomDB
import com.juan.mygamingcollection.data.viewmodel.ItemsViewModel
import com.juan.mygamingcollection.data.viewmodel.ScreenViewModel
import com.juan.mygamingcollection.data.viewmodel.UserViewModel
import com.juan.mygamingcollection.model.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.cancellation.CancellationException

class FirebaseDBConnectImpl(roomDatabase: ItemsRoomDB, itemsViewModel: ItemsViewModel) : FirebaseDBConnect {

    lateinit var db:FirebaseDatabase
    var user: String = ""
    var hasBeenSent = MutableLiveData(-1)
    val itemDataBase = roomDatabase
    val itemListViewModel = itemsViewModel
    var storage: FirebaseStorage = FirebaseStorage.getInstance()
    val storageReference =storage.reference

    init {
        createConnection()
        GlobalScope.launch (Dispatchers.Main) {
            runBlocking {
                user = GlobalScope.async {
                    MyPreferences.shared.getCurrentUserEmail()
                }.await()
            }
        }
        db
    }
    val userName = Firebase.auth.currentUser?.email?.split("@")?.get(0) ?: user

    override fun createConnection() {
        db = Firebase.database("https://tour-guide-app-677c8-default-rtdb.europe-west1.firebasedatabase.app/")
    }

    override fun readAllRegistries(itemsViewModel: ItemsViewModel) {
        GlobalScope.launch {
            val list = itemDataBase.itemsDAO().getAllItems()
            withContext(Dispatchers.Main){
                itemsViewModel.setItemListViewModel(list)
            }
        }
    }

    override fun syncOnlineItems(itemsViewModel: ItemsViewModel, context: Context, screenViewModel: ScreenViewModel)  {
        screenViewModel.setIsSyncing(true)
        val query = db.getReference("users").child(userName).child("items")
        val list = mutableListOf<Item>()

        query.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.mapNotNull {
                    val item = it.getValue(Item::class.java)
                    item?.googleDBId = it.key ?: ""
                    list.add(item!!)
                }

                GlobalScope.launch {
                    itemDataBase.itemsDAO().deleteAll()
                    itemDataBase.itemsDAO().insertItemList(list)
                    val job = GlobalScope.launch(Dispatchers.Main) {
                        try {
                            itemsViewModel.setItemListViewModel(list)
                        } catch (e: CancellationException) {
                            Toast.makeText(context, "Cancelling Sync", Toast.LENGTH_SHORT).show()
                        } finally {
                            screenViewModel.setIsSyncing(false)
                        }
                    }
                    if (!job.isActive)
                        job.join()
                    }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to sync data. Tray again later.", Toast.LENGTH_SHORT).show()
            }
        })
        screenViewModel.setIsSyncing(true)
    }

    override fun writeNewRegistry(
        newItem: Item,
        context: Context,
        userViewModel: UserViewModel,
        lazyListState: LazyListState
    ) {
        var roomNewItem: Item
        val query = db.getReference("users").child(userName).child("items").push().setValue(newItem)

        query.addOnCompleteListener {
            it.addOnSuccessListener {
                hasBeenSent.value = 0
                Toast.makeText(context, R.string.new_item_inserted_successfully, Toast.LENGTH_SHORT).show()

                db.getReference("users").child(userName).child("items").orderByKey().limitToLast(1).addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        roomNewItem = if (!snapshot.key.isNullOrEmpty())
                            newItem.copy(googleDBId = snapshot.key ?: "")
                        else
                            newItem.copy(googleDBId = newItem.itemId)

                        GlobalScope.launch {
                            itemDataBase.itemsDAO().insertItem(roomNewItem)
                            readAllRegistries(itemListViewModel)
                        }
                    }
                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onChildRemoved(snapshot: DataSnapshot) {}
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onCancelled(error: DatabaseError) {}
                })
            }.addOnFailureListener {
                hasBeenSent.value = -1
                Toast.makeText(context, R.string.new_item_inserted_failure, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onUpdateRegistry(item: Item, context: Context) {
        db.getReference("users").child(user).child("items").child(item.googleDBId).setValue(item).addOnCompleteListener {
            it.addOnSuccessListener {
                GlobalScope.launch {
                    itemDataBase.itemsDAO().updateItem(item)
                    itemListViewModel.updateItem(item, context)
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to Update Item", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun eraseOneRegistry(item: Item, context: Context) {
        db.getReference("users").child(user).child("items").child(item.googleDBId).removeValue().addOnCompleteListener {
            it.addOnSuccessListener {
                GlobalScope.launch {
                    itemDataBase.itemsDAO().deleteItem(item)
                    itemListViewModel.eraseItem(item, context)
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to remove Item", Toast.LENGTH_SHORT).show()
            }
        }
        GlobalScope.launch {
            itemDataBase.itemsDAO().deleteItem(item)
        }
    }

    override fun eraseRemoteDatabase(itemsViewModel: ItemsViewModel, context: Context) {
        val itemIdList = mutableListOf<String>()
        itemsViewModel.itemsList.value?.forEach { item ->
            itemIdList.add(item.googleDBId)
        }
        if (itemIdList.size > 0) {
            var count = 1
            itemIdList.forEach { item ->
                db.getReference("users").child(user).child("items").child(item).removeValue()
                    .addOnCompleteListener {
                        it.addOnSuccessListener {
                            if (count > 0) {
                                itemsViewModel.setItemListViewModel(listOf())
                                Toast.makeText(context, "Success removing collection", Toast.LENGTH_SHORT).show()
                                count--
                            }
                        }.addOnFailureListener {
                            Toast.makeText(context, "Failed to remove Item", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        else
            Toast.makeText(context, "Can't erase empty collection.", Toast.LENGTH_SHORT).show()
    }

    fun searchItem(itemName: String) {
        val searchTerm = if (itemName==null) "" else itemName
        GlobalScope.launch {
            val itemListFlow = itemDataBase.itemsDAO().getAllItems().filter { item ->
                item.itemName.contains(searchTerm)
            }
            itemListViewModel.setSearchItemList(
                   if ( searchTerm.equals(""))
                     emptyList()
                   else
                     itemListFlow
            )
        }
    }

    fun uploadFile(fileList: List<File?>, context: Context) {
        fileList.forEachIndexed { i, file ->
            if (file != null) {
                    storageReference.child("images/" + file.name.lowercase()).putFile(file.toUri())
                        .addOnCompleteListener {
                            it.addOnSuccessListener {
                                Toast.makeText(context, "Image $i upload Succeed!", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                Toast.makeText(context, "Image $i Upload Failed!", Toast.LENGTH_SHORT).show()
                            }
                    }
            }
        }
     }
}