package com.e.learnit.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.learnit.R
import com.e.learnit.model.Notes
import com.e.learnit.utility.Utilities
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_my_notes.*

class MyNotesActivity : AppCompatActivity() {
    val u = Utilities()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_notes)

        setSupportActionBar(quiz_topic_toolbar)
        val appBar = supportActionBar

        appBar!!.title = "LearnIt"
        appBar.subtitle="My Notes"
        appBar.setLogo(R.mipmap.ic_launcher_round)
        appBar.setDisplayUseLogoEnabled(true)
        appBar.setDisplayHomeAsUpEnabled(true)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        val acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext())
        val userId = acct!!.id.toString()

        if(u.isOnline(this))
        {
            val ref: DatabaseReference =
                FirebaseDatabase.getInstance().getReference().child("Notes").child(userId)
            val notesRecyclerView = findViewById<RecyclerView>(R.id.notes_recycler_view)
            notesRecyclerView.layoutManager = LinearLayoutManager(this,
                RecyclerView.VERTICAL,false)

            fireBaseData(ref, notesRecyclerView)
        }
        else
            u.showMaterialDialogNoInternet(this)

    }

    private fun fireBaseData(ref: DatabaseReference, notesRecyclerView: RecyclerView?) {
        val option = FirebaseRecyclerOptions.Builder<Notes>()
            .setQuery(ref, Notes::class.java)
            .setLifecycleOwner(this)
            .build()

        val firebaseRecyclerAdapter =
            object : FirebaseRecyclerAdapter<Notes, MyViewHolder>(option) {


                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                    val itemView =
                        LayoutInflater.from(this@MyNotesActivity).inflate(R.layout.notes_layout, parent, false)
                    return MyViewHolder(itemView)
                }

                override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Notes) {
                    val placeid = getRef(position).key.toString()

                    ref.child(placeid).addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            Toast.makeText(
                                this@MyNotesActivity,
                                "Error Occurred " + p0.toException(),
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            notes_topic_progress_bar.visibility =
                                if (itemCount == 0) View.VISIBLE else View.GONE

                            holder.txt_name.setText(model.topicName)

                            holder.itemView.setOnClickListener {
                                val intent = Intent(this@MyNotesActivity,ReadNotesActivity::class.java)
                                intent.putExtra("topic",model.topicName)
                                intent.putExtra("data",model.data)
                                startActivity(intent)
                            }

                        }
                    })

                }
            }
        notesRecyclerView!!.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()


    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var txt_name: TextView = itemView.findViewById(R.id.notes_name)

    }
}
