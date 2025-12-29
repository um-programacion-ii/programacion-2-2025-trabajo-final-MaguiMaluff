package um.frontend.data.store

import android.content.Context

class TokenStore(context: Context) {
    private val prefs = context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)
    fun saveToken(token: String?) {
        prefs.edit().apply {
            if (token == null) remove(KEY_TOKEN) else putString(KEY_TOKEN, token)
        }.apply()
    }

    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)
    fun saveUserId(userId: String?) {
        prefs.edit().apply {
            if (userId == null) remove(KEY_USER_ID) else putString(KEY_USER_ID, userId)
        }.apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val KEY_TOKEN = "token"
        const val KEY_USER_ID = "userId"
    }
}