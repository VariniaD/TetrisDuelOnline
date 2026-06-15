package com.example.tetrisduelonline.game

data class FallingPiece(
    val type: Tetromino,
    val x: Int,
    val y: Int,
    val rotation: Int = 0
) {

      fun cells(): List<Pair<Int, Int>> {
        return TetrominoShapes.getShape(type, rotation).map { cell ->
            Pair(
                x + cell.first,
                y + cell.second
            )
        }
    }

    fun move(dx: Int, dy: Int): FallingPiece {
        return copy(
            x = x + dx,
            y = y + dy
        )
    }

    fun rotate(): FallingPiece {
        return copy(
            rotation = (rotation + 1) % 4
        )
    }

    companion object {

            fun random(): FallingPiece {
            val randomType = Tetromino.values().random()

            return FallingPiece(
                type = randomType,
                x = 3,
                y = 0,
                rotation = 0
            )
        }
    }
}

object TetrominoShapes {

    fun getShape(
        type: Tetromino,
        rotation: Int
    ): List<Pair<Int, Int>> {

        val baseShape = when (type) {

            // Pieza I:
            Tetromino.I -> listOf(
                Pair(0, 1),
                Pair(1, 1),
                Pair(2, 1),
                Pair(3, 1)
            )

            // Pieza O:
            Tetromino.O -> listOf(
                Pair(0, 0),
                Pair(1, 0),
                Pair(0, 1),
                Pair(1, 1)
            )

            // Pieza T:
            Tetromino.T -> listOf(
                Pair(1, 0),
                Pair(0, 1),
                Pair(1, 1),
                Pair(2, 1)
            )

            // Pieza S:
            Tetromino.S -> listOf(
                Pair(1, 0),
                Pair(2, 0),
                Pair(0, 1),
                Pair(1, 1)
            )

            // Pieza Z:
            Tetromino.Z -> listOf(
                Pair(0, 0),
                Pair(1, 0),
                Pair(1, 1),
                Pair(2, 1)
            )

            // Pieza J:
            Tetromino.J -> listOf(
                Pair(0, 0),
                Pair(0, 1),
                Pair(1, 1),
                Pair(2, 1)
            )

            // Pieza L:
            Tetromino.L -> listOf(
                Pair(2, 0),
                Pair(0, 1),
                Pair(1, 1),
                Pair(2, 1)
            )
        }

        if (type == Tetromino.O) {
            return baseShape
        }

        var rotatedShape = baseShape

        repeat(rotation % 4) {
            rotatedShape = rotateShape(rotatedShape)
        }

        return normalizeShape(rotatedShape)
    }

    private fun rotateShape(
        shape: List<Pair<Int, Int>>
    ): List<Pair<Int, Int>> {
        return shape.map { cell ->
            val x = cell.first
            val y = cell.second

            Pair(
                y,
                -x
            )
        }
    }

    private fun normalizeShape(
        shape: List<Pair<Int, Int>>
    ): List<Pair<Int, Int>> {

        val minX = shape.minOf { it.first }
        val minY = shape.minOf { it.second }

        return shape.map { cell ->
            Pair(
                cell.first - minX,
                cell.second - minY
            )
        }
    }
}