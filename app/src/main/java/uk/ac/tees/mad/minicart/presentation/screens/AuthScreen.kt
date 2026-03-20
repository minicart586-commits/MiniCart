package uk.ac.tees.mad.minicart.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.ac.tees.mad.minicart.ViewModel.AppViewModel
import uk.ac.tees.mad.minicart.model.UserData



@Composable
fun AuthScreen(
    viewModel: AppViewModel? = null,
    initialIsLogin: Boolean = true,
    onLoginSuccess: () -> Unit = {}
) {
    var isLogin by remember { mutableStateOf(initialIsLogin) }

    val loginState = viewModel?.loginScreenState?.value
    val signupState = viewModel?.signupScreenState?.value

    LaunchedEffect(loginState?.success, signupState?.success) {
        if (loginState?.success == true || signupState?.success == true) {
            onLoginSuccess()
        }
    }

    AuthScreenContent(
        isLogin = isLogin,
        onToggleMode = { isLogin = !isLogin },
        isLoading = if (isLogin) loginState?.isLoading == true else signupState?.isLoading == true,
        errorMessage = if (isLogin) loginState?.error else signupState?.error,
        onSubmit = { email, password ->
            val userData = UserData(
                email = email,
                password = password
            )
            if (isLogin) {
                viewModel?.loginUser(userData)
            } else {
                viewModel?.registerUser(userData)
            }
        }
    )
}

@Composable
fun AuthScreenContent(
    isLogin: Boolean,
    onToggleMode: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onSubmit: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isLogin) "Welcome Back" else "Create Account",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Button(
            onClick = { onSubmit(email, password) },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(text = if (isLogin) "Login" else "Sign Up")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isLogin) "Don't have an account? Sign up" else "Already have an account? Login",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onToggleMode() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenLoginPreview() {
    MaterialTheme {
        AuthScreenContent(
            isLogin = true,
            onToggleMode = {},
            isLoading = false,
            errorMessage = null,
            onSubmit = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenRegisterPreview() {
    MaterialTheme {
        AuthScreenContent(
            isLogin = false,
            onToggleMode = {},
            isLoading = false,
            errorMessage = null,
            onSubmit = { _, _ -> }
        )
    }
}
