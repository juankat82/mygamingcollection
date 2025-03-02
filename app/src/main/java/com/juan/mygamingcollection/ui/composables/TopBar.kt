package com.juan.mygamingcollection.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juan.mygamingcollection.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onNavigationIconClick: () -> Unit) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth().border(border = BorderStroke(1.dp, color = Color.Black)),
        title = {
            Row {
                Text(
                    modifier = Modifier
                        .wrapContentSize(Alignment.BottomStart)
                        .padding(start = 25.dp)
                        .weight(0.5f, true),
                    text = stringResource(id = R.string.app_name_separated),
                    fontFamily = FontFamily(Font(R.font.super_mario_bros_font)),
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.black),
                    fontSize = 20.sp
                )}
            },
        navigationIcon = {
            IconButton(enabled = true, onClick = {
                onNavigationIconClick()
            }) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "open_close_drawer", modifier = Modifier.semantics { contentDescription = "open_close_drawer" })
            }
        },
        colors = TopAppBarColors(
            actionIconContentColor = Color.Blue,
            navigationIconContentColor = Color.Blue,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            scrolledContainerColor = Color.Blue,
            titleContentColor = Color.Blue)
    )
}