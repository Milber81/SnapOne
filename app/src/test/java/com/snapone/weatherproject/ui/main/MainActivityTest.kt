package com.snapone.weatherproject.ui.main

import android.widget.Button
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.snapone.weatherproject.ui.AddCityFragment
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class MainActivityTest {

    private lateinit var activity: MainActivity
    private lateinit var button: Button
    private lateinit var recyclerView: RecyclerView

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(MainActivity::class.java)
            .create()
            .start()
            .resume()
            .get()

        button = activity.findViewById(com.snapone.weatherproject.R.id.addCityButton)
        recyclerView = activity.findViewById(com.snapone.weatherproject.R.id.rec)

    }

    @Test
    fun testButtonText() {
        button.performClick()

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        val fragment =
            activity.supportFragmentManager.findFragmentByTag(AddCityFragment::class.java.simpleName)

        assertNotNull(fragment)
        assertTrue(fragment is AddCityFragment)
    }

    @Test
    fun testRecyclerViewIsVisibleAndHasAtLeastThreeItems() {
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        assertTrue(recyclerView.isVisible)

        val adapter = recyclerView.adapter
        assertNotNull(adapter)
        assertTrue(adapter?.itemCount ?: 0 >= 3)
    }
}
