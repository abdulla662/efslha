package com.example.efslha.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.efslha.R

private val TAB_TITLES = arrayOf(
        R.string.tab_text_1,
        R.string.tab_text_2
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        if (position==0){
            return BronzeFragment()
        }else if(position==1){
        return SilverFragment()
        }else if (position==2){
            return GoldFragment()
        }else{
            return PlatinumFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        if (position==0){
            return "Bronze"

        }else if (position==1){
            return "Silver"
        }else{
            return "Gold"
        }
    }

    override fun getCount(): Int {
        //here i will show 4 pages adapter
        return 3
    }
}