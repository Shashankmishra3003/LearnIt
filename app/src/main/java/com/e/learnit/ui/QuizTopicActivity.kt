package com.e.learnit.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.e.learnit.R
import com.e.learnit.ui.fragments.QuizTopicFragment
import kotlinx.android.synthetic.main.app_bar_main.*

class QuizTopicActivity : AppCompatActivity() {
    lateinit var id:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_topic)

        setSupportActionBar(quiz_topic_toolbar)
        val appBar = supportActionBar
        appBar!!.title = "LearnIt"
        appBar.subtitle="Quiz Topics"
        appBar.setLogo(R.mipmap.ic_launcher_round)
        appBar.setDisplayUseLogoEnabled(true)
        appBar.setDisplayHomeAsUpEnabled(true)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)

        id = intent.getStringExtra("id")

        supportFragmentManager
            .beginTransaction()
            .add(R.id.quiz_topic_activity,QuizTopicFragment.newInstance(id))
            .commit()

    }


}
