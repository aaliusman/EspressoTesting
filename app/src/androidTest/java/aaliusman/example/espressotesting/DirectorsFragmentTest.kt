package aaliusman.example.espressotesting

import android.os.Bundle
import aaliusman.example.espressotesting.factory.MovieFragmentFactory
import aaliusman.example.espressotesting.ui.movie.DirectorsFragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class DirectorsFragmentTest {


    @Test
    fun test_isDirectorsListVisible() {
        // Given
        val directors = arrayListOf("R.J. Stewart", "James")
        val fragmentFactory = MovieFragmentFactory()
        val bundle = Bundle()
        bundle.putStringArrayList("args_directors", directors)
        val scenario = launchFragmentInContainer<DirectorsFragment> (
            fragmentArgs = bundle,
            factory = fragmentFactory
        )

        //VERIFY
            onView(withId(R.id.directors_text)).check((matches(isCompletelyDisplayed())))
        onView(withId(R.id.directors_text)).check(matches(withText(DirectorsFragment.stringBuilderForDirectors(directors))))
//        onView(withId(R.id.directors_text)).check(matches(withText("R.J. Stewart")))
        onView(withSubstring("R.J. Stewart")).check(matches(withText(DirectorsFragment.stringBuilderForDirectors(directors))))
        onView(withText(R.string.text_directors)).check(matches(isCompletelyDisplayed()))
        onView(withText(R.string.text_directors)).check(matches(withText("Directors:")))
        onView(withId(R.id.directors_text)).check(matches(withText(DirectorsFragment.stringBuilderForDirectors(
            arrayListOf("R.J. Stewart", "James")))))

    }
}