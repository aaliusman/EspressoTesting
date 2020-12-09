package aaliusman.example.espressotesting.data.source

import aaliusman.example.espressotesting.data.Movie

interface MoviesDataSource {

    fun getMovie(movieId: Int): Movie?
}