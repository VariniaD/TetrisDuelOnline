package com.example.tetrisduelonline.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onCreateRoom: () -> Unit,
    onJoinRoom: (String) -> Unit
) {

    var roomCode by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Button(
            onClick = onCreateRoom
        ) {
            Text("Crear Sala")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = roomCode,
            onValueChange = {
                roomCode = it
            },
            label = {
                Text("Código de Sala")
            }
        )
        Text(
            text = "Sala creada: $roomCode"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onJoinRoom(roomCode)
            }
        ) {
            Text("Unirse a Sala")
        }
    }
}