package com.e.learnit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.e.learnit.adapter.ViewPagerAdapter
import com.e.learnit.model.SliderData
import com.e.learnit.ui.fragments.CourseFragment
import com.e.learnit.ui.fragments.QuizFragment
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
     var  imageList:ArrayList<SliderData> = ArrayList()
    val u : com.e.learnit.utility.Utilities = com.e.learnit.utility.Utilities()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Adding data for the View Pager
        imageList.add(SliderData("C++", R.drawable.cpp))
        imageList.add(SliderData("JAVA", R.drawable.java))
        imageList.add(SliderData("C#", R.drawable.csharp))
        imageList.add(SliderData("Python", R.drawable.python))

        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        //Calling the View pager Adapter
        root.slider_view_pager.adapter =
            ViewPagerAdapter(activity!!.supportFragmentManager, imageList)
        root.slider_view_pager.currentItem = 0
        root.slider_view_pager.setPageTransformer(false,MyPageTransformer())

        if(u.isOnline(activity!!))
        {
            activity!!.supportFragmentManager
                .beginTransaction()
                .add(R.id.course_layout,CourseFragment(),"Course")
                .commit()

            activity!!.supportFragmentManager
                .beginTransaction()
                .add(R.id.quiz_layout, QuizFragment(),"Quiz")
                .commit()

            return root
        }
        else{
            u.showMaterialDialogNoInternet(activity!!)
        }

        return  inflater.inflate(R.layout.fragment_error, container, false)
    }

    private class MyPageTransformer: androidx.viewpager.widget.ViewPager.PageTransformer {
        private val MIN_SCALE = 0.85F
        private val MIN_ALPHA = 0.5F

        override fun transformPage(p0: View, p1: Float) {
            val pageW = p0.width
            val pageH = p0.height

            if (p1 < -1) { // way off-screen to the left!
                p0.alpha = 0f // full transparency, 1: full opacity
            }
            else if (p1 <= 1) { //[-1 0]
                val scaleFactor = Math.max(MIN_SCALE, 1 -Math.abs(p1))
                val verMargin = pageH * (1-scaleFactor)/2
                val horMargin = pageW * (1-scaleFactor)/2

                if (p1 < 0)
                    p0.translationX = horMargin - verMargin/2
                else
                    p0.translationX = verMargin/2 - horMargin

                p0.scaleX = scaleFactor
                p0.scaleY = scaleFactor

                p0.alpha = MIN_ALPHA + (scaleFactor - MIN_SCALE)/(1 - MIN_SCALE)*(1 - MIN_ALPHA)
                //p0.rotationY = p0.translationX*(-30)
            }
            else { // (1, +infinity
                p0.alpha = 0F
            }
        }
    }
}