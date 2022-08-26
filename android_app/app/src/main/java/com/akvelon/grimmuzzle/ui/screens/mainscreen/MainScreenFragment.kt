package com.akvelon.grimmuzzle.ui.screens.mainscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.akvelon.grimmuzzle.R
import com.akvelon.grimmuzzle.data.entities.event.EventProcessor
import com.akvelon.grimmuzzle.databinding.MainScreenFragmentBinding
import com.akvelon.grimmuzzle.ui.MainActivity
import com.akvelon.grimmuzzle.ui.screens.mainscreen.viewpager.TalesScreenViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainScreenFragment : Fragment(), EventProcessor<Any> {

    private lateinit var viewModel: MainScreenViewModel
    private lateinit var binding: MainScreenFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.main_screen_fragment,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainScreenViewModel::class.java)
        viewModel.adapter = TalesScreenViewPagerAdapter(MainActivity.instance)
        binding.viewPager.adapter = viewModel.adapter
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.position == 1) {
                    tab.view.setBackgroundResource(R.drawable.active_globe_earth)
                } else {
                    tab.view.setBackgroundResource(R.drawable.active_bookmark)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                if (tab!!.position == 1) {
                    tab.view.setBackgroundResource(R.drawable.globe_earth)
                } else {
                    tab.view.setBackgroundResource(R.drawable.bookmark)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 ->
                    tab.view.setBackgroundResource(R.drawable.bookmark)
                else ->
                    tab.view.setBackgroundResource(R.drawable.globe_earth)
            }
        }.attach()
    }

    override fun onResume() {
        super.onResume()
        viewModel.adapter.updateTalesData(viewModel.dataNode.pendingTalesUpdates)
    }

    override fun onError(t: Throwable?) {
        binding.tabLayout.visibility = View.INVISIBLE
        binding.viewPager.visibility = View.INVISIBLE
    }

    override fun onSuccess() {
        binding.tabLayout.visibility = View.VISIBLE
        binding.viewPager.visibility = View.VISIBLE
    }
}