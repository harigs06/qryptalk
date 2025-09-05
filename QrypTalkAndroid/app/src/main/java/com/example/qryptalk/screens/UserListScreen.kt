package com.example.qryptalk.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.qryptalk.models.User

@Composable
fun UserList(

){

    var searchQuery by remember {mutableStateOf("")}
    val users = listOf(
        User("","Rohit","rohit124@gmail.com"),
        User("","Hari","rohit124@gmail.com"),
        User("","Manideep","rohit124@gmail.com"),
        User("","Sandeep","rohit124@gmail.com"),
        User("","Badhri","rohit124@gmail.com"),
    )


    Column(

    ){
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            label = {
                Text(
                    text = "Search"
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Clear search",
                    modifier = Modifier.clickable { searchQuery = "" },
                    tint = Color.Blue
                )
            },
            modifier = Modifier
                .fillMaxWidth()
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(users) { user ->
                UserItem(user = user)
                Divider()
            }
        }
    }
}




@Composable
fun UserItem(user: User) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = user.profilePicUrl),
            contentDescription = "${user.name}'s avatar",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = user.name,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


//@Preview
//@Composable
//fun Preview(){
//    UserList()
//
//}





