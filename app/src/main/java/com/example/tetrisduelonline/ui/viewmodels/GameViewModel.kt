package com.example.tetrisduelonline.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tetrisduelonline.game.FallingPiece
import com.example.tetrisduelonline.ui.states.GameState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import com.example.tetrisduelonline.game.TetrisEngine
import com.example.tetrisduelonline.data.repositories.SocketRepository

class GameViewModel : ViewModel() {


    private val socketRepository = SocketRepository()

    init {

        android.util.Log.d("TETRIS", "VIEWMODEL INICIADO")

        socketRepository.connect()

        socketRepository.onRoomCreated { roomId ->

            android.util.Log.d(
                "SOCKET",
                "ROOM ID = $roomId"
            )

            _state.value = _state.value.copy(
                roomCode = roomId,
                connectionStatus = "Sala creada",
                message = "Sala creada. Esperando al segundo jugador...."
            )
        }
        socketRepository.onGameStart {

            autoDropJob?.cancel()
            timerJob?.cancel()

            _state.value = _state.value.copy(
                opponentConnected = true,
                isRunning = false,
                connectionStatus = "Partida iniciada",
                message = "Ambos jugadores conectados. Iniciamos partida..."

            )

            startGame()
        }
        socketRepository.onReceiveAttack { lines ->

            android.util.Log.d(
                "SOCKET_ATTACK",
                "Ataque recibido desde socket: $lines línea(s)"
            )

            receiveGarbage(lines)
        }

        socketRepository.onVictory {

            viewModelScope.launch {

                autoDropJob?.cancel()
                autoDropJob = null

                timerJob?.cancel()
                timerJob = null

                _state.value = _state.value.copy(
                    isRunning = false,
                    isGameOver = true,
                    winner = "Victoria",
                    message = "Ganaste"
                )
            }
        }

        socketRepository.onOpponentDisconnected {

            viewModelScope.launch {

                autoDropJob?.cancel()
                timerJob?.cancel()

                _state.value = _state.value.copy(
                    isRunning = false,
                    isGameOver = true,
                    winner = "Victoria",
                    message = "El rival abandonó la partida"
                )
            }
        }
    }

    companion object {
        const val DROP_DELAY = 700L
    }

    private val _state = MutableStateFlow(
        GameState()
    )
     val state: StateFlow<GameState> = _state.asStateFlow()

    private var autoDropJob: Job? = null

    private var timerJob: Job? = null


    // Inicia la partida local.
    fun startGame() {
        val currentState = _state.value

        if (currentState.isGameOver) {
            return
        }

        if (currentState.isRunning) {
            return
        }

        _state.value = currentState.copy(
            isRunning = true,
            message = "Partida iniciada"
        )

        startAutoDrop()
        startTimer()
    }

    // Pausa la partida.
    fun pauseGame() {
        autoDropJob?.cancel()
        autoDropJob = null

        timerJob?.cancel()
        timerJob = null

        _state.value = _state.value.copy(
            isRunning = false,
            message = "Partida pausada"
        )
    }

    fun resetGame() {
        autoDropJob?.cancel()
        autoDropJob = null

        timerJob?.cancel()
        timerJob = null

        _state.value = GameState(
            connectionStatus = "Conectado",
            message = "Listo para crear o unirse a una sala"
        )
    }


    private fun startAutoDrop() {
        autoDropJob?.cancel()

        autoDropJob = viewModelScope.launch {
            while (_state.value.isRunning && !_state.value.isGameOver) {

                delay(DROP_DELAY)

                softDrop()

                if (_state.value.isGameOver) {
                    autoDropJob?.cancel()
                    autoDropJob = null
                }
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            while (_state.value.isRunning && !_state.value.isGameOver) {

                delay(1000L)

                val currentState = _state.value
                val newDuration = currentState.durationSeconds + 1

                _state.value = currentState.copy(
                    durationSeconds = newDuration,
                    show37Message = newDuration >= 37,
                    message = if (newDuration == 37) {
                        "Modo 37 activado"
                    } else {
                        currentState.message
                    }
                )
            }
        }
    }


    fun moveLeft() {
        val currentState = _state.value

        if (currentState.isGameOver) return

        val newPiece = currentState.currentPiece.move(
            dx = -1,
            dy = 0
        )

        if (TetrisEngine.isValidPosition(newPiece, currentState.board)) {
            _state.value = currentState.copy(
                currentPiece = newPiece,
                message = "Movimiento a la izquierda"
            )
        }
    }

    fun moveRight() {
        val currentState = _state.value

        if (currentState.isGameOver) return

        val newPiece = currentState.currentPiece.move(
            dx = 1,
            dy = 0
        )

        if (TetrisEngine.isValidPosition(newPiece, currentState.board)) {
            _state.value = currentState.copy(
                currentPiece = newPiece,
                message = "Movimiento a la derecha"
            )
        }
    }

    fun softDrop() {
        val currentState = _state.value

        if (currentState.isGameOver) return

        val newPiece = currentState.currentPiece.move(
            dx = 0,
            dy = 1
        )

        if (TetrisEngine.isValidPosition(newPiece, currentState.board)) {
            _state.value = currentState.copy(
                currentPiece = newPiece,
                message = "Bajando pieza"
            )
        } else {
            fixPieceToBoard()
        }
    }

    fun hardDrop() {
        val currentState = _state.value

        if (currentState.isGameOver) return

        var piece = currentState.currentPiece

        while (true) {
            val nextPiece = piece.move(
                dx = 0,
                dy = 1
            )
            if (TetrisEngine.isValidPosition(nextPiece, currentState.board)) {
                piece = nextPiece
            } else {
                break
            }
        }

        _state.value = currentState.copy(
            currentPiece = piece,
            message = "Caída instantánea"
        )

        fixPieceToBoard()
    }


    fun rotate() {
        val currentState = _state.value

        if (currentState.isGameOver) return

        val rotatedPiece = currentState.currentPiece.rotate()

        // Validamos la rotación usando TetrisEngine.
        // Si la pieza rotada choca con pared o bloques, no se aplica.
        if (TetrisEngine.isValidPosition(rotatedPiece, currentState.board)) {
            _state.value = currentState.copy(
                currentPiece = rotatedPiece,
                message = "Pieza rotada"
            )
        } else {
            _state.value = currentState.copy(
                message = "No se puede rotar aquí"
            )
        }
    }
    fun testGarbageLine() {
        receiveGarbage(1)
    }

    fun receiveGarbage(lines: Int) {
        val currentState = _state.value

        if (currentState.isGameOver) return

        if (lines <= 0) return

        val result = TetrisEngine.addGarbageLines(
            board = currentState.board,
            amount = lines
        )

        val currentPieceStillValid = TetrisEngine.isValidPosition(
            piece = currentState.currentPiece,
            board = result.board
        )

        if (result.isGameOver || !currentPieceStillValid) {
            autoDropJob?.cancel()
            autoDropJob = null

            timerJob?.cancel()
            timerJob = null

            if (currentState.roomCode.isNotEmpty()) {

                socketRepository.gameOver(
                    currentState.roomCode
                )
            }

            _state.value = currentState.copy(
                board = result.board,
                isRunning = false,
                isGameOver = true,
                winner = "Perdiste",
                message = "Fin de partida"
            )

            return
        }

        _state.value = currentState.copy(
            board = result.board,
            message = "Ataque recibido: $lines línea(s) basura"
        )
    }


    fun testClearLines() {
        val currentState = _state.value

        if (currentState.isGameOver) return

        val mutableBoard = currentState.board.map { row ->
            row.toMutableList()
        }.toMutableList()


        val testLines = currentState.testLinesToClear

        for (i in 0 until testLines) {
            val rowIndex = TetrisEngine.BOARD_HEIGHT - 1 - i

            mutableBoard[rowIndex] = MutableList(TetrisEngine.BOARD_WIDTH) { 1 }
        }


        val cleanResult = TetrisEngine.clearCompletedLines(
            board = mutableBoard.map { row -> row.toList() }
        )

        val attack = TetrisEngine.calculateAttack(
            linesCleared = cleanResult.linesCleared
        )

        sendAttackIfNeeded(attack)

        val nextTestLines = if (testLines >= 4) {
            1
        } else {
            testLines + 1
        }

        _state.value = currentState.copy(
            board = cleanResult.board,
            score = currentState.score + cleanResult.scoreEarned,
            lines = currentState.lines + cleanResult.linesCleared,
            lastAttack = attack,
            pendingAttack = currentState.pendingAttack + attack,
            testLinesToClear = nextTestLines,
            message = "Prueba: ${cleanResult.linesCleared} línea(s), ataque $attack"
        )
    }

    private fun sendAttackIfNeeded(attack: Int) {
        val currentState = _state.value

        if (attack <= 0) {
            android.util.Log.d(
                "SOCKET_ATTACK",
                "No se envía ataque porque attack = $attack"
            )
            return
        }

        if (currentState.roomCode.isBlank()) {
            android.util.Log.d(
                "SOCKET_ATTACK",
                "No se envía ataque porque no hay código de sala"
            )
            return
        }

        android.util.Log.d(
            "SOCKET_ATTACK",
            "Enviando ataque de $attack línea(s) a la sala ${currentState.roomCode}"
        )

        socketRepository.sendAttack(
            roomId = currentState.roomCode,
            garbageLines = attack
        )
    }

    private fun fixPieceToBoard() {
        val currentState = _state.value

        val boardWithPiece = TetrisEngine.placePieceOnBoard(
            board = currentState.board,
            piece = currentState.currentPiece
        )

        val cleanResult = TetrisEngine.clearCompletedLines(
            board = boardWithPiece
        )

        val newCurrentPiece = currentState.nextPiece.copy(
            x = 3,
            y = 0,
            rotation = 0
        )

        val newNextPiece = FallingPiece.random()

        val attack = TetrisEngine.calculateAttack(
            linesCleared = cleanResult.linesCleared
        )

        if (attack > 0 && currentState.roomCode.isNotEmpty()) {

            val attack = TetrisEngine.calculateAttack(
                linesCleared = cleanResult.linesCleared
            )

            sendAttackIfNeeded(attack)
        }

        if (!TetrisEngine.isValidPosition(newCurrentPiece, cleanResult.board)) {

            autoDropJob?.cancel()
            autoDropJob = null

            timerJob?.cancel()
            timerJob = null

            if (currentState.roomCode.isNotEmpty()) {

                socketRepository.gameOver(
                    currentState.roomCode
                )
            }

            _state.value = currentState.copy(
                board = cleanResult.board,
                score = currentState.score + cleanResult.scoreEarned,
                lines = currentState.lines + cleanResult.linesCleared,
                lastAttack = attack,
                pendingAttack = currentState.pendingAttack + attack,
                isRunning = false,
                isGameOver = true,
                winner = "Perdiste",
                message = "Fin de partida"
            )

            return
        }

        _state.value = currentState.copy(
            board = cleanResult.board,
            currentPiece = newCurrentPiece,
            nextPiece = newNextPiece,
            score = currentState.score + cleanResult.scoreEarned,
            lines = currentState.lines + cleanResult.linesCleared,
            lastAttack = attack,
            pendingAttack = currentState.pendingAttack + attack,
            message = "Nueva pieza: ${newCurrentPiece.type}"
        )
    }

    fun hasPendingAttack(): Boolean {
        return _state.value.pendingAttack > 0
    }

    fun getPendingAttack(): Int {
        return _state.value.pendingAttack
    }

    fun clearPendingAttack() {
        _state.value = _state.value.copy(
            pendingAttack = 0
        )
    }

    fun consumePendingAttack(): Int {
        val attack = _state.value.pendingAttack

        _state.value = _state.value.copy(
            pendingAttack = 0
        )

        return attack
    }

    fun connect() {
        socketRepository.connect()

        _state.value = _state.value.copy(
            connectionStatus = "Conectado"
        )
    }

    fun disconnect() {
        socketRepository.disconnect()

        _state.value = _state.value.copy(
            connectionStatus = "Desconectado"
        )
    }

    fun createRoom() {
        _state.value = _state.value.copy(
            roomCode = "",
            opponentConnected = false,
            connectionStatus = "Creando sala...",
            message = "Solicitando código de sala al servidor..."
        )

        socketRepository.createRoom()
    }

    fun joinRoom(
        roomCode: String
    ) {

        val cleanRoomCode = roomCode.trim().uppercase()

        if (cleanRoomCode.isBlank()) {
            _state.value = _state.value.copy(
                message = "Debe ingresar un código de sala"
            )
            return
        }

        _state.value = _state.value.copy(
            roomCode = cleanRoomCode,
            opponentConnected = false,
            connectionStatus = "Uniéndose a sala...",
            message = "Intentando unirse a la sala $cleanRoomCode"
        )

        socketRepository.joinRoom(cleanRoomCode)
    }

    override fun onCleared() {
        super.onCleared()
        autoDropJob?.cancel()
        timerJob?.cancel()
    }




}

