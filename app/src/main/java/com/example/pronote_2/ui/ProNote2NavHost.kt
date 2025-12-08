package com.example.pronote_2.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.pronote_2.ui.screen.MainScreen
import kotlinx.serialization.Serializable
import androidx.navigation.compose.composable
import com.example.pronote_2.ui.screen.AddingGradeScreen
import com.example.pronote_2.ui.screen.EditingGradeScreen
import com.example.pronote_2.ui.screen.ParametersScreen

@Serializable
object Main

@Serializable
object AddingGrade

@Serializable
object EditingGrade

@Serializable
object Parameters

@Composable
fun ProNote2NavHost(
    navController: NavHostController
) {
    NavHost(navController, startDestination = Main) {
        composable<Main> {
            MainScreen(

            )
        }

        composable<EditingGrade> {
            EditingGradeScreen(

            )
        }

        composable<AddingGrade> {
            AddingGradeScreen(
                
            )
        }

        composable<Parameters> {
            ParametersScreen(

            )
        }
    }
}
