package com.e.learnit.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import androidx.core.content.ContextCompat
import com.e.learnit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.core.utilities.Utilities
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(quiz_topic_toolbar)

        val user = FirebaseAuth.getInstance()

        val appBar = supportActionBar
        appBar!!.title = "LearnIt"
        appBar.subtitle="Home"
        appBar.setLogo(R.mipmap.ic_launcher_round)
        appBar.setDisplayUseLogoEnabled(true)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)

        //setting the nav header items
        val hview = nav_view.getHeaderView(0)
        hview.user_name.text = user.currentUser!!.displayName
        hview.user_email.text = user.currentUser!!.email
        Picasso.get()
            .load(user.currentUser!!.photoUrl)
            .into(hview.user_image)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_home
                ), drawerLayout
            )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener {
            when(it.itemId)
            {
                R.id.nav_share ->
                {
                    signOut()
                    true
                }
                R.id.my_courses->
                {
                    startActivity(Intent(this,MyNotesActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
//
//        return true
//    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun signOut()
    {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, SignInActivity::class.java))
    }
}
