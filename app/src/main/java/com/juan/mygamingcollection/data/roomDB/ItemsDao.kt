package com.juan.mygamingcollection.data.roomDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.juan.mygamingcollection.model.Item

@Dao
interface ItemsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item)

    @Query("Select * FROM items")
    suspend fun getAllItems(): List<Item>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemList(itemList: List<Item>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateItem(item: Item)

    @Delete
    suspend fun deleteItem(item: Item)

    @Query("DELETE FROM items")
    suspend fun deleteAll()
}