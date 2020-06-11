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
import com.e.learnit.model.Course
import com.e.learnit.ui.CourseTopicActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_course.*



class CourseFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_course, container, false)
        val ref: DatabaseReference
        ref = FirebaseDatabase.getInstance().getReference().child("Course")
        val courseRecyclerView = root.findViewById<RecyclerView>(R.id.course_recycler_view)
        courseRecyclerView.layoutManager = GridLayoutManager(activity!!.applicationContext, 2)


        fireBaseData(ref, courseRecyclerView)
        return root
    }

    private fun fireBaseData(ref: DatabaseReference, courseRecyclerView: RecyclerView?) {
        val option = FirebaseRecyclerOptions.Builder<Course>()
            .setQuery(ref, Course::class.java)
            .setLifecycleOwner(this)
            .build()


        val firebaseRecyclerAdapter =
            object : FirebaseRecyclerAdapter<Course, MyViewHolder>(option) {


                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                    val itemView =
                        LayoutInflater.from(activity).inflate(R.layout.course_layout, parent, false)
                    return MyViewHolder(itemView)
                }

                override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Course) {
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
                            course_progress_bar.visibility =
                                if (itemCount == 0) View.VISIBLE else View.GONE

                            holder.txt_name.setText(model.Name)
                            Picasso.get().load(model.Image).into(holder.img_vet)

                            holder.itemView.setOnClickListener{
                                val intent = Intent(activity!!,CourseTopicActivity::class.java)
                                intent.putExtra("ID",placeid)
                                startActivity(intent)

                            }

                        }
                    })

                }
            }
        courseRecyclerView!!.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var txt_name: TextView = itemView!!.findViewById(R.id.notes_name)
        internal var img_vet: ImageView = itemView!!.findViewById(R.id.course_image)

    }
}
