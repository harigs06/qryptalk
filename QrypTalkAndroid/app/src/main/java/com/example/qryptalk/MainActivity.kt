package com.example.qryptalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.qryptalk.auth.SupabaseClientProvider
import com.example.qryptalk.data.AppDatabase
import com.example.qryptalk.data.UserPreferences
import com.example.qryptalk.models.User
import com.example.qryptalk.navigation.AppNavGraph
import com.example.qryptalk.network.ChatWebSocketManager
import com.example.qryptalk.screens.LoginScreen
import com.example.qryptalk.screens.SessionScreen
import com.example.qryptalk.ui.theme.QrypTalkTheme
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.http.ContentDisposition.Companion.File
import java.io.File

//val supabase = createSupabaseClient(
//            supabaseUrl = "https://xyzcompany.supabase.co",
//            supabaseKey = "your_public_anon_key"
//        ) {
//            install(Postgrest)
//        }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_db"
        ).build()

        val supabaseClient = SupabaseClientProvider.client
        val userPrefs = UserPreferences(applicationContext)




//
        setContent {
            QrypTalkTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                  Box(
                      modifier = Modifier
                          .padding(innerPadding)
                  ){


                      val navController = rememberNavController()
                      val user by userPrefs.userFlow.collectAsState(initial = null)
                      val wsBaseUrl = "ws://10.199.184.28:8000/chat/ws/"
                      val currentUser = user?.name ?: "guest"
                      val wsManager = ChatWebSocketManager(wsBaseUrl)
                      wsManager.start(currentUser)


                      val startDestination = if (user == null) {
                          "login"
                      } else {
                          "user_list"
                      }

                      AppNavGraph(
                          navController = navController,
                          appDatabase = db,
                          supabaseClient = supabaseClient,
                          startDestination = startDestination,
                          context = LocalContext.current,
                          wsManager = wsManager
                      )
                  }
                  }
                }
            }
        }
    }

