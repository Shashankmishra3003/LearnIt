package com.e.learnit.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.e.learnit.R
import kotlinx.android.synthetic.main.fragment_slider.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "name"
private const val ARG_PARAM2 = "image"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SliderFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SliderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SliderFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var name: String? = null
    private var image: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString(ARG_PARAM1)
            image = it.getInt(ARG_PARAM2)
        }
    }

    override fun onStart() {
        super.onStart()
        slider_name.setText(name)
        image?.let { slider_image.setImageResource(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_slider, container, false)
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(name: String, image: Int) =
            SliderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, name)
                    putInt(ARG_PARAM2, image)
                }
            }
    }
}
