package com.e.learnit.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.e.learnit.R
import com.e.learnit.model.Notes
import com.e.learnit.utility.Utilities
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.android.synthetic.main.activity_video_notes.*


class VideoNotesActivity : AppCompatActivity() {

    var topicName:String? = ""
    var videoId:String? = ""
    var topicId:String? = ""
    val u : Utilities = Utilities()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_notes)

        topicName = intent.getStringExtra("topicName")
        videoId = intent.getStringExtra("video")
        topicId = intent.getStringExtra("topicId")

        setSupportActionBar(video_toolbar)
        val appBar = supportActionBar

        appBar!!.title = "LearnIt"
        appBar.subtitle="Video and Notes"
        appBar.setLogo(R.mipmap.ic_launcher_round)
        appBar.setDisplayUseLogoEnabled(true)
       // appBar.setDisplayHomeAsUpEnabled(true)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)

        if(u.isOnline(this))
        {
            video_view.getPlayerUiController().showFullscreenButton(true)
            video_view.addYouTubePlayerListener(object : AbstractYouTubePlayerListener(){
                override fun onReady(@NonNull youTubePlayer: YouTubePlayer)
                {

                    youTubePlayer.cueVideo(videoId.toString(),0f)
                }
            })

            video_view.getPlayerUiController().setFullScreenButtonClickListener(View.OnClickListener {
                if (video_view.isFullScreen())
                {
                    video_view.exitFullScreen()
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

                    //Show Action bar

                    appBar.show()
                }
                else
                {
                    video_view.enterFullScreen()
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

                    appBar.hide()
                }
            })

            txt_notes.setMovementMethod(ScrollingMovementMethod())

            getNotes(topicId)
        }
        else
            u.showMaterialDialogNoInternet(this)

    }

    private fun getNotes(topicId: String?) {
        val acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext())
        val userId = acct!!.id.toString()

        var reference = FirebaseDatabase.getInstance().getReference("Notes")

        reference.child(userId)
        reference.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
//                if(p0.value == null)
//                {
                        save_notes.setOnClickListener {
                            if(save_notes.text.trim().isEmpty())
                            {
                                u.showNoDataDialog(this@VideoNotesActivity)
                            }
                            else
                            {
                                val request = Notes(
                                    edt_notes.getText().toString(),
                                    topicName
                                )

                                reference.child(userId).child(topicName.toString()).setValue(request)
                            }

                            it.hideKeyboard()
                    }
//
//                }
//                else
//                {
//                    reference.child(userId).child(topicId.toString())
//                    reference.addListenerForSingleValueEvent(object:ValueEventListener{
//                        override fun onCancelled(p0: DatabaseError) {
//
//                        }
//
//                        override fun onDataChange(p0: DataSnapshot) {
//
//                            for(notesSnap in p0.children)
//                            {
//                                val notes = notesSnap.child(topicId.toString()).getValue(Notes::class.java)!!
//                                edt_notes.visibility = View.GONE
//                                save_notes.visibility  =View.GONE
//
//                                txt_notes.visibility = View.VISIBLE
//                                txt_notes.text = notes.data
//                            }
//
//                        }
//                    })
//                }
            }

        })



    }

    override fun onBackPressed() {
        val i = Intent()
        i.putExtra("id",topicId)
        setResult(Activity.RESULT_OK,i)
        finish()
    }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item?.itemId)
        {
            R.id.menu_notes ->
            {
                startActivity(Intent(this,MyNotesActivity::class.java))
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
}
