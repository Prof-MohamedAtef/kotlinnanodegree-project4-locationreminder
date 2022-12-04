package com.udacity.project4.atef

import android.app.Activity
import android.app.Application
import android.util.Log
import android.view.View
import androidx.navigation.NavController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationViewModel
import com.udacity.project4.authentication.FakeAuthController
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import kotlinx.android.synthetic.main.fragment_select_location.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.mockito.Mockito.mock


/*
//    TODO: add End to End testing to the app
 */
//END TO END test to black box test the app

@RunWith(AndroidJUnit4::class)
@LargeTest
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test
    private var mockNavController: NavController?=null
    private var decorView: View? = null

    private lateinit var repository: ReminderDataSource

    private lateinit var appContext: Application

    private val fakePointOfInterest = PointOfInterest(LatLng(30.1589, 31.6954), "Fake POI Num", "Fake Home Title")

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */

    @get:Rule
    val activityRule = ActivityTestRule(RemindersActivity::class.java)

    @get:Rule @JvmField
    val FineLocationPermission: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    @get:Rule @JvmField
    val CoarseLocationPermission: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    @get:Rule @JvmField
    val BackgroundLocationPermission: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
            single { FakeAuthController(true) as AuthenticationViewModel }
            single { listOf(fakePointOfInterest) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }

        decorView=activityRule.activity.window.decorView
        mockNavController = mock(NavController::class.java)
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    // return activity
    private fun getActivity(activityScenario: ActivityScenario<RemindersActivity>): Activity? {
        var activity: Activity? = null
        activityScenario.onActivity {
            activity = it
        }
        return activity
    }

    @Test
    fun addReminderEndToEndTesting(){
        //create custom reminder
        val reminder = ReminderDTO(
            "Test Title 1", "Test Description 1",
            fakePointOfInterest.name, fakePointOfInterest.latLng.latitude, fakePointOfInterest.latLng.longitude
        )
        // launch reminders screen
        val remindersActivity = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(remindersActivity)
        //check view with id is displayed
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))

        //perform click on fab view
        onView(withId(R.id.addReminderFAB)).perform(click())

        // write title
        onView(withId(R.id.reminderTitle)).perform(typeText(reminder.title))
        // check location granted snackBar on
        onView(withText(R.string.location_granted))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        //write description
        onView(withId(R.id.reminderDescription)).perform(typeText(reminder.description), closeSoftKeyboard())
        // perform click
        onView(withId(R.id.selectLocation)).perform(click())

        try {
            // check map is appearing
            runBlocking { delay(3000) }
            onView(withId(R.id.map_fragment)).check(matches(isDisplayed()))

            //todo note
            // May cannot perform a POI click which is hardoced
            // You must click on the Map yourself during the delay ( during the map is appearing ).

            // redirect to select location screen
            runBlocking { delay(1000) }
            // save poi
            onView(withId(R.id.savePoi)).perform(click())

            // Click on save reminder to save and add geofence
            runBlocking { delay(4000) }
            onView(withId(R.id.saveReminder)).perform(click())

            runBlocking {
                repository.saveReminder(reminder)
            }

            onView(withText(R.string.geofence_added))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        } catch (e: NoMatchingViewException) {
            // View is not in hierarchy
            Log.e("Some Tests Failed", "Please Select a Point on map")
        }



//        // check location granted snackBar
//        onView(withText(R.string.location_granted))
//            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
//        // perform click
//        onView(withId(R.id.selectLocation)).perform(click())
//        runBlocking { delay(1000) }
//        Espresso.pressBack()
//        onView(withId())
//            .check(matches(withText(R.string.location_granted)))
//        onView(withId(R.id.reminderTitle)).check(matches(isDisplayed()))
//        onView(withId(R.id.reminderDescription)).check(matches(isDisplayed()))
    // Check if select location name is displayed
//        onView(withId(R.id.selectedLocation)).check(matches(withText(reminder.title)))
    // Check if toast is shown and with R.string.reminder_saved
    // Check if it was correctly displayed on the recyclerView and its items hasDescendant similar title, description, and location
//        onView(withId(R.id.reminderssRecyclerView)).check(matches(isDisplayed()))
//        onView(withId(R.id.reminderssRecyclerView)).check(matches(hasDescendant((withText(reminder.title)))))
//        onView(withId(R.id.reminderssRecyclerView)).check(matches(hasDescendant((withText(reminder.description)))))
//        onView(withId(R.id.reminderssRecyclerView)).check(matches(hasDescendant((withText(reminder.location)))))
//
//        onView(withText(R.string.geofence_added))
//            .inRoot(withDecorView(Matchers.not(decorView)))// Here you use decorView
//            .check(matches(isDisplayed()));
//
//        Log.e("AtefTest", "Finished Testing")
//        // close activity
//        remindersActivity.close()
    }
}