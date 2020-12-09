package aaliusman.example.espressotesting

import aaliusman.example.espressotesting.Matchers.childAtPosition
import aaliusman.example.espressotesting.Matchers.isBitmapTheSame
import aaliusman.example.espressotesting.ui.movie.DirectorsFragment
import aaliusman.example.espressotesting.ui.movie.MainActivity
import android.os.SystemClock
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.hamcrest.Matchers.containsString
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import junit.framework.Assert
import junit.framework.AssertionFailedError
import org.hamcrest.Matchers.allOf


@RunWith(AndroidJUnit4ClassRunner::class)
class NavigationTest{

    var uiDevice: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Test
    fun testMovieFragmentsNavigation() {
        var context = InstrumentationRegistry.getInstrumentation().context
        // SETUP
        val directorsOfTheRun = arrayListOf("R.J. Stewart", "James Vanderbilt")
        val actorsOfTheRun = arrayListOf(
            "Dwayne Johnson",
            "Seann William Scott",
            "Rosario Dawson",
            "Christopher Walken"
        )
        val directorsInfinityWar = arrayListOf("Anthony Russo", "Joe Russo")
        val actorsOfInfinityWar = arrayListOf(
            "Robert Downey Jr.", "Chris Hemsworth", "Mark Ruffalo", "+ more..."
        )

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        // NAV DirectorsFragment
//        onView(withId(R.id.movie_directors)).check(matches(withClassName(containsString("AppCompatTextView"))))
//            .perform(forceClick())



        waitTillTextIsClickable("android.widget.TextView", "Directors", 1000)

//        onView(allOf(withId(R.id.movie_star_actors), isDescendantOfA(childAtPosition(withId(R.id.movie_directors), 0))))
//            .check(matches(withText("Star Actors")))
//            .check(matches(withClassName(containsString("AppCompatTextView"))))
//            3000

        viewWithIdAndText(R.id.movie_directors, "Directors").check(
            matches(
                withClassName(
                    containsString(
                        "AppCompatTextView"
                    )
                )
            )
        )
            .isBelowViewWithId(R.id.movie_image)
            .perform(click())
        var directorTitle = uiDevice.findObject(UiSelector().className("android.widget.TextView").text("Directors:"))
        Assert.assertTrue("Directors title is missing", directorTitle.exists())

        // VERIFY
        onView(withId(R.id.fragment_directors_parent))
            .check(matches(isDisplayed()))

        onView(withId(R.id.directors_text)).check(
            matches(
                withText(
                    DirectorsFragment.stringBuilderForDirectors(
                        directorsInfinityWar
                    )
                )
            )
        )
        // NAV MovieDetailFragment
        pressBack()

//        onView(withId(R.id.movie_image))
//            .check(matches(isDisplayed()))
//            .check(matches(isBitmapTheSame(R.drawable.infinity_war)))

        // VERIFY
        onView(allOf(withId(R.id.fragment_movie_detail_parent)))
            .check(matches(isDisplayed()))
        onView(withId(R.id.fragment_movie_detail_parent))
            .check(matches(isDisplayed()))

        onView(withId(R.id.movie_star_actors)).check(matches(isCompletelyDisplayed()))
            .check(isCompletelyBelow(withId(R.id.movie_directors)))
            .check(matches(withClassName(containsString("TextView"))))

        onView(withId(R.id.movie_description)).check(matches(isCompletelyDisplayed()))
            .check(isCompletelyBelow(withId(R.id.view)))

        onView(withId(R.id.movie_description)).check(matches(isCompletelyDisplayed()))
            .check(isCompletelyBelow(withId(R.id.movie_star_actors)))

        onView(withId(R.id.movie_description)).check(matches(isCompletelyDisplayed()))
            .check(isCompletelyBelow(withId(R.id.movie_star_actors)))
            .check(matches(withClassName(containsString("AppCompatTextView"))))
            .classNameContains("CompatText")


        // NAV StarActorsFragment
        onView(withId(R.id.movie_star_actors)).perform(click())
        onView(withId(R.id.star_actors_text)).check(
            matches(
                withText(
                    stringBuilderForStarActors(
                        actorsOfInfinityWar
                    )
                )
            )
        )

        // VERIFY
        onView(withId(R.id.fragment_star_actors_parent))
            .check(matches(isCompletelyDisplayed()))

        // NAV MovieDetailFragment
        pressBack()

        // VERIFY
        onView(withId(R.id.fragment_movie_detail_parent))
            .check(matches(isDisplayed()))
    }



    private fun stringBuilderForStarActors(actors: ArrayList<String>): String{
        val sb = StringBuilder()
        for(actor in actors){
            sb.append(actor + "\n")
        }
        return sb.toString()
    }

    private fun forceClick(): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return CoreMatchers.allOf(isClickable(), isEnabled(), isDisplayed())
            }

            override fun getDescription(): String {
                return "force click"
            }

            override fun perform(uiController: UiController, view: View) {
                view.performClick() // perform click without checking view coordinates.
                uiController.loopMainThreadUntilIdle()
            }
        }
    }

    fun viewWithIdAndText(id: Int, text: String): ViewInteraction {
        return onView(CoreMatchers.allOf(withId(id), withText(text)))
    }

    fun ViewInteraction.isBelowViewWithId(id: Int): ViewInteraction {
        return this.check(isCompletelyBelow(withId(id)))
    }

    fun ViewInteraction.classNameContains(className: String): ViewInteraction {
        return this.check(matches(withClassName(CoreMatchers.containsString(className))))
    }

    fun waitTillTextIsClickable(className: String, viewName: String, waitInMillis: Long) {
        var visible = false
        var lastError: Throwable? = null
        val startTime = System.currentTimeMillis()
        while (!visible && System.currentTimeMillis() - startTime < waitInMillis) {
            try {
                val uiObject = uiDevice.findObject(UiSelector().className(className).text(viewName))
                Assert.assertTrue(viewName + " is not clickable", uiObject.isClickable());
                visible = true
            } catch (e: AssertionFailedError) {
                lastError = e
                SystemClock.sleep(500)
            }
        }
        if (visible == false) {
            throw lastError!!
        }
    }

}











