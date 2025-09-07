package com.example.qryptalk.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.qryptalk.R
import com.example.qryptalk.data.UserEntity
import com.example.qryptalk.network.UserListViewModelFactory
import com.example.qryptalk.repositories.UserRepository
import com.example.qryptalk.viewmodels.UserListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun UserListScreen(
    viewModel: UserListViewModel = viewModel(),
    onUserClick: (UserEntity) -> Unit
) {
    val userList by viewModel.userList.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var context = LocalContext.current
    var coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(top = 18.dp)
    ) {

        Row(
            modifier = Modifier
                .padding(6.dp)
                .padding(start = 18.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = "QrypTalk",
                style = MaterialTheme.typography.headlineLarge.copy(color = Color.Green, fontWeight = FontWeight.Bold)
            )

            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription =  "Logout",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.
                    clickable {
                        coroutineScope.launch {
                            viewModel.logout(context)
                        }
                    }.size(30.dp)
            )



        }
        // 🔍 Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.searchUsers(it)
            },
            label = { Text("Search users") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        // 📌 Search Results
        if (searchQuery.isNotEmpty()) {
            LazyColumn {
                items(searchResults) { user ->
                    UserCard(
                        user = user,
                        showCancel = false,
                        onClick = { viewModel.addUser(user) }
                    )
                }
            }
        }

        Divider()

        // 👥 Friend List (from Room)
        Text(
            "Your Friends",
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.titleMedium
        )
        LazyColumn {
            items(userList) { user ->
                UserCard(
                    user = user,
                    showCancel = true,
                    onClick = { onUserClick(user) },
                    onCancel = { viewModel.deleteUser(user) }
                )
            }
        }
    }
}

@Composable
fun UserCard(
    user: UserEntity,
    showCancel: Boolean,
    onClick: () -> Unit,
    onCancel: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_person_24), // same profile pic
            contentDescription = "Profile",
            modifier = Modifier.size(48.dp).clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(user.username, style = MaterialTheme.typography.bodyLarge)
            Text(user.email, style = MaterialTheme.typography.bodySmall)
        }
        if (showCancel && onCancel != null) {
            IconButton(onClick = { onCancel() }) {
                Icon(Icons.Default.Close, contentDescription = "Remove")
            }
        }
    }
}




