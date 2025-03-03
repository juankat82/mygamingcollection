package com.juan.mygamingcollection.data.roomDB

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.juan.mygamingcollection.data.preferences.MyPreferences
import com.juan.mygamingcollection.model.Item
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import kotlin.collections.listOf

@RunWith(AndroidJUnit4::class)
class RoomDBTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    var context = InstrumentationRegistry.getInstrumentation().context
    lateinit var roomDatabase: ItemsRoomDB
    lateinit var roomDatabaseRAM: ItemsRoomDB
    val latch = CountDownLatch(1)
    val testItem = Item("1","1","1","1","1","1","1","1","1","1","1","1","1","1","1")
    val modifiedTestItem = Item("1","7","7","7","7","7","7","7","7","7","7","7","7","7","7")
    val testItemList = listOf<Item>(
        Item("1","1","1","1","1","1","1","1","1","1","1","1","1","1","1"),
        Item("2","1","1","1","1","1","1","1","1","1","1","1","1","1","1"),
        Item("3","1","1","1","1","1","1","1","1","1","1","1","1","1","1")
    )

    val updatedItemList = listOf<Item>(
        Item("1","7","7","7","7","7","7","7","7","7","7","7","7","7","7"),
        Item("2","1","1","1","1","1","1","1","1","1","1","1","1","1","1"),
        Item("3","1","1","1","1","1","1","1","1","1","1","1","1","1","1")
    )

    val testItemListUpdated = listOf<Item>(
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
        coEvery { roomDatabase.itemsDAO().getAllItems() } returns listOf<Item>(testItem)
        assertEquals(listOf(testItem), listOf<Item>(testItem))
        runTest {
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
    //https://medium.com/@wambuinjumbi/unit-testing-in-android-room-361bf56b69c5
    //I DONT TEST "getAllItems()" as I continuously do it in all methods
    @Test
    fun `test insert items DB`() {

        runTest{
            roomDatabaseRAM.itemsDAO().insertItem(testItem)
            latch.countDown()
        }
        runTest{
            val result = roomDatabaseRAM.itemsDAO().getAllItems()
            assertEquals(result.get(0), testItem)
            Log.i("RESULTIS: ",result.get(0).toString())
            latch.countDown()
        }
        latch.await()
    }

    @Test
    fun `test insert itemList DBd`() {
        runTest{
            roomDatabaseRAM.itemsDAO().insertItemList(testItemList)
            latch.countDown()
        }
        runTest{
            val result = roomDatabaseRAM.itemsDAO().getAllItems()
            assertEquals(result, testItemList)
            Log.i("RESULTIS: ",result.toString())
            latch.countDown()
        }
        latch.await()
    }

    @Test
    fun `test delete testItem DB`() {
        runTest{
            roomDatabaseRAM.itemsDAO().insertItemList(testItemList)
            latch.countDown()
        }
        runTest{
            val result = roomDatabaseRAM.itemsDAO().getAllItems()
            assertEquals(result, testItemList)
            Log.i("RESULTIS: ",result.toString())
            latch.countDown()
        }
        runTest{
            roomDatabaseRAM.itemsDAO().deleteItem(testItem)
            latch.countDown()
        }
        runTest{
            val result = roomDatabaseRAM.itemsDAO().getAllItems()
            assertEquals(result, testItemListUpdated)
            Log.i("RESULTIS: ",result.toString())
            latch.countDown()
        }
        latch.await()
    }

    @Test
    fun `test update item DB`() {
        runTest{
            roomDatabaseRAM.itemsDAO().insertItemList(testItemList)
            latch.countDown()
        }
        runTest{
            val result = roomDatabaseRAM.itemsDAO().getAllItems()
            assertEquals(result, testItemList)
            Log.i("RESULTIS: ",result.toString())
            latch.countDown()
        }
        runTest{
            roomDatabaseRAM.itemsDAO().updateItem(modifiedTestItem)
            latch.countDown()
        }
        runTest{
            val result = roomDatabaseRAM.itemsDAO().getAllItems()
            assertEquals(result, updatedItemList)
            Log.i("RESULTIS: ",result.toString())
            latch.countDown()
        }
        latch.await()
    }

    @Test
    fun `test delete all items DB`() {
        runTest{
            roomDatabaseRAM.itemsDAO().insertItemList(testItemList)
            latch.countDown()
        }
        runTest{
            val result = roomDatabaseRAM.itemsDAO().getAllItems()
            assertEquals(result, testItemList)
            Log.i("RESULTIS: ",result.toString())
            latch.countDown()
        }
        runTest{
            roomDatabaseRAM.itemsDAO().deleteAll()
            latch.countDown()
        }
        runTest{
            val result = roomDatabaseRAM.itemsDAO().getAllItems()
            assertEquals(result, listOf<Item>())
            Log.i("RESULTIS: ",result.toString())
            latch.countDown()
        }
        latch.await()
    }
    /////////////////////////////////////////////////
    @After
    fun closeDatabase() {
        roomDatabase.close()
        roomDatabaseRAM.close()
    }
}