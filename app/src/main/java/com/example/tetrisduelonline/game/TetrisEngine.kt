package com.example.tetrisduelonline.game

import kotlin.random.Random

// TetrisEngine contiene la lógica principal del juego.
//
// Esta clase NO dibuja la pantalla.
// Esta clase NO maneja botones.
// Esta clase NO usa Compose.
//
// Su responsabilidad es trabajar con reglas del Tetris:
// - validar posiciones;
// - colocar piezas en el tablero;
// - eliminar líneas;
// - calcular ataques;
// - agregar líneas basura fijas.
object TetrisEngine {

    const val BOARD_WIDTH = 10
    const val BOARD_HEIGHT = 20

    // Crea un tablero vacío de 20 filas x 10 columnas.
    //
    // Valor 0 = celda vacía.
    fun emptyBoard(): List<List<Int>> {
        return List(BOARD_HEIGHT) {
            List(BOARD_WIDTH) { 0 }
        }
    }

    // Valida si una pieza puede estar en una posición.
    //
    // Revisa:
    // - que no salga por izquierda;
    // - que no salga por derecha;
    // - que no pase del fondo;
    // - que no choque con bloques ocupados.
    fun isValidPosition(
        piece: FallingPiece,
        board: List<List<Int>>
    ): Boolean {
        for (cell in piece.cells()) {
            val x = cell.first
            val y = cell.second

            if (x < 0 || x >= BOARD_WIDTH) {
                return false
            }

            if (y >= BOARD_HEIGHT) {
                return false
            }

            if (y < 0) {
                continue
            }

            if (board[y][x] != 0) {
                return false
            }
        }

        return true
    }

    // Coloca la pieza actual dentro del tablero.
    //
    // Esto se usa cuando la pieza ya no puede seguir bajando.
    fun placePieceOnBoard(
        board: List<List<Int>>,
        piece: FallingPiece
    ): List<List<Int>> {

        val mutableBoard = board.map { row ->
            row.toMutableList()
        }.toMutableList()

        for (cell in piece.cells()) {
            val x = cell.first
            val y = cell.second

            if (y in 0 until BOARD_HEIGHT && x in 0 until BOARD_WIDTH) {
                mutableBoard[y][x] = 1
            }
        }

        return mutableBoard.map { row ->
            row.toList()
        }
    }

    // Elimina líneas completas del tablero.
    //
    // REGLA IMPORTANTE:
    // Solo se eliminan filas compuestas completamente por bloques normales.
    //
    // Valor 1 = bloque normal eliminable.
    // Valor 2 = bloque basura fijo NO eliminable.
    //
    // Por eso una fila con basura no se elimina,
    // aunque el jugador complete el hueco después.
    fun clearCompletedLines(
        board: List<List<Int>>
    ): ClearLinesResult {

        val remainingRows = board.filter { row ->

            val isNormalCompletedLine = row.all { cell ->
                cell == 1
            }

            !isNormalCompletedLine
        }

        val linesCleared = BOARD_HEIGHT - remainingRows.size

        val emptyRows = List(linesCleared) {
            List(BOARD_WIDTH) { 0 }
        }

        val newBoard = emptyRows + remainingRows

        val scoreEarned = when (linesCleared) {
            1 -> 100
            2 -> 300
            3 -> 500
            4 -> 800
            else -> 0
        }

        return ClearLinesResult(
            board = newBoard,
            linesCleared = linesCleared,
            scoreEarned = scoreEarned
        )
    }

    // Calcula el ataque según la tabla del práctico.
    //
    // 1 línea  -> 0 líneas basura
    // 2 líneas -> 1 línea basura
    // 3 líneas -> 2 líneas basura
    // 4 líneas -> 4 líneas basura
    fun calculateAttack(linesCleared: Int): Int {
        return when (linesCleared) {
            2 -> 1
            3 -> 2
            4 -> 4
            else -> 0
        }
    }

    // Agrega líneas basura desde la parte inferior.
    //
    // REGLA DEL ATAQUE:
    // La línea basura tiene una celda vacía aleatoria,
    // pero sus bloques son fijos y no eliminables.
    //
    // Valor 0 = vacío.
    // Valor 1 = bloque normal.
    // Valor 2 = basura fija.
    fun addGarbageLines(
        board: List<List<Int>>,
        amount: Int
    ): GarbageResult {

        var newBoard = board
        var gameOver = false

        repeat(amount) {

            val topRowHasBlocks = newBoard.first().any { cell ->
                cell != 0
            }

            if (topRowHasBlocks) {
                gameOver = true
            }

            val withoutTopRow = newBoard.drop(1)

            val garbageRow = MutableList(BOARD_WIDTH) { 2 }

            val emptyColumn = Random.nextInt(BOARD_WIDTH)
            garbageRow[emptyColumn] = 0

            newBoard = withoutTopRow + listOf(garbageRow)
        }

        return GarbageResult(
            board = newBoard,
            isGameOver = gameOver
        )
    }
}

// Resultado de limpiar líneas.
data class ClearLinesResult(
    val board: List<List<Int>>,
    val linesCleared: Int,
    val scoreEarned: Int
)

// Resultado de agregar líneas basura.
data class GarbageResult(
    val board: List<List<Int>>,
    val isGameOver: Boolean
)