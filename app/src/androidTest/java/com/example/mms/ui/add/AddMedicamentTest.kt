package com.example.mms.ui.add

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.mms.R
import org.junit.Test

class AddMedicamentTest {

    @Test
    fun testEditionNomDuMedicamentVidePresent() {
        ActivityScenario.launch(AddMedicamentActivity::class.java)
        onView(withId(R.id.edit_medicament_nom)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_medicament_nom)).check(matches(withText("")))
    }

    @Test
    fun testPresenceListeDeroulanteForme() {
        ActivityScenario.launch(AddMedicamentActivity::class.java)
        onView(withId(R.id.spinner_forme)).check(matches(isDisplayed()))
    }

    @Test
    fun testPresenceListeDeroulanteDosage(){
        ActivityScenario.launch(AddMedicamentActivity::class.java)
        onView(withId(R.id.spinner_dosage)).check(matches(isDisplayed()))
    }

    @Test
    fun testPresenceBoutonSuivant(){
        ActivityScenario.launch(AddMedicamentActivity::class.java)
        onView(withId(R.id.next_button)).check(matches(isDisplayed()))
    }

    @Test
    fun testPresenceBoutonRetour(){
        ActivityScenario.launch(AddMedicamentActivity::class.java)
        onView(withId(R.id.back_button)).check(matches(isDisplayed()))
    }
}