package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainCoroutineDispatcher
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
/*
TODO: provide testing to the RemindersListViewModel and its live data objects
 */
class RemindersListViewModelTest {
    // testing with architecture components
    @get:Rule
    var instanceExecutorRule=InstantTaskExecutorRule()

    // main coroutine Dispatcher for Unit Testing
    @get:Rule
    var mainCoroutineDispatcherRule = MainCoroutineRule()


    // use test double/Fake data source to be injected into the viewmodel
    private lateinit var dataSource: FakeDataSource
}