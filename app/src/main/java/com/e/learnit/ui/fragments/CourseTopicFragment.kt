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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.e.learnit.R
import com.e.learnit.model.Course
import com.e.learnit.model.CourseTopic
import com.e.learnit.ui.VideoNotesActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_course.*
import kotlinx.android.synthetic.main.fragment_course_topic.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CourseTopicFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CourseTopicFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CourseTopicFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val u : com.e.learnit.utility.Utilities = com.e.learnit.utility.Utilities()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val root = inflater.inflate(R.layout.fragment_course_topic, container, false)

        val ref: DatabaseReference
        ref = FirebaseDatabase.getInstance().getReference().child("CourseTopics")
        val courseTopicRecyclerView = root.findViewById<RecyclerView>(R.id.course_topic_recycler_view)
        courseTopicRecyclerView.layoutManager = LinearLayoutManager(activity,RecyclerView.VERTICAL,false)


        fireBaseData(ref, courseTopicRecyclerView)
        if(u.isOnline(activity!!))
        {
            return root
        }
        else
            u.showMaterialDialogNoInternet(activity!!)

        return inflater.inflate(R.layout.fragment_error, container, false)

    }

    private fun fireBaseData(ref: DatabaseReference, courseTopicRecyclerView: RecyclerView?) {
        val option = FirebaseRecyclerOptions.Builder<CourseTopic>()
            .setQuery(ref.orderByChild("id").equalTo(param1), CourseTopic::class.java)
            .setLifecycleOwner(this)
            .build()

        val firebaseRecyclerAdapter =
            object : FirebaseRecyclerAdapter<CourseTopic, MyViewHolder>(option) {


                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                    val itemView =
                        LayoutInflater.from(activity).inflate(R.layout.course_topic_layout, parent, false)
                    return MyViewHolder(itemView)
                }

                override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: CourseTopic) {
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
                            course_topic_progress_bar.visibility =
                                if (itemCount == 0) View.VISIBLE else View.GONE

                            holder.txt_name.setText(model.Name)
                            holder.txt_description.setText(model.Description)

                            holder.itemView.setOnClickListener {
                                val intent = Intent(activity,VideoNotesActivity::class.java)
                                intent.putExtra("video",model.Video)
                                intent.putExtra("topicName",model.Name)
                                intent.putExtra("topicId",param1)
                                startActivityForResult(intent,1)
                            }

                        }
                    })

                }
            }
        courseTopicRecyclerView!!.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var txt_name: TextView = itemView.findViewById(R.id.course_topic_name)
        internal var txt_description: TextView = itemView.findViewById(R.id.course_topic_desc)


    }


    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CourseTopicFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
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
