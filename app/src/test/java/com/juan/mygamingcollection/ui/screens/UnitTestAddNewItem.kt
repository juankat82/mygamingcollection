package com.juan.mygamingcollection.ui.screens

import org.junit.Assert
import org.junit.Test

class UnitTestAddNewItem {

    val consoleNintendoModels = listOf("NES/FAMICOM","SNES/SFC", "NINTENDO 64", "VIRTUALBOY", "GAMECUBE", "WII", "WII-U", "SWITCH", "GAMEBOY", "3/DS")
    val consolesSegaModels = listOf("MASTER SYSTEM", "MEGADRIVE", "SEGA CD", "32X", "GAMEGEAR", "SATURN", "DREAMCAST")
    val consoleSonyModels = listOf("PS1", "PS2", "PS3", "PS4", "PS5")
    val consolesMicrosoftModels = listOf("XBOX", "XBOX360", "XBOX ONE", "XBOX ONE S", "XBOX ONE X", "XBOX S", "XBOX S")
    val consoleSnkModels = listOf("NEOGEO AES", "NEOGEO MVS", "NEOGEO POCKET")
    val consoleNecModels = listOf("PC ENGINE", "COREGRAFX", "COREGRAFX II", "TURBO DUO", "TURBO DUO R", "TURBO DUO RX", "SUPERGRAFX", "GT", "TURBOGRAFX")

    @Test
    fun `test console models`() {
        Assert.assertEquals(getConsoleModelList(0), consoleNintendoModels)
        Assert.assertEquals(getConsoleModelList(1), consolesSegaModels)
        Assert.assertEquals(getConsoleModelList(2), consoleSonyModels)
        Assert.assertEquals(getConsoleModelList(3), consolesMicrosoftModels)
        Assert.assertEquals(getConsoleModelList(4), consoleSnkModels)
        Assert.assertEquals(getConsoleModelList(5), consoleNecModels)
    }

    fun getConsoleModelList(modelId: Int) : List<String> {
        return when(modelId) {
            0 -> consoleNintendoModels
            1 -> consolesSegaModels
            2 -> consoleSonyModels
            3 -> consolesMicrosoftModels
            4 -> consoleSnkModels
            else -> consoleNecModels
        }
    }
}