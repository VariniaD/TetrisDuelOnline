package com.example.tetrisduelonline.game

import kotlin.random.Random

object TetrisEngine {

    const val BOARD_WIDTH = 10
    const val BOARD_HEIGHT = 20

    fun emptyBoard(): List<List<Int>> {
        return List(BOARD_HEIGHT) {
            List(BOARD_WIDTH) { 0 }
        }
    }

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

    fun calculateAttack(linesCleared: Int): Int {
        return when (linesCleared) {
            2 -> 1
            3 -> 2
            4 -> 4
            else -> 0
        }
    }


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


data class ClearLinesResult(
    val board: List<List<Int>>,
    val linesCleared: Int,
    val scoreEarned: Int
)

data class GarbageResult(
    val board: List<List<Int>>,
    val isGameOver: Boolean
)