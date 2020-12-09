package aaliusman.example.espressotesting.factory

import aaliusman.example.espressotesting.data.source.MoviesDataSource
import aaliusman.example.espressotesting.ui.movie.DirectorsFragment
import aaliusman.example.espressotesting.ui.movie.MovieDetailFragment
import aaliusman.example.espressotesting.ui.movie.StarActorsFragment
import androidx.fragment.app.FragmentFactory
import com.bumptech.glide.request.RequestOptions

class MovieFragmentFactory(
    private val requestOptions: RequestOptions? = null,
    private val moviesDataSource: MoviesDataSource? = null
) : FragmentFactory() {

    private val TAG: String = "AppDebug"

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            MovieDetailFragment::class.java.name -> {
                if (requestOptions != null
                    && moviesDataSource != null
                ) {
                    MovieDetailFragment(
                    )
                } else {
                    super.instantiate(classLoader, className)
                }
            }

            DirectorsFragment::class.java.name -> {
                DirectorsFragment()
            }

            StarActorsFragment::class.java.name -> {
                StarActorsFragment()
            }

            else -> {
                super.instantiate(classLoader, className)
            }
        }
}













