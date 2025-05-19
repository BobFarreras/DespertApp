package com.deixebledenkaito.despertapp.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm

import androidx.compose.material.icons.filled.Settings

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun ButtonsBottomBar(
    onSettingsClick: () -> Unit,
    onAlarmClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedButtom: String,
) {


    Box(modifier = modifier){
        Row(
            modifier = Modifier.align(Alignment.Center).padding(horizontal = 30.dp).padding(bottom = 38.dp),

        ) {
            IconButton(onClick = onAlarmClick) {
                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = "Alarm",
                    modifier = Modifier.size(40.dp).padding(4.dp), // Mida lleugerament reduïda
                    tint = if(selectedButtom == "Alarmes") Color.White else Color.White.copy(alpha = 0.4f)

                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(40.dp).padding(4.dp), // Mida lleugerament reduïda
                    tint = if(selectedButtom == "Settings") Color.White else Color.White.copy(alpha = 0.4f)
                )
            }
        }
    }
}