package com.juan.mygamingcollection.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser
import com.juan.mygamingcollection.R
import com.juan.mygamingcollection.data.viewmodel.ScreenViewModel
import com.juan.mygamingcollection.ui.screens.Screens
import com.juan.mygamingcollection.ui.screens.screensInBottom
import com.juan.mygamingcollection.ui.theme.Purple80

@Composable
fun BottomBar(
    navHostController: NavHostController,
    screenViewModel: ScreenViewModel,
    currentUser: MutableState<FirebaseUser?>
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val unpressedColor = Color(0.85490197f, 0.8862745f, 1.0f, 1.0f, ColorSpaces.Srgb)
    val pressedColor = Color(Purple80.toArgb())
    val componentWidth = remember { mutableStateOf (0.dp) }
    val density = LocalDensity.current
    val screenTabColors = remember { mutableStateListOf(unpressedColor, unpressedColor, unpressedColor) }
    val screensStats = screensInBottom
    if (screenTabColors[0].value == unpressedColor.value && screenTabColors[1].value == unpressedColor.value && screenTabColors[2].value == unpressedColor.value) {
        screenTabColors[0] = pressedColor
    }
    keyboardController?.hide()
    HorizontalDivider(thickness = 1.dp, color = Color.Black, modifier = Modifier.padding(vertical = 2.dp, horizontal = 2.dp))
    Row (horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier
        .fillMaxWidth()
        .onGloballyPositioned {
            componentWidth.value = with(density) {
                it.size.width.toDp()
            }
        }
        .background(color = MaterialTheme.colorScheme.primaryContainer)) {
            screensStats.fastForEachIndexed { index, bottomScreens ->
                if (currentUser.value?.email!!.contains("spanishfly82")) {
                    BottomBarItem(
                        index = index,
                        screensStats = screensStats,
                        screenTabColors = screenTabColors,
                        unpressedColor = unpressedColor,
                        pressedColor = pressedColor,
                        totalWidth = componentWidth.value,
                        navHostController = navHostController,
                        screenViewModel = screenViewModel
                    )
                }
                else {
                    if (index != 2 ) {
                        BottomBarItem(
                            index = index,
                            screensStats = screensStats,
                            screenTabColors = screenTabColors,
                            unpressedColor = unpressedColor,
                            pressedColor = pressedColor,
                            totalWidth = componentWidth.value,
                            navHostController = navHostController,
                            screenViewModel = screenViewModel
                        )
                    }
                }
            }
    }
}

@Composable
fun BottomBarItem(
    index: Int,
    screensStats: List<Screens.BottomScreens>,
    screenTabColors: SnapshotStateList<Color>,
    unpressedColor: Color,
    pressedColor: Color,
    totalWidth: Dp,
    navHostController: NavHostController,
    screenViewModel: ScreenViewModel
) {
    Column(
            modifier = Modifier
                .width(totalWidth / 3)
                .background(Color(screenTabColors.get(index).value)),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        IconButton( onClick = {
            //defines buttons color
            when(index) {
                -1, 0 -> {
                    screenTabColors[0] = pressedColor
                    screenTabColors[1] = unpressedColor
                    screenTabColors[2] = unpressedColor
                    screenViewModel.setScreen(0)
                }
                1 -> {
                    screenTabColors[0] = unpressedColor
                    screenTabColors[1] = pressedColor
                    screenTabColors[2] = unpressedColor
                    screenViewModel.setScreen(1)
                }
                2 -> {
                    screenTabColors[0] = unpressedColor
                    screenTabColors[1] = unpressedColor
                    screenTabColors[2] = pressedColor
                    screenViewModel.setScreen(2)
                }
            }
            screenViewModel.setScreen(index)
            navHostController.navigate(screenViewModel.getSelectedScreen().route) {
                popUpTo(0)
                navHostController.graph.startDestinationRoute?.let {
                    popUpTo(navHostController.graph.id) {
                        inclusive = true
                        saveState = true
                    }
                }
                launchSingleTop = true
                restoreState = true
            }
        }) {
            Icon(
                modifier = Modifier.padding(start = 3.dp, bottom = 3.dp, end = 3.dp).semantics{contentDescription = screensStats[index].bTitle},
                painter = painterResource(id = screensStats[index].icon),
                contentDescription = screensStats[index].bTitle
            )
        }
        Text(modifier = Modifier.padding(start = 3.dp, bottom = 3.dp, end = 3.dp), text = stringResource(id =
        when (index) {
          0 -> R.string.see_full_collection
          1 -> R.string.add_new_item
          2 -> R.string.upload_new_pictures
          else -> 0
        }))
    }
}