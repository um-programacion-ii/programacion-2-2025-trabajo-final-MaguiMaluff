package um.frontend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import um.frontend.ui.viewmodel.AuthViewModel

@Composable
fun SignupScreen(
    authVM: AuthViewModel,
    onSignedUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("Crear cuenta", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = username, onValueChange = { username = it },
                    label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = userId, onValueChange = { userId = it },
                    label = { Text("Usuario") },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    label = { Text("Contraseña") }, singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        status = null
                        loading = true
                        scope.launch {
                            authVM.signupAwait(username, password, userId)
                                .onSuccess { resp ->
                                    loading = false
                                    if (resp.created == true) {
                                        status = resp.message ?: "Cuenta creada. Ahora puedes iniciar sesión."
                                        onSignedUp()
                                    } else {
                                        status = resp.message ?: "No se pudo crear la cuenta."
                                    }
                                }
                                .onFailure { e ->
                                    loading = false
                                    status = e.message ?: "Error creando cuenta (¿backend accesible?)"
                                }
                        }
                    },
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth()
                ) { Text(if (loading) "Creando..." else "Crear") }

                status?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}