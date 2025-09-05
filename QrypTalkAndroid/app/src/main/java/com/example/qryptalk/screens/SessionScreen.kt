package com.example.qryptalk.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.qryptalk.models.User
import com.example.qryptalk.viewmodels.SessionViewModel
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    user: User,
    currentUserId: String,
    viewModel: SessionViewModel = viewModel()
) {
    val messages = viewModel.messages
    val inputText = viewModel.inputText
    val isSecure = viewModel.isSecure

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = user.profilePicUrl,
                            contentDescription = "${user.name}'s profile",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = user.name)
                    }
                },
                actions = {
                    Icon(
                        imageVector = if (isSecure) Icons.Default.Lock else Icons.Default.Warning,
                        tint = if (isSecure) Color.Green else Color.Red,
                        contentDescription = if (isSecure) "Secure" else "Insecure",
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(messages) { msg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (msg.isFromMe) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            color = if (msg.isFromMe) Color(0xFFDCF8C6) else Color.LightGray,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(text = msg.content)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = viewModel.formatTimestamp(msg.timestamp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .imePadding()
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { viewModel.onInputChange(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { viewModel.sendMessage(currentUserId) }) {
                    Text("Send")
                }
            }
        }
    }
}

