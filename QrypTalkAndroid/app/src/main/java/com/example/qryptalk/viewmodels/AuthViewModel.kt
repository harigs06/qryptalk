package com.example.qryptalk.viewmodels



import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qryptalk.auth.AuthRepository
import com.example.qryptalk.auth.SupabaseClientProvider
import com.example.qryptalk.models.SignUpData
import com.example.qryptalk.models.User
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository(SupabaseClientProvider.client)

    fun signup(user: SignUpData, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.signUpUser(user)
            onResult(success)
        }
    }



    fun login(username: String, password: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val loginData: SignUpData? = repository.loginUser(username, password) // make this return SignUpData? instead of Boolean
            if (loginData != null) {
                val user = User(
                    id = loginData.id,
                    name = "${loginData.firstName} ${loginData.lastName}",
                    email = loginData.email,
                    profilePicUrl = 0 // default drawable for now
                )
                onResult(user)
            } else {
                onResult(null)
            }
        }
    }



}



