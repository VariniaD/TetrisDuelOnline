package com.example.tetrisduelonline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.tetrisduelonline.ui.screens.GameScreen
import com.example.tetrisduelonline.ui.theme.TetrisDuelOnlineTheme
import androidx.compose.foundation.layout.padding
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tetrisduelonline.ui.NavScreens
import com.example.tetrisduelonline.ui.screens.HomeScreen
import com.example.tetrisduelonline.ui.screens.ResultScreen
import com.example.tetrisduelonline.ui.viewmodels.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            TetrisDuelOnlineTheme {

                val navController = rememberNavController()
                val vm: GameViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = NavScreens.HOME.name
                ) {

                    composable(
                        route = NavScreens.HOME.name
                    ) {

                        HomeScreen(

                            onCreateRoom = {

                                vm.createRoom()
                                navController.navigate(
                                    NavScreens.GAME.name
                                )

                            },

                            onJoinRoom = { roomCode ->

                                vm.joinRoom(roomCode)
                                navController.navigate(
                                    NavScreens.GAME.name
                                )

                            }
                        )
                    }

                    composable(
                        route = NavScreens.GAME.name
                    ) {

                        GameScreen(
                            vm = vm,
                            onBackToHome = {

                                navController.popBackStack(
                                    NavScreens.HOME.name,
                                    false
                                )
                            }
                        )
                    }

                    composable(
                        route = NavScreens.RESULT.name
                    ) {

                        ResultScreen(
                            winner = "Jugador 1",
                            score = 1000,
                            lines = 25,
                            duration = 120,

                            onExit = {

                                navController.popBackStack(
                                    NavScreens.HOME.name,
                                    false
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}