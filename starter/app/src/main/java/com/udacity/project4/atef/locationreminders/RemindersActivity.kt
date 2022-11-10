package com.udacity.project4.atef.locationreminders

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.udacity.project4.atef.R
import com.udacity.project4.atef.authentication.AuthenticationActivity
import com.udacity.project4.atef.authentication.AuthenticationViewModel
import com.udacity.project4.atef.utils.Config
import kotlinx.android.synthetic.main.activity_reminders.*
import org.koin.android.ext.android.inject

/**
 * The RemindersActivity that holds the reminders fragments
 */
class RemindersActivity : AppCompatActivity() {

    private val authenticationViewModel by inject<AuthenticationViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminders)

        authenticationViewModel.authenticationState.observe(this, Observer { authState->
            if (authState==Config.AuthenticationState.UNAUTHENTICATED){
                goToAuthenticationActivity()
            }
        })
    }

    private fun goToAuthenticationActivity() {
        val authIntent = Intent(this, AuthenticationActivity::class.java)
        startActivity(authIntent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout-> {
                goToAuthenticationActivity()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}