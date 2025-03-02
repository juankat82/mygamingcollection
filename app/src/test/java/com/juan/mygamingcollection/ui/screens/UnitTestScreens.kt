package com.juan.mygamingcollection.ui.screens

import org.junit.Assert
import org.junit.Test

class UnitTestScreens {

    val seeFullCollection = Screens.BottomScreens.SeeFullCollection
    val addNewItem = Screens.BottomScreens.AddNewItem
    val uploadImages = Screens.BottomScreens.UploadImages
    val loginOrRegister = Screens.OtherScreens.LoginOrRegisterScreen

    @Test
    fun `test class types`() {
        Assert.assertFalse(seeFullCollection !is Screens.BottomScreens.SeeFullCollection)
        Assert.assertFalse(seeFullCollection !is Screens.BottomScreens)
        Assert.assertFalse(seeFullCollection !is Screens)

        Assert.assertTrue(addNewItem is Screens.BottomScreens.AddNewItem)
        Assert.assertTrue(addNewItem is Screens.BottomScreens)
        Assert.assertTrue(addNewItem is Screens)

        Assert.assertTrue(uploadImages is Screens.BottomScreens.UploadImages)
        Assert.assertTrue(uploadImages is Screens.BottomScreens)
        Assert.assertTrue(uploadImages is Screens)

        Assert.assertTrue(loginOrRegister is Screens.OtherScreens.LoginOrRegisterScreen)
        Assert.assertTrue(loginOrRegister is Screens.OtherScreens)
        Assert.assertTrue(loginOrRegister is Screens)
    }
}