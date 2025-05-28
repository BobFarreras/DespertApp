package com.deixebledenkaito.despertapp.ui.screens.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AddAlarmFAB(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color.White.copy(alpha = 0.9f),
        contentColor = Color.White,
        modifier = Modifier.padding(16.dp)
    ) {
        Icon(

            imageVector = Icons.Default.Add,
            tint = Color.Black,
            contentDescription = "Afegir alarma",
            modifier = Modifier.size(24.dp)


        )


    }
}