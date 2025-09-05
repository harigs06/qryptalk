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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.qryptalk.models.User
import com.example.qryptalk.screens.LoginScreen
import com.example.qryptalk.screens.SessionScreen
import com.example.qryptalk.screens.UserList
import com.example.qryptalk.ui.theme.QrypTalkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QrypTalkTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                  Box(
                      modifier = Modifier
                          .padding(innerPadding)
                  ){
//                      SessionScreen(
//                          User(
//                              "11",
//                              "hari",
//                              "dfjadkfjasdlkj",
//                          ),
//                          "rt"
//                      )


                      UserList()
                  }
                }
            }
        }
    }
}

