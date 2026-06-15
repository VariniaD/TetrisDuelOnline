package com.example.tetrisduelonline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tetrisduelonline.ui.NavScreens
import com.example.tetrisduelonline.ui.screens.GameScreen
import com.example.tetrisduelonline.ui.screens.HomeScreen
import com.example.tetrisduelonline.ui.screens.ResultScreen
import com.example.tetrisduelonline.ui.theme.TetrisDuelOnlineTheme
import com.example.tetrisduelonline.ui.viewmodels.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            TetrisDuelOnlineTheme {

                val navController = rememberNavController()
                val vm: GameViewModel = viewModel()

                val state by vm.state.collectAsState()

                LaunchedEffect(state.opponentConnected, state.isRunning) {
                    if (state.opponentConnected && state.isRunning) {
                        navController.navigate(NavScreens.GAME.name) {
                            popUpTo(NavScreens.HOME.name) {
                                inclusive = false
                            }
                        }
                    }
                }

                LaunchedEffect(state.isGameOver, state.winner) {
                    if (state.isGameOver && state.winner.isNotEmpty()) {
                        navController.navigate(NavScreens.RESULT.name) {
                            popUpTo(NavScreens.GAME.name) {
                                inclusive = true
                            }
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = NavScreens.HOME.name
                ) {

                    composable(
                        route = NavScreens.HOME.name
                    ) {
                        HomeScreen(
                            state = state,

                            onCreateRoom = {

                                vm.createRoom()
                            },

                            onJoinRoom = { roomCode ->

                                vm.joinRoom(roomCode)
                            }
                        )
                    }

                    composable(
                        route = NavScreens.GAME.name
                    ) {
                        GameScreen(
                            vm = vm,
                            onBackToHome = {
                                vm.resetGame()

                                navController.navigate(NavScreens.HOME.name) {
                                    popUpTo(NavScreens.HOME.name) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    composable(
                        route = NavScreens.RESULT.name
                    ) {
                        ResultScreen(
                            winner = state.winner,
                            score = state.score,
                            lines = state.lines,
                            duration = state.durationSeconds,

                            onExit = {
                                vm.resetGame()

                                navController.navigate(NavScreens.HOME.name) {
                                    popUpTo(NavScreens.HOME.name) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}