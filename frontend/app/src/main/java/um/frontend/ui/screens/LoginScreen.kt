package um.frontend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import um.frontend.ui.viewmodel.AuthState
import um.frontend.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authVM: AuthViewModel,
    onLoggedIn: (String?) -> Unit,
    onSignupClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by authVM.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.LoggedIn) {
            val token = (authState as AuthState.LoggedIn).token
            onLoggedIn(token)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Iniciar sesión", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Usuario") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { authVM.login(username, password) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Entrar") }

                    if (authState is AuthState.Error) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            (authState as AuthState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            TextButton(onClick = onSignupClick) {
                Text("Crear cuenta")
            }
        }
    }
}