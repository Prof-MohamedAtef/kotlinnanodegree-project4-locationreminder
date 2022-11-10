package com.udacity.project4.atef.utils

object Config {
    val AuthPref: String?="AuthenticationPreferences"
    const val SIGN_IN_INTENT_RESULT_CODE=1000

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }
}