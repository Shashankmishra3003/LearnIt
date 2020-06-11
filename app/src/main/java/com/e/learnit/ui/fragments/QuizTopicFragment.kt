package com.e.learnit.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.learnit.Interface.IAnswerSelect

import com.e.learnit.R
import com.e.learnit.model.CourseTopic
import com.e.learnit.model.QuizTopic
import com.e.learnit.ui.QuizActivity
import com.e.learnit.ui.VideoNotesActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.common.internal.service.Common
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_course_topic.*
import kotlinx.android.synthetic.main.fragment_quiz_topic.*

// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"

class QuizTopicFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    val u : com.e.learnit.utility.Utilities = com.e.learnit.utility.Utilities()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.fragment_quiz_topic, container, false)

        val ref: DatabaseReference
        ref = FirebaseDatabase.getInstance().getReference().child("QuizTopics")
        val quizTopicRecyclerView = root.findViewById<RecyclerView>(R.id.quiz_topic_recycler_view)
        quizTopicRecyclerView.layoutManager = LinearLayoutManager(activity,
            RecyclerView.VERTICAL,false)


        quizFireBaseData(ref, quizTopicRecyclerView)

        if(u.isOnline(activity!!))
        {
            return root
        }

        return inflater.inflate(R.layout.fragment_course_topic, container, false)
    }

    private fun quizFireBaseData(ref: DatabaseReference, quizTopicRecyclerView: RecyclerView?) {

        val option = FirebaseRecyclerOptions.Builder<QuizTopic>()
            .setQuery(ref.orderByChild("id").equalTo(param1), QuizTopic::class.java)
            .setLifecycleOwner(this)
            .build()

        val firebaseRecyclerAdapter =
            object : FirebaseRecyclerAdapter<QuizTopic, MyViewHolder>(option) {


                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                    val itemView =
                        LayoutInflater.from(activity).inflate(R.layout.quiz_topic_layout, parent, false)
                    return MyViewHolder(itemView)
                }

                override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: QuizTopic) {
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
                            quiz_topic_progress_bar.visibility =
                                if (itemCount == 0) View.VISIBLE else View.GONE

                            holder.txt_name.setText(model.Name)
                            holder.txt_description.setText(model.Description)

                            holder.itemView.setOnClickListener {
                                com.e.learnit.Common.Common.selectedCategory = model.id
                                val intent = Intent(activity, QuizActivity::class.java)
                                intent.putExtra("category",com.e.learnit.Common.Common.selectedCategory)

                                startActivityForResult(intent,1)
                            }

                        }
                    })

                }
            }
        quizTopicRecyclerView!!.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var txt_name: TextView = itemView.findViewById(R.id.quiz_topic)
        internal var txt_description: TextView = itemView.findViewById(R.id.quiz_description)


    }


    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            QuizTopicFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1)
        {
            if(resultCode == Activity.RESULT_OK)
                param1 = data!!.getStringExtra("id")
        }
    }
}
