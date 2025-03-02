package com.juan.mygamingcollection.data.roomDB

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.juan.mygamingcollection.data.authentication.AuthenticationImpl
import com.juan.mygamingcollection.data.preferences.MyPreferences
import com.juan.mygamingcollection.data.viewmodel.ScreenViewModel
import com.juan.mygamingcollection.data.viewmodel.UserViewModel
import com.juan.mygamingcollection.model.Item
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.collections.listOf

@RunWith(AndroidJUnit4::class)
class RoomDBTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    var context = InstrumentationRegistry.getInstrumentation().context
    lateinit var roomDatabase: ItemsRoomDB
    lateinit var roomDatabaseRAM: ItemsRoomDB

    val itemList = listOf<Item>(
        Item("1","1","1","1","1","1","1","1","1","1","1","1","1","1","1"),
        Item("2","1","1","1","1","1","1","1","1","1","1","1","1","1","1"),
        Item("3","1","1","1","1","1","1","1","1","1","1","1","1","1","1")
    )

    @Before
    fun setup() {
        mockkObject(MyPreferences.shared)
        roomDatabase = spyk<ItemsRoomDB>(Room.databaseBuilder(context,ItemsRoomDB::class.java, "items").fallbackToDestructiveMigration().build())
        roomDatabaseRAM = Room.inMemoryDatabaseBuilder(context,ItemsRoomDB::class.java).allowMainThreadQueries().build()

    }

    ///TESTS METHODS GETS CALLED./////
    @Test
    fun `test get all items gets called`() {
        coEvery { roomDatabase.itemsDAO().getAllItems() } returns listOf<Item>(Item("1","1","1","1","1","1","1","1","1","1","1","1","1","1","1"))
        assertEquals(listOf(Item("1","1","1","1","1","1","1","1","1","1","1","1","1","1","1")), listOf<Item>(Item("1","1","1","1","1","1","1","1","1","1","1","1","1","1","1")))
        runTest() {
            roomDatabase.itemsDAO().getAllItems()
        }
    }

    @Test
    fun `test insert items gets called`() {
        val item = mockk<Item>()
        coEvery { roomDatabase.itemsDAO().insertItem(item) } returnsArgument 0
        runTest {
            roomDatabase.itemsDAO().insertItem(item)
        }
        coVerify{ roomDatabase.itemsDAO().insertItem(item) }

    }

    @Test
    fun `test insert itemList gets called`() {
        val itemList = mockk<List<Item>>()
        coEvery { roomDatabase.itemsDAO().insertItemList(itemList) } returnsArgument 0
        runTest {
            roomDatabase.itemsDAO().insertItemList(itemList)
        }
        coVerify{ roomDatabase.itemsDAO().insertItemList(itemList) }
    }

    @Test
    fun `test delete itemList gets called`() {
        val item = mockk<Item>()
        coEvery { roomDatabase.itemsDAO().deleteItem(any()) } returnsArgument 0
        runTest{
            roomDatabase.itemsDAO().deleteItem(item)
        }
        coVerify{roomDatabase.itemsDAO().deleteItem(item)}
    }

    @Test
    fun `test update item gets called`() {
        val item = mockk<Item>()
        coEvery { roomDatabase.itemsDAO().updateItem(any()) } returnsArgument 0
        runTest{
            roomDatabase.itemsDAO().updateItem(item)
        }
        coVerify{roomDatabase.itemsDAO().updateItem(item)}
    }

    @Test
    fun `test delete all items dets called`() {
        coEvery { roomDatabase.itemsDAO().deleteAll() } returnsArgument 0
        runTest{
            roomDatabase.itemsDAO().deleteAll()
        }
        coVerify{roomDatabase.itemsDAO().deleteAll()}
    }
    ////////////////////////////////////
    /////TEST DATABASE DOES UPDATE DATA WHEN WE CALL ITS METHODS/////

    @After
    fun closeDatabase() {
        roomDatabase.close()
        roomDatabaseRAM.close()
    }
}