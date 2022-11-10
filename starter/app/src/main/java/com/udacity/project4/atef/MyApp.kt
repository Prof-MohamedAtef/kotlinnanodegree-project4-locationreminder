package com.udacity.project4.atef

import android.app.Application
import com.udacity.project4.atef.authentication.AuthenticationViewModel
import com.udacity.project4.atef.authentication.sharedPrefs.MyPrefs
import com.udacity.project4.atef.locationreminders.data.ReminderDataSource
import com.udacity.project4.atef.locationreminders.data.local.LocalDB
import com.udacity.project4.atef.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.atef.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.atef.locationreminders.savereminder.SaveReminderViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module


val prefs: MyPrefs by lazy {
    MyApp.prefs!!
}

class MyApp : Application() {

    companion object {
        var prefs: MyPrefs? = null
        lateinit var instance: MyApp
            private set
    }

    override fun onCreate() {
        super.onCreate()

        /*
        global sharedPrefs
         */
        instance=this
        prefs=MyPrefs(applicationContext)

        /**
         * use Koin Library as a service locator
         */
        val myModule = module {
            //Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
            viewModel {
                RemindersListViewModel(
                    get(),
                    get() as ReminderDataSource
                )

            }
            //Declare singleton definitions to be later injected using by inject()
            single {
                //This view model is declared singleton to be used across multiple fragments
                SaveReminderViewModel(
                    get(),
                    get() as ReminderDataSource
                )
            }

            //Declare singleton definitions to be later injected using by inject()
            single {
                //This view model is declared singleton to be used across multiple fragments
                AuthenticationViewModel()
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(this@MyApp) }
        }

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(myModule))
        }
    }
}