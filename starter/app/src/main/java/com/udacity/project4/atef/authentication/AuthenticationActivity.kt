package com.udacity.project4.atef.authentication

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.udacity.project4.atef.R
import com.udacity.project4.atef.utils.Config.SIGN_IN_INTENT_RESULT_CODE

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        findViewById<Button>(R.id.btnSignIn).setOnClickListener { launchGoogleSignInIntent() }



//         TODO: Implement the create account and sign in using FirebaseUI, use sign in using email and sign in using Google

//          TODO: If the user was authenticated, send him to RemindersActivity

//          TODO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout

    }

    private fun launchGoogleSignInIntent() {
        val providersList= arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val customLayout = AuthMethodPickerLayout.Builder(R.layout.signin_layout)
            .setGoogleButtonId(R.id.btnGoogleSignIn)
            .setEmailButtonId(R.id.btnEmailLogin)
            .build()

        val signInIntent=AuthUI.getInstance().createSignInIntentBuilder()
            .setAuthMethodPickerLayout(customLayout)
            .setAvailableProviders(providersList)
            .build()

        startActivityForResult(signInIntent, SIGN_IN_INTENT_RESULT_CODE)
    }
}
