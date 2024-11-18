package com.juan.mygamingcollection.data.roomDB

import androidx.room.Database
import androidx.room.RoomDatabase
import com.juan.mygamingcollection.model.Item

@Database(entities = [Item::class], version = 3)
abstract class ItemsRoomDB : RoomDatabase() {
    abstract fun itemsDAO(): ItemsDao
}