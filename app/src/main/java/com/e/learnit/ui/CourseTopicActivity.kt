package com.e.learnit.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.e.learnit.R
import com.e.learnit.ui.fragments.CourseTopicFragment
import kotlinx.android.synthetic.main.activity_course_topic.*

class CourseTopicActivity : AppCompatActivity() {

    lateinit var id:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_topic)

        setSupportActionBar(video_toolbar)
        val appBar = supportActionBar
        appBar!!.title = "LearnIt"
        appBar.subtitle="Course Topic"
        appBar.setLogo(R.mipmap.ic_launcher_round)
        appBar.setDisplayUseLogoEnabled(true)
        appBar.setDisplayHomeAsUpEnabled(true)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)

         id = intent.getStringExtra("ID")

        supportFragmentManager
            .beginTransaction()
            .add(R.id.course_topic_activity,CourseTopicFragment.newInstance(id,"string"))
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1)
        {
            if(resultCode == Activity.RESULT_OK)
                id = data!!.getStringExtra("id")
        }
    }


}
