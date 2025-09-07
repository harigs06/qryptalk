package com.example.qryptalk.navigation

import android.content.Context
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.qryptalk.screens.SessionScreen


import com.example.qryptalk.screens.*


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import com.example.qryptalk.data.AppDatabase
import com.example.qryptalk.data.UserEntity
import com.example.qryptalk.network.ChatWebSocketManager
import com.example.qryptalk.repositories.UserRepository
import com.example.qryptalk.viewmodels.AuthViewModel
import com.example.qryptalk.viewmodels.SessionViewModel
import com.example.qryptalk.viewmodels.UserListViewModel
import io.github.jan.supabase.SupabaseClient


@Composable
fun AppNavGraph(
    navController: NavHostController,
    context: Context,
    appDatabase: AppDatabase,
    supabaseClient: SupabaseClient,
    wsManager: ChatWebSocketManager,
    startDestination: String
) {

    val authViewModel = AuthViewModel()
    NavHost(navController = navController, startDestination = startDestination) {

        // Login screen
        composable(Screen.Login.route) {
            LoginScreen(
               navController = navController,
                authViewModel = authViewModel,
                context = context
            )
        }

        // Signup screen
        composable(Screen.Signup.route) {
            SignupScreen(
                onSignupSuccess = { userId ->
                    // after signup, navigate to user list
                    navController.navigate(Screen.UserList.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        // User list screen
        composable(Screen.UserList.route) {
            val userRepo = remember { UserRepository(appDatabase.userDao(), supabaseClient) }
            val userListVm: UserListViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return UserListViewModel(userRepo) as T
                }
            })

            val users by userListVm.userList.collectAsState()

            UserListScreen(
                onUserClick = { user ->
                    navController.navigate(Screen.Chat.createRoute(user.id))
                }
            )
        }

        // Chat / Session screen
        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getString("chatId") ?: return@composable

            val currentUser = remember { UserEntity(id = "current_user_id", username = "Me", email = "me@example.com") }

            val contact = remember(contactId) { UserEntity(id = contactId, username = "User $contactId", email = "") }

//            val wsManager = remember { ChatWebSocketManager(wsBaseUrl) }

            val factory = remember(currentUser.id, contact.id) {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return SessionViewModel(currentUser.id, contact.id, wsManager) as T
                    }
                }
            }

            val sessionVm: SessionViewModel = viewModel(factory = factory)

            val messages by sessionVm.messages.collectAsState()

            SessionScreen(
                currentUser = currentUser,
                contact = contact,
                viewModel = sessionVm,
                onBack = { navController.popBackStack() }
            )
        }
    }
}



