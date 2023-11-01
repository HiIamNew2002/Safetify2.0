package com.example.safetify201v

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.safetify201v.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_routes, R.id.navigation_alarm, R.id.navigation_call, R.id.navigation_guides
            )
        )

        //hide the navigation for greeting1 Fragment's bar
        /*navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == R.id.greeting1Fragment) {

                navView.visibility = View.GONE
            } else {

                navView.visibility = View.VISIBLE
            }
        }*/

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.greeting1Fragment,
                R.id.greeting2Fragment,
                R.id.greeting3Fragment -> {
                    // Hide the bottom navigation bar for specific fragments
                    navView.visibility = View.GONE
                }
                else -> {
                    // Show the bottom navigation bar for other fragments
                    navView.visibility = View.VISIBLE
                }
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }




}