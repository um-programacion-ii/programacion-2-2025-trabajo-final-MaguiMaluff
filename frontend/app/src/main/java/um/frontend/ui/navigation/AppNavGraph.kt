package um.frontend.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import um.frontend.ui.screens.*
import um.frontend.ui.viewmodel.AuthViewModel
import um.frontend.ui.viewmodel.EventsViewModel
import um.frontend.ui.viewmodel.SelectionViewModel

object Routes {
    const val Login = "login"
    const val Signup = "signup"
    const val Events = "events"
    const val EventDetail = "event/{id}"
    const val Seats = "seats/{id}"
    const val Names = "names/{id}"
    const val Checkout = "checkout/{id}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    authVM: AuthViewModel,
    onTokenUpdate: (String?) -> Unit,
    eventsVM: EventsViewModel,
    selectionVM: SelectionViewModel
) {
    NavHost(navController, startDestination = Routes.Login) {
        composable(Routes.Login) {
            Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("UM Eventos") }) }) { padding ->
                LoginScreen(
                    authVM = authVM,
                    onLoggedIn = { token ->
                        onTokenUpdate(token)
                        navController.navigate(Routes.Events) {
                            popUpTo(Routes.Login) { inclusive = true }
                        }
                    },
                    onSignupClick = { navController.navigate(Routes.Signup) },
                    modifier = Modifier.padding(padding)
                )
            }
        }
        composable(Routes.Signup) {
            Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Crear cuenta") }) }) { padding ->
                SignupScreen(
                    authVM = authVM,
                    onSignedUp = { navController.popBackStack() },
                    modifier = Modifier.padding(padding)
                )
            }
        }
        composable(Routes.Events) {
            Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Eventos") }) }) { padding ->
                EventListScreen(
                    eventsVM = eventsVM,
                    onSelectEvent = { id -> navController.navigate("event/$id") },
                    modifier = Modifier.padding(padding)
                )
            }
        }
        composable(Routes.EventDetail) { entry ->
            val id = entry.arguments?.getString("id")?.toLongOrNull() ?: return@composable
            Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Detalle del evento") }) }) { padding ->
                EventDetailScreen(
                    eventsVM = eventsVM,
                    eventId = id,
                    onStartSelection = { navController.navigate("seats/$id") },
                    modifier = Modifier.padding(padding)
                )
            }
        }
        composable(Routes.Seats) { entry ->
            val id = entry.arguments?.getString("id")?.toLongOrNull() ?: return@composable
            Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Seleccionar asientos") }) }) { padding ->
                SeatSelectionScreen(
                    eventId = id,
                    selectionVM = selectionVM,
                    eventsVM = eventsVM,
                    onNext = { navController.navigate("names/$id") },
                    modifier = Modifier.padding(padding)
                )
            }
        }
        composable(Routes.Names) { entry ->
            val id = entry.arguments?.getString("id")?.toLongOrNull() ?: return@composable
            Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Datos de las personas") }) }) { padding ->
                NamesScreen(
                    eventId = id,
                    selectionVM = selectionVM,
                    onNext = { navController.navigate("checkout/$id") },
                    modifier = Modifier.padding(padding)
                )
            }
        }
        composable(Routes.Checkout) { entry ->
            val id = entry.arguments?.getString("id")?.toLongOrNull() ?: return@composable
            Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Confirmación") }) }) { padding ->
                CheckoutScreen(
                    eventId = id,
                    selectionVM = selectionVM,
                    onDone = {
                        navController.navigate(Routes.Events) {
                            popUpTo(Routes.Events) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}