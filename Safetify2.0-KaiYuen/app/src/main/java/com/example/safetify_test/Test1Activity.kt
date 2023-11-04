package com.example.safetify_test

/*
*  So far some naming schemes are default by Google's template, not much time to find all dependencies to change the names one by one
*  Gallery = Inputting document
*  Home = Viewing document
*  Slideshow = Panic Button
* */


import android.content.Intent
import android.os.Bundle
import android.view.Menu

import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.safetify_test.databinding.ActivityTest1Binding


class Test1Activity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityTest1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        //default code for the layouts
        binding = ActivityTest1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarTest1.toolbar)

        binding.appBarTest1.fab.setOnClickListener { view ->
            val intent = Intent(this, Test1Activity::class.java)
            startActivity(intent)
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView2
        val navController = findNavController(R.id.nav_host_fragment_content_test1)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_retrieve_report, R.id.nav_submit_report, R.id.nav_panic_button
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.test1, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_test1)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}