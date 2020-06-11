package com.e.learnit.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.e.learnit.R
import com.e.learnit.model.Quiz
import com.e.learnit.ui.QuizTopicActivity
import com.e.learnit.utility.Utilities
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_quiz.*

class QuizFragment : Fragment() {
    val u = Utilities()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_quiz, container, false)

        if(u.isOnline(activity!!))
        {
            val ref: DatabaseReference
            ref = FirebaseDatabase.getInstance().getReference().child("Quiz")
            val quizRecyclerView = root.findViewById<RecyclerView>(R.id.quiz_recycler_view)
            quizRecyclerView.layoutManager = GridLayoutManager(activity!!.applicationContext, 2)


            QuizFireBaseData(ref, quizRecyclerView)
        }
        else
            u.showMaterialDialogNoInternet(activity!!)

        return root
    }

    private fun QuizFireBaseData(ref: DatabaseReference, quizRecyclerView: RecyclerView?) {
        val option = FirebaseRecyclerOptions.Builder<Quiz>()
            .setQuery(ref, Quiz::class.java)
            .setLifecycleOwner(this)
            .build()


        val firebaseRecyclerAdapter =
            object : FirebaseRecyclerAdapter<Quiz, MyViewHolder>(option) {


                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                    val itemView =
                        LayoutInflater.from(activity).inflate(R.layout.quiz_layout, parent, false)
                    return MyViewHolder(itemView)
                }

                override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Quiz) {
                    val placeid = getRef(position).key.toString()

                    ref.child(placeid).addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            Toast.makeText(
                                activity,
                                "Error Occurred " + p0.toException(),
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            quiz_progress_bar.visibility =
                                if (itemCount == 0) View.VISIBLE else View.GONE

                            holder.txt_name.setText(model.Name)
                            Picasso.get().load(model.Image).into(holder.img_vet)
                            holder.itemView.setOnClickListener {
                                val intent = Intent(activity!!,QuizTopicActivity::class.java)
                                intent.putExtra("id",placeid)
                                startActivity(intent)
                            }
                        }
                    })
                }
            }
        quizRecyclerView!!.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var txt_name: TextView = itemView!!.findViewById(R.id.quiz_name)
        internal var img_vet: ImageView = itemView!!.findViewById(R.id.quiz_image)


    }

}
