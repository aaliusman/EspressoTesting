package aaliusman.example.espressotesting

import aaliusman.example.espressotesting.data.Movie
import aaliusman.example.espressotesting.factory.MovieFragmentFactory
import aaliusman.example.espressotesting.ui.movie.MovieDetailFragment
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.bumptech.glide.request.RequestOptions
import org.hamcrest.Matchers.containsString
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matchers.not

@RunWith(AndroidJUnit4ClassRunner::class)
class MovieDetailFragmentTest {


    @Test
    fun test_navToDetailFragment() {

        // SETUP
        val movieId = 0
        val title = "Avengers: Infinity War"
        val description =
            "The Avengers and their allies must be willing to sacrifice all in an attempt to defeat the powerful Thanos before his blitz of devastation and ruin puts an end to the universe."
        val movie = Movie(
            movieId,
            title,
            "https://nyc3.digitaloceanspaces.com/open-api-spaces/open-api-static/blog/1/Infinity_War-infinity_war.png",
            description,
            arrayListOf("Anthony Russo", "Joe Russo"),
            arrayListOf(
                "Robert Downey Jr.", "Chris Hemsworth", "Mark Ruffalo", "+ more..."
            )
        )

        val requestOptions = RequestOptions()
            .placeholder(R.drawable.default_image)
            .error(R.drawable.default_image)
        val fragmentFactory = MovieFragmentFactory()
        val bundle = Bundle()
        bundle.putInt("movie_id", movieId)
        val scenario = launchFragmentInContainer<MovieDetailFragment>(
            fragmentArgs = bundle,
            factory = fragmentFactory
        )

        // VERIFY
        onView(withId(R.id.movie_title)).check(matches(withText(title)))
            .check(matches(withClassName(containsString("TextView"))))

        onView(withId(R.id.movie_description)).check(matches(withText(description)))
            .check(matches(withClassName(containsString("TextView"))))


        onView(withId(R.id.movie_star_actors)).check(isCompletelyBelow(withId(R.id.movie_directors)))
            .check(matches(withClassName(containsString("TextView"))))

        onView(withId(R.id.movie_star_actors)).check(matches(isDisplayed()))

        onView(withId(R.id.movie_image)).check(matches(not(isDisplayed())))
        onView(withId(R.id.directors_text)).check(doesNotExist())
        Thread.sleep(2500)
        onView(withId(R.id.movie_image)).check(matches(isDisplayed()))
            .check(matches(withClassName(containsString("ImageView"))))

    }

//    fun ViewInteraction.matchesImage(resourceId: Int): ViewInteraction {
//        return this.check(matches(Matchers.isBitmapTheSame(resourceId)))
//    }
}









