package com.akvelon.grimmuzzle.ui.screens.mainscreen.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.akvelon.grimmuzzle.data.entities.Tale
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.saved.SavedTalesListFragment
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.shared.SharedTalesListFragment

class TalesScreenViewPagerAdapter(
    private val fa: FragmentActivity
) :
    FragmentStateAdapter(fa) {

    private val ITEM_COUNT = 2
    private val SAVED_TALES_TYPE = 0

    fun updateTalesData(list: List<Tale>) {
        val savedFragment =
            fa.supportFragmentManager.fragments.find { it is SavedTalesListFragment }
        val sharedFragment =
            fa.supportFragmentManager.fragments.find { it is SharedTalesListFragment }
        savedFragment?.let {
            (it as SavedTalesListFragment).updateTalesDataInList(list)
        }
        sharedFragment?.let {
            (it as SharedTalesListFragment).updateTalesDataInList(list)
        }
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            SAVED_TALES_TYPE -> {
                SavedTalesListFragment()
            }
            else -> {
                SharedTalesListFragment()
            }
        }
    }

    override fun getItemCount(): Int {
        return ITEM_COUNT
    }
}
