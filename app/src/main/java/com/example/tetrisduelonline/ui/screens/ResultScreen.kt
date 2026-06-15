package com.example.tetrisduelonline.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ResultScreen(
    winner: String,
    score: Int,
    lines: Int,
    duration: Int,
    onExit: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("Ganador: $winner")

        Spacer(modifier = Modifier.height(12.dp))

        Text("Puntaje: $score")

        Text("Líneas eliminadas: $lines")

        Text("Duración: $duration segundos")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onExit
        ) {
            Text("Salir")
        }
    }
}