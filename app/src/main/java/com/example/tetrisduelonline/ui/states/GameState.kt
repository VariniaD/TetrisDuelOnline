package com.example.tetrisduelonline.ui.states

import com.example.tetrisduelonline.game.FallingPiece
import com.example.tetrisduelonline.game.TetrisEngine

// GameState representa el estado visible del juego.
//
// La pantalla observa este estado y se redibuja automáticamente
// cuando cambia algún valor.
data class GameState(

    // Tablero de 20 filas x 10 columnas.
    //
    // Valor 0 = celda vacía.
    // Valor 1 = bloque normal.
    // Valor 2 = basura fija.
    val board: List<List<Int>> = TetrisEngine.emptyBoard(),

    val currentPiece: FallingPiece = FallingPiece.random(),

    val nextPiece: FallingPiece = FallingPiece.random(),

    val score: Int = 0,

    val lines: Int = 0,

    val lastAttack: Int = 0,

    val message: String = "Presiona iniciar para comenzar",
    val durationSeconds: Int = 0,
    val show37Message: Boolean = false,

    val isRunning: Boolean = false,

    val isGameOver: Boolean = false,
    val pendingAttack: Int = 0,
    val testLinesToClear: Int = 1,

    val roomCode: String = "",

    val opponentConnected: Boolean = false,

    val connectionStatus: String = "Desconectado",

    val winner: String = ""


) {

    // Convierte la pieza actual en celdas reales.
    val currentPieceCells: List<Pair<Int, Int>>
        get() = currentPiece.cells()
}