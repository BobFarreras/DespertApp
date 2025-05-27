package com.deixebledenkaito.despertapp.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.unit.dp


@Composable
fun TopBarDesplegable (

    showMenu: Boolean,
    onMenuToggle: (Boolean) -> Unit,

) {
    Box {
        IconButton(onClick = { onMenuToggle(!showMenu) }) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Perfil",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2F))
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }


        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { onMenuToggle(false) },
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            DropdownMenuItem(
                text = { Text("El meu perfil") },
                onClick = { /* TODO */ },
                leadingIcon = { Icon(Icons.Default.Person, null) }
            )
            DropdownMenuItem(
                text = { Text("Configuració") },
                onClick = { /* TODO */ },
                leadingIcon = { Icon(Icons.Default.Settings, null) }
            )
            HorizontalDivider()
//            DropdownMenuItem(
//                text = { Text("Tancar sessió", color = MaterialTheme.colorScheme.error) },
//                onClick = onLogout,
//                leadingIcon = {
//                    Icon(Icons.Default.Logout, null, tint = MaterialTheme.colorScheme.error)
//                }
//            )
        }
    }
}