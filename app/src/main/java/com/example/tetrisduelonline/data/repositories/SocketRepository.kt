package com.example.tetrisduelonline.data.repositories

import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import android.util.Log

class SocketRepository {

    private val socket: Socket =
        IO.socket("http://192.168.1.120:3000")

    fun connect() {

        socket.on(Socket.EVENT_CONNECT) {
            Log.d("SOCKET", "CONECTADO")
        }

        socket.on(Socket.EVENT_CONNECT_ERROR) {
            Log.d("SOCKET", "ERROR")
        }

        socket.connect()
    }

    fun disconnect() {
        socket.disconnect()
    }

    fun createRoom() {
        Log.d("SOCKET", "ENVIANDO CREATE_ROOM")
        socket.emit("create_room")
    }

    fun joinRoom(roomId: String) {

        val data = JSONObject()

        data.put(
            "roomId",
            roomId
        )

        socket.emit(
            "join_room",
            data
        )
    }

    fun sendAttack(
        roomId: String,
        garbageLines: Int
    ) {

        val data = JSONObject()

        data.put("roomId", roomId)
        data.put("garbageLines", garbageLines)

        Log.d(
            "SOCKET_ATTACK",
            "Emitiendo send_attack: roomId=$roomId, garbageLines=$garbageLines"
        )

        socket.emit(
            "send_attack",
            data
        )
    }

    fun gameOver(roomId: String) {

        val data = JSONObject()

        data.put(
            "roomId",
            roomId
        )

        socket.emit(
            "game_over",
            data
        )
    }

    fun onRoomCreated(
        callback: (String) -> Unit
    ) {

        socket.on("room_created") {

            Log.d("SOCKET", "ROOM CREATED RECIBIDO")

            val data = it[0] as JSONObject

            callback(
                data.getString("roomId")
            )
        }
    }

    fun onGameStart(
        callback: () -> Unit
    ) {

        socket.on("game_start") {

            callback()
        }
    }

    fun onReceiveAttack(
        callback: (Int) -> Unit
    ) {

        socket.on("receive_attack") {

            val data = it[0] as JSONObject

            callback(
                data.getInt("garbageLines")
            )
        }
    }

    fun onVictory(
        callback: () -> Unit
    ) {

        socket.on("victory") {

            android.util.Log.d(
                "SOCKET",
                "VICTORY RECIBIDO"
            )

            callback()
        }
    }

    fun onOpponentDisconnected(
        callback: () -> Unit
    ) {

        socket.on("opponent_disconnected") {

            callback()
        }
    }
}