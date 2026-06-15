package com.example.tetrisduelonline.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tetrisduelonline.ui.states.GameState

@Composable
fun HomeScreen(
    state: GameState,
    onCreateRoom: () -> Unit,
    onJoinRoom: (String) -> Unit
) {

    var roomCodeInput by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Tetris Duel Online",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onCreateRoom()
            }
        ) {
            Text("Crear Sala")
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (state.roomCode.isNotEmpty()) {
            Text(
                text = "Código de sala: ${state.roomCode}",
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Comparte este código con tu compañera.",
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = roomCodeInput,
            onValueChange = {
                roomCodeInput = it.uppercase()
            },
            label = {
                Text("Código de Sala")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                onJoinRoom(roomCodeInput.trim())
            }
        ) {
            Text("Unirse a Sala")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Estado: ${state.connectionStatus}",
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = state.message,
            textAlign = TextAlign.Center
        )

        if (state.roomCode.isNotEmpty() && !state.opponentConnected) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Esperando al segundo jugador...",
                textAlign = TextAlign.Center
            )
        }
    }
}