package com.e.learnit.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.e.learnit.model.SliderData
import com.e.learnit.ui.fragments.SliderFragment

class ViewPagerAdapter(fragmentManager: FragmentManager,val sliderList:List<SliderData>):
    FragmentPagerAdapter(fragmentManager) {


    override fun getItem(position: Int): Fragment {

      return SliderFragment.newInstance(
                sliderList[position].sliderName,
                sliderList[position].image_id
            )

    }

    override fun getCount(): Int {
        return sliderList.size
    }
}