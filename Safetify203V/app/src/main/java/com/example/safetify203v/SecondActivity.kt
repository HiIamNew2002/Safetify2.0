package com.example.safetify203v


import android.os.Bundle
import android.Manifest
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.safetify203v.databinding.ActivitySecondBinding
import com.google.android.material.navigation.NavigationView

class SecondActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{


    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitleTextAppearance(this, R.style.ActionBarTitle)

        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.nav_open, R.string.nav_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationDrawer.setNavigationItemSelectedListener(this)

        binding.bottomNavigation.background = null
        binding.bottomNavigation.setOnItemSelectedListener{item ->
            when(item.itemId){
                R.id.bottom_call -> openFragment(CallFragment())
                R.id.bottom_alarm -> openFragment(AlarmFragment())
                R.id.bottom_home -> openFragment(HomeFragment())
                R.id.bottom_guides -> openFragment(GuidesFragment())
                R.id.bottom_report -> openFragment(ReportFragment())
            }
            true
        }


        fragmentManager = supportFragmentManager
        openFragment(HomeFragment())




    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_contacts -> openFragment(ContactsFragment())
            //R.id.nav_report -> openFragment(ReportFragment())
            R.id.nav_settings -> openFragment(SettingsFragment())
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }

    private fun openFragment(fragment: Fragment){
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
}