package com.e.learnit.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.e.learnit.R
import kotlinx.android.synthetic.main.activity_read_notes.*
import kotlinx.android.synthetic.main.activity_read_notes.notes_title

class ReadNotesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_notes)

        setSupportActionBar(notes_topic_toolbar)
        val appBar = supportActionBar

        appBar!!.title = "LearnIt"
        appBar.subtitle="Video and Notes"
        appBar.setLogo(R.mipmap.ic_launcher_round)
        appBar.setDisplayUseLogoEnabled(true)
        appBar.setDisplayHomeAsUpEnabled(true)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)

        var topic = intent.getStringExtra("topic")
        var data = intent.getStringExtra("data")

        notes_title.text = topic
        notes_content.text = data
    }
}
