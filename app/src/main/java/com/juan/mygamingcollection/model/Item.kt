package com.juan.mygamingcollection.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(@field:ColumnInfo(name="googleBDId") @PrimaryKey var googleDBId: String = "",
                @field:ColumnInfo(name="itemId") var itemId: String = "",
                @field:ColumnInfo(name="itemName") var itemName: String = "",
                @field:ColumnInfo(name="itemModel") var itemModel: String = "",
                @field:ColumnInfo(name="itemType") var itemType: String = "",
                @field:ColumnInfo(name="itemBrand") var itemBrand: String = "",
                @field:ColumnInfo(name="itemCompleteness") var itemCompleteness: String = "",
                @field:ColumnInfo(name="itemRegion") var itemRegion: String = "",
                @field:ColumnInfo(name="itemModded") var itemModded: String = "",
                @field:ColumnInfo(name="itemPrice") var itemPrice: String = "",
                @field:ColumnInfo(name="itemPriceCurrency") var itemPriceCurrency: String = "",
                @field:ColumnInfo(name="itemYear") var itemYear: String = "",
                @field:ColumnInfo(name="pictureGame") var pictureGame: String = "",
                @field:ColumnInfo(name="pictureBackBox") var pictureBackBox: String = "",
                @field:ColumnInfo(name="pictureFrontBox") var pictureFrontBox: String = ""
)


