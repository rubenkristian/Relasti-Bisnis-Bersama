package com.ethelworld.RBBApp.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    behavior: Int,
    private var tabName: Array<String>,
    private var pages: Array<Fragment>): FragmentStatePagerAdapter(fragmentManager, behavior) {

    override fun getItem(position: Int): Fragment {
        return pages[position]
    }

    override fun getCount(): Int {
        return pages.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabName[position]
    }
}