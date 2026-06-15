package com.example.tetrisduelonline.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp



@Composable
fun TetrisBoard(
    board: List<List<Int>>,
    currentPiece: List<Pair<Int, Int>>,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(10f / 20f)
            .border(2.dp, Color.Black)
    ) {
        val rows = 20
        val columns = 10

        val cellWidth = size.width / columns
        val cellHeight = size.height / rows

        // fondo del tablero.
        drawRect(
            color = Color(0xFFF5F5F5),
            size = size
        )

        //bloque fijos
        board.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, cell ->

                if (cell != 0) {
                    val color = when (cell) {
                        1 -> Color(0xFF1565C0)
                        2 -> Color(0xFF757575)
                        else -> Color.Black
                    }

                    drawRect(
                        color = color,
                        topLeft = Offset(
                            x = columnIndex * cellWidth,
                            y = rowIndex * cellHeight
                        ),
                        size = androidx.compose.ui.geometry.Size(
                            width = cellWidth,
                            height = cellHeight
                        )
                    )
                }
            }
        }

        // pieza actual.
        currentPiece.forEach { cell ->
            val x = cell.first
            val y = cell.second

            if (y >= 0) {
                drawRect(
                    color = Color(0xFFD32F2F),
                    topLeft = Offset(
                        x = x * cellWidth,
                        y = y * cellHeight
                    ),
                    size = Size(
                        width = cellWidth,
                        height = cellHeight
                    )
                )
            }
        }


        for (column in 0..columns) {
            drawLine(
                color = Color(0xFF9E9E9E),
                start = Offset(column * cellWidth, 0f),
                end = Offset(column * cellWidth, size.height),
                strokeWidth = 2f
            )
        }


        for (row in 0..rows) {
            drawLine(
                color = Color(0xFF9E9E9E),
                start = Offset(0f, row * cellHeight),
                end = Offset(size.width, row * cellHeight),
                strokeWidth = 2f
            )
        }

        drawRect(
            color = Color.Black,
            style = Stroke(width = 4f),
            size = size
        )
    }
}