package aaliusman.example.espressotesting.ui.movie

import aaliusman.example.espressotesting.R
import aaliusman.example.espressotesting.factory.MovieFragmentFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = MovieFragmentFactory()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }
        private fun init(){
            if(supportFragmentManager.fragments.size == 0){
                val movieId = 0
                val bundle = Bundle()
                bundle.putInt("movie_id", movieId)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MovieDetailFragment::class.java, bundle)
                    .commit()
            }
        }
}