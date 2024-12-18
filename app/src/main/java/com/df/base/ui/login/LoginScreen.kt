package com.df.base.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.df.base.R
import com.df.base.ui.AppViewModelProvider
import com.df.base.ui.ShowWarningAlert
import com.df.base.ui.navigation.NavigationDestination

object LoginDestination: NavigationDestination {
    override  val route = "login"
    override  val titleRes = R.string.login
    override  val icon = null
}

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignupMode by remember { mutableStateOf(false) }

    val loginState by loginViewModel.loginState.collectAsState()
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            isError = username.isEmpty() && loginState is LoginState.Error
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = password.isEmpty() && loginState is LoginState.Error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isSignupMode) {
                    loginViewModel.signup(username, password)
                } else {
                    loginViewModel.login(username, password)
                }
            },
            enabled = username.isNotEmpty() && password.isNotEmpty() && loginState != LoginState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loginState == LoginState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(if (isSignupMode) "Sign Up" else "Login")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { isSignupMode = !isSignupMode }) {
            Text(
                text = if (isSignupMode) "Already have an account? Login" else "Don't have an account? Sign Up",
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (loginState is LoginState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (loginState as LoginState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

