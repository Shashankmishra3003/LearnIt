package com.e.learnit.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.e.learnit.ui.fragments.QuestionFragment

class MyFragmentAdapter (fm:FragmentManager,var context: Context,
                         var fragmentList:List<QuestionFragment>):FragmentPagerAdapter(fm){

    internal var instance:MyFragmentAdapter?=null
    
    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return StringBuilder("Question ").append(position +1).toString()
    }

}