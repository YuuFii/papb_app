package com.example.papb_app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.papb_app.pages.HomePage
import com.example.papb_app.pages.LoginPage
import com.example.papb_app.pages.LoginScreen
import com.example.papb_app.pages.SignUpPage
import com.example.papb_app.pages.SignUpScreen

@Composable
fun Navigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login") {
//            LoginPage(modifier, navController, authViewModel)
            LoginScreen(modifier, navController, authViewModel)
        }
        composable("signup") {
//            SignUpPage(modifier, navController, authViewModel)
            SignUpScreen(modifier, navController, authViewModel)
        }
        composable("homepage") {
            HomePage(modifier, navController, authViewModel)
        }
    })
}
