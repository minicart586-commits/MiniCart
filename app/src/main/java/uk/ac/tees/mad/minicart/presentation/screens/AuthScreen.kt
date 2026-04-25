package uk.ac.tees.mad.minicart.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.tees.mad.minicart.ViewModel.AppViewModel
import uk.ac.tees.mad.minicart.model.UserData
import uk.ac.tees.mad.minicart.ui.theme.PrimaryTeal

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

@OptIn(ExperimentalMaterial3Api::class)
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

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Top Teal background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
                .background(
                    color = PrimaryTeal,
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isLogin) "Sign In" else "Sign Up",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        ),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, "Email") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = PrimaryTeal,
                            cursorColor = PrimaryTeal
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, "Password") },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = "Toggle Visibility"
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = PrimaryTeal,
                            cursorColor = PrimaryTeal
                        ),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 12.dp).align(Alignment.Start)
                        )
                    }

                    Button(
                        onClick = { onSubmit(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text(if (isLogin) "Login" else "Register", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    TextButton(
                        onClick = onToggleMode,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(
                            text = if (isLogin) "Register" else "Back to Login",
                            color = PrimaryTeal,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthPreview() {
    uk.ac.tees.mad.minicart.ui.theme.MiniCartTheme {
        AuthScreenContent(
            isLogin = true,
            onToggleMode = {},
            isLoading = false,
            errorMessage = "Invalid credentials",
            onSubmit = {_, _ ->}
        )
    }
}
