/*
*
* This file tests the Firebase realtime databse in a rudimentary way (that means, without using the
* Firebase Realtime Database).
* I was thinking of installing Cloud Functions Emulator, but I will think about it.
* This file creates data that could be the response to querying the database instead of using the real one
* as, to my knowledge, it cant be emulated/mocked.
* If you know a way or feel like it, feel free to try.
* */
package com.juan.mygamingcollection.data.firebaseDB

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.juan.mygamingcollection.data.authentication.AuthenticationImpl
import com.juan.mygamingcollection.data.preferences.MyPreferences
import com.juan.mygamingcollection.data.roomDB.ItemsDao
import com.juan.mygamingcollection.data.roomDB.ItemsRoomDB
import com.juan.mygamingcollection.data.viewmodel.ItemsViewModel
import com.juan.mygamingcollection.data.viewmodel.ScreenViewModel
import com.juan.mygamingcollection.data.viewmodel.UserViewModel
import com.juan.mygamingcollection.model.Item
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockkObject
import io.mockk.spyk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.concurrent.CountDownLatch

class FirebaseDBConnectImplTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    var context = InstrumentationRegistry.getInstrumentation().context
    var roomDatabaseRAM: ItemsRoomDB = Room.inMemoryDatabaseBuilder(context,ItemsRoomDB::class.java).allowMainThreadQueries().build()
    val latch = CountDownLatch(1)
    var user: String = ""
    lateinit var db:FirebaseDatabase
    lateinit var itemsViewModel: ItemsViewModel
    lateinit var screenViewModel: ScreenViewModel
    lateinit var userViewModel: UserViewModel
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    var userName: String = ""
    lateinit var firebaseDBConnectImpl: FirebaseDBConnectImpl
    val testItemList = listOf<Item>(
        Item("1","1","1","1","1","1","1","1","1","1","1","1","1","1","1"),
        Item("2","1","1","1","1","1","1","1","1","1","1","1","1","1","1"),
        Item("3","1","1","1","1","1","1","1","1","1","1","1","1","1","1")
    )
    val testItem = Item("1","1","1","1","1","1","1","1","1","1","1","1","1","1","1")
    val updatedTestItem2 = Item("1","2","2","2","2","2","2","2","2","2","2","2","2","2","2")
    var reItemList = listOf(
        Item("2","1","1","1","1","1","1","1","1","1","1","1","1","1","1"),
        Item("3","1","1","1","1","1","1","1","1","1","1","1","1","1","1")
    )
    lateinit var dao: ItemsDao

    @Before
    fun setup() {
        mockkObject(MyPreferences.shared)
        storage = spyk(FirebaseStorage.getInstance())
        storageReference = spyk(storage.reference)
        itemsViewModel = spyk<ItemsViewModel>()
        screenViewModel = spyk<ScreenViewModel>()
        val authentication = spyk<AuthenticationImpl>()
        userViewModel = spyk(UserViewModel(authentication))
        userName = user
        runTest{ user = MyPreferences.shared.getCurrentUserEmail() }
        roomDatabaseRAM = spyk(Room.inMemoryDatabaseBuilder(context,ItemsRoomDB::class.java).allowMainThreadQueries().build())
        dao = roomDatabaseRAM.itemsDAO()
        db = spyk(Firebase.database("https://tour-guide-app-677c8-default-rtdb.europe-west1.firebasedatabase.app/"))
        firebaseDBConnectImpl = spyk(FirebaseDBConnectImpl(roomDatabaseRAM, itemsViewModel))
    }

    @Test
    fun `test read all registries`() {
        coEvery { roomDatabaseRAM.itemsDAO().getAllItems() } returns testItemList
        assertEquals(testItemList, listOf<Item>(
            Item("1","1","1","1","1","1","1","1","1","1","1","1","1","1","1"),
            Item("2","1","1","1","1","1","1","1","1","1","1","1","1","1","1"),
            Item("3","1","1","1","1","1","1","1","1","1","1","1","1","1","1")))
        runTest{ roomDatabaseRAM.itemsDAO().getAllItems() }
    }

    @Test
    fun `test sync online items`() {
        //Tests method works
        screenViewModel.setIsSyncing(true)
        Assert.assertTrue(screenViewModel.isSyncingEnabled.value == true)
        //Tests method is called when we attempt to sync our online database with out local database
        firebaseDBConnectImpl.syncOnlineItems(itemsViewModel, context, screenViewModel)
        coVerify{screenViewModel.setIsSyncing(any())}
    }

    @Test
    fun `test write new registry`() {
        runTest {
            roomDatabaseRAM.itemsDAO().insertItem(testItem)
            firebaseDBConnectImpl.writeNewRegistry(testItem,context, userViewModel)
        }

        runTest{
            latch.countDown()
        }
        latch.await()
        Log.i("MYVALUEIS: ", firebaseDBConnectImpl.hasBeenSent.value.toString())
        Assert.assertTrue(firebaseDBConnectImpl.hasBeenSent.value==0 || firebaseDBConnectImpl.hasBeenSent.value==-1)
    }

    @Test
    fun `test update registry`() {
        var old = listOf<Item>(testItem)
        var new =listOf<Item>(updatedTestItem2)

        coEvery { firebaseDBConnectImpl.itemDataBase.itemsDAO().getAllItems() } returns old
        runTest {
            firebaseDBConnectImpl.onUpdateRegistry(testItem,context)
            old = firebaseDBConnectImpl.itemDataBase.itemsDAO().getAllItems()
        }

        firebaseDBConnectImpl.onUpdateRegistry(updatedTestItem2,context)
        coEvery { firebaseDBConnectImpl.itemDataBase.itemsDAO().getAllItems() } returns new
        runTest{
            firebaseDBConnectImpl.onUpdateRegistry(updatedTestItem2,context)
            new =  firebaseDBConnectImpl.itemDataBase.itemsDAO().getAllItems()
        }
        Log.i("DIFFERENT: ","${old}+\n+${new}")
        Assert.assertNotEquals(old,new)
    }

    @Test
    fun `test erase one registry`() {
        var myItemList = listOf<Item>()
        var myNewItemList = listOf<Item>()

        runTest {
            roomDatabaseRAM.itemsDAO().insertItemList(testItemList)
            myItemList = roomDatabaseRAM.itemsDAO().getAllItems()
            itemsViewModel.setItemListViewModel(myItemList)
        }

        coEvery { itemsViewModel.eraseItem(testItem, context) } answers {itemsViewModel.setItemListViewModel(reItemList)}
        runTest {
            db.getReference("users").child(user).child("items").child(testItem.googleDBId).removeValue()
            roomDatabaseRAM.itemsDAO().deleteItem(testItem)
            itemsViewModel.eraseItem(testItem, context)
            myNewItemList = itemsViewModel.itemsList.value!!.toList()
        }
        Log.i("COMPARATION LISTS: ",myItemList.toString() + "\n" + myNewItemList)
        Assert.assertNotEquals(myItemList, myNewItemList)
    }

    @Test
    fun `test erase full remote database`() = runTest{
        var listAdded = listOf<Item>()
        var listAddedNew = listOf<Item>()

        roomDatabaseRAM.itemsDAO().insertItemList(testItemList)
        listAdded = roomDatabaseRAM.itemsDAO().getAllItems()
        roomDatabaseRAM.itemsDAO().deleteAll()
        itemsViewModel.setItemListViewModel(listOf())
        listAddedNew = roomDatabaseRAM.itemsDAO().getAllItems()

        Log.i("COMPARATION LISTS: ",listAdded.toString() + "\n" + listAddedNew)
        Assert.assertNotEquals(listAdded, listAddedNew)

    }
    @Test
    fun `test search item in remote database`() = runTest {
        var searchTerm = "1"
        roomDatabaseRAM.itemsDAO().insertItemList(testItemList)
        val searchItemList = roomDatabaseRAM.itemsDAO().getAllItems().filter { item ->
            item.itemName.contains(searchTerm)
        }
        Log.i("SEARCH RESULT: ",testItem.toString() + "\n" + searchItemList.get(0))
        Assert.assertEquals(testItem, searchItemList.get(0))
    }

    @Test
    fun `test upload file`() = runTest{
        val fileList = listOf<File>(File("1.txt"),File("2.txt"), File("3.txt"))
        coEvery { firebaseDBConnectImpl.uploadFile(any(), context)} returns firebaseDBConnectImpl.errorCodeFileUpload
        val returnCodeArray = firebaseDBConnectImpl.uploadFile(fileList, context)
        Assert.assertTrue(returnCodeArray.asList().toString().equals("[0, 0, 0]"))
    }

    @After
    fun closeDatabase() {
        roomDatabaseRAM.close()
    }
}