package com.juan.mygamingcollection.data.viewmodel

import androidx.compose.material3.DrawerState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.juan.mygamingcollection.ui.screens.Screens
import com.juan.mygamingcollection.ui.screens.otherScreens
import com.juan.mygamingcollection.ui.screens.screensInBottom
import javax.inject.Inject

class ScreenViewModel : ViewModel() {

    private var _screens = MutableLiveData<Int>()
    val screens: LiveData<Int>
        get() = _screens

    val screenList = screensInBottom
    val loginRegisterScreen = otherScreens

    init {
        _screens.value = 0
    }

    private var _isSyncingEnabled = MutableLiveData<Boolean>()
    val isSyncingEnabled: LiveData<Boolean>
        get() = _isSyncingEnabled

    private var _isDrawerOpen = MutableLiveData<Boolean>()

    fun setDrawerOpen(drawerState: DrawerState) {
        if (drawerState.isOpen)
            _isDrawerOpen.value = false
        else
            _isDrawerOpen.value = true
    }

    fun setScreen(screenNumber: Int) : Screens{
        _screens.value = screenNumber
        val scr = when (screens.value) {
            0, 1, 2 -> {
                screenList.get(screenNumber)
            }
            else -> {
                loginRegisterScreen.get(0)
            }
        }
        return scr
    }

    fun getScreens() = screens.value

    fun getSelectedScreen(): Screens {
        val scr = when (getScreens()){
            0, 1, 2 -> {
                screenList.get(screens.value!!)
            }
            else -> {
                loginRegisterScreen.get(0)
            }
        }
        return scr
    }

    fun setIsSyncing(isSyncing: Boolean) {
        _isSyncingEnabled.value = isSyncing
    }
}