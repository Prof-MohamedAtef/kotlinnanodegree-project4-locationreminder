package com.udacity.project4.atef.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.udacity.project4.atef.utils.Config

class AuthenticationViewModel:ViewModel() {
    val authenticationState=FirebaseLiveData().map { firebaseUser ->
        if (firebaseUser!=null){
            Config.AuthenticationState.AUTHENTICATED
        }else{
            Config.AuthenticationState.UNAUTHENTICATED
        }
    }
}