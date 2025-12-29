package um.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import um.frontend.data.api.BackendApi
import um.frontend.data.repo.AuthRepository
import um.frontend.data.repo.EventsRepository
import um.frontend.data.repo.SelectionRepository
import um.frontend.data.store.TokenStore
import um.frontend.ui.navigation.AppNavGraph
import um.frontend.ui.theme.UmTheme
import um.frontend.ui.navigation.Routes

import um.frontend.ui.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenStore = TokenStore(this)
        var cachedToken: String? = tokenStore.getToken()
        val api = BackendApi { cachedToken }

        val authVM = AuthViewModel(AuthRepository(api), tokenStore)
        val eventsVM = EventsViewModel(EventsRepository(api))
        val selectionVM = SelectionViewModel(SelectionRepository(api), tokenStore)
        val startDest = if (cachedToken.isNullOrBlank()) Routes.Login else Routes.Events
        setContent {
            UmTheme {
                val nav = rememberNavController()
                AppNavGraph(
                    navController = nav,
                    authVM = authVM,
                    onTokenUpdate = { cachedToken = it },
                    eventsVM = eventsVM,
                    selectionVM = selectionVM
                )
            }
        }
    }
}