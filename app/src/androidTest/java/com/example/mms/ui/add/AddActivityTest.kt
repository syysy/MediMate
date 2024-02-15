package com.example.mms.ui.add

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.mms.R
import org.junit.Test

class AddActivityTest {

    @Test
    fun testPresenceBoutonRetour() {
        ActivityScenario.launch(AddActivity::class.java)
        onView(withId(R.id.back_button)).check(matches(isDisplayed()))
    }

    @Test
    fun testPresenceSearchBar() {
        ActivityScenario.launch(AddActivity::class.java)
        onView(withId(R.id.medi_searchbar)).check(matches(isDisplayed()))
    }

    @Test
    fun testPresenceBoutonScanOrdonance() {
        ActivityScenario.launch(AddActivity::class.java)
        onView(withId(R.id.buttonScanOrdonnance)).check(matches(isDisplayed()))
    }

    @Test
    fun testPresenceBoutonGetPhoto() {
        ActivityScenario.launch(AddActivity::class.java)
        onView(withId(R.id.buttonGetPhoto)).check(matches(isDisplayed()))
    }
}