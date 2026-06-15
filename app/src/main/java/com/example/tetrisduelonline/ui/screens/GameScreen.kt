package com.example.tetrisduelonline.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tetrisduelonline.ui.viewmodels.GameViewModel

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    vm: GameViewModel,
    onBackToHome: () -> Unit
) {
    val state by vm.state.collectAsState()


    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Text(
            text = "Tetris Duel Online",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Puntos: ${state.score}",
                fontSize = 13.sp
            )

            Text(
                text = "Sala: ${state.roomCode}",
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 14.sp
            )


            Text(
                text = "Líneas: ${state.lines}",
                fontSize = 13.sp
            )

            Text(
                text = "Ataque: ${state.lastAttack}",
                fontSize = 13.sp
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {


            TetrisBoard(
                board = state.board,
                currentPiece = state.currentPieceCells,
                modifier = Modifier.fillMaxWidth(0.72f)
            )

            Spacer(modifier = Modifier.width(6.dp))


        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ControlButton(
                text = "←"
            ) {
                vm.moveLeft()
            }

            ControlButton(
                text = "↻"
            ) {
                vm.rotate()
            }

            ControlButton(
                text = "→"
            ) {
                vm.moveRight()
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ControlButton(
                text = "↓"
            ) {
                vm.softDrop()
            }

            ControlButton(
                text = "⇩"
            ) {
                vm.hardDrop()
            }
        }

        if (state.isGameOver || state.winner.isNotEmpty()) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = if (state.winner == "Victoria")
                    "🏆 GANASTE"
                else
                    "💀 PERDISTE",
                fontSize = 30.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Puntaje: ${state.score}"
            )

            Text(
                text = "Líneas: ${state.lines}"
            )

            Text(
                text = "Duración: ${state.durationSeconds} s"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {

                    onBackToHome()
                }
            ) {
                Text("Volver al inicio")
            }
        }

        if (state.show37Message) {
            Text(
                text = "Modo 37 activo",
                modifier = Modifier.padding(top = 2.dp),
                fontSize = 12.sp
            )
        }

    }
}


@Composable
fun ControlButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(54.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SmallGameButton(
    text: String,
    description: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.size(38.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = text,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = description,
            fontSize = 8.sp,
            textAlign = TextAlign.Center
        )
    }
}