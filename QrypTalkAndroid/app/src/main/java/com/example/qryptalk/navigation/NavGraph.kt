package com.example.qryptalk.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.qryptalk.screens.LoginScreen
import com.example.qryptalk.screens.SessionScreen
import com.example.qryptalk.screens.SignupScreen


import com.example.qryptalk.screens.*
import com.example.qryptalk.viewmodels.*


import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import com.example.qryptalk.models.User
import com.example.qryptalk.network.ChatViewModelFactory
import com.example.qryptalk.network.UserListViewModelFactory
import com.example.qryptalk.repositories.UserRepository
import com.example.qryptalk.viewmodels.SessionViewModel
import com.example.qryptalk.viewmodels.UserListViewModel

@Composable
fun AppNavGraph(navController: NavHostController) {
    val userRepository = UserRepository()

    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController, context = LocalContext.current) }
        composable("signup") { SignupScreen(
            navController = navController,
            onSignupSuccess = {
                navController.navigate(Screen.Login.route)
            }
        ) }
        composable(
            route = "userList/{currentUserId}",
            arguments = listOf(navArgument("currentUserId") { type = NavType.StringType })
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getString("currentUserId") ?: ""
            UserListScreen(navController = navController, currentUserId = currentUserId)
        }
        composable(
            route = "chat/{contactId}/{currentUserId}",
            arguments = listOf(
                navArgument("contactId") { type = NavType.StringType },
                navArgument("currentUserId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getString("contactId") ?: ""
            val currentUserId = backStackEntry.arguments?.getString("currentUserId") ?: ""
            val contact = User(id = contactId, name = "User $contactId", email = "")
            val chatViewModel: SessionViewModel = viewModel(factory = ChatViewModelFactory(currentUserId))
            SessionScreen(contact = contact, currentUserId = currentUserId, viewModel = chatViewModel)
        }
    }

}


