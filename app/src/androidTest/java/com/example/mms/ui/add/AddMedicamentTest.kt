package com.example.mms.ui.add

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.mms.R
import org.junit.Test

class AddMedicamentTest {

    @Test
    fun testNomDuMedicament() {
        ActivityScenario.launch(AddMedicamentActivity::class.java)
        onView(withId(R.id.edit_medicament_nom)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_medicament_nom)).check(matches(withText("")))
    }
}