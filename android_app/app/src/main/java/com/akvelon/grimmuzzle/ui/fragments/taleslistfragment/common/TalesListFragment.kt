package com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.akvelon.grimmuzzle.R
import com.akvelon.grimmuzzle.data.entities.Attribute
import com.akvelon.grimmuzzle.data.entities.Tale
import com.akvelon.grimmuzzle.data.entities.event.EventProcessor
import com.akvelon.grimmuzzle.databinding.TalesListFragmentBinding
import com.akvelon.grimmuzzle.ui.MainActivity
import com.akvelon.grimmuzzle.ui.screens.mainscreen.MainScreenFragment

abstract class TalesListFragment : Fragment(),
    EventProcessor<MutableList<Attribute>> {

    var successAfterLayoutComplete = false

    abstract fun getBinding(): ViewDataBinding

    abstract fun initViewModel(): TalesListViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = initViewModel()
        (getBinding() as TalesListFragmentBinding).talesRecyclerView.layoutManager =
            object : LinearLayoutManager(context, HORIZONTAL, false) {
                override fun onLayoutCompleted(state: RecyclerView.State?) {
                    super.onLayoutCompleted(state)
                    checkForScrollButtons()
                    if (successAfterLayoutComplete) {
                        successAfterLayoutComplete = false
                        (getBinding() as TalesListFragmentBinding).talesRecyclerView.postDelayed(
                            {
                                onSuccess()
                            },
                            1000
                        )
                    }
                }
            }
        initData()
        (getBinding() as TalesListFragmentBinding).talesRecyclerView.adapter =
            viewModel.getAdapter()
    }

    private fun initData() {
        val viewModel = initViewModel()
        viewModel.initData(
            this, this
        )
        (getBinding() as TalesListFragmentBinding).talesRecyclerView.adapter =
            viewModel.getAdapter()
        (getBinding() as TalesListFragmentBinding).talesRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstCompVisPos =
                    ((getBinding() as TalesListFragmentBinding).talesRecyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                val lastCompVisPos: Int =
                    ((getBinding() as TalesListFragmentBinding).talesRecyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val countView =
                    (getBinding() as TalesListFragmentBinding).talesRecyclerView.adapter!!.itemCount

                if (firstCompVisPos <= 1) {
                    (getBinding() as TalesListFragmentBinding).arrowLeft.visibility = View.GONE
                } else {
                    (getBinding() as TalesListFragmentBinding).arrowLeft.visibility = View.VISIBLE
                }
                if (lastCompVisPos >= countView - 2) {
                    (getBinding() as TalesListFragmentBinding).arrowRight.visibility = View.GONE
                } else {
                    (getBinding() as TalesListFragmentBinding).arrowRight.visibility = View.VISIBLE
                }
            }
        })
    }

    fun updateTalesDataInList(list: List<Tale>) {
        initViewModel().getAdapter().updateTalesList(list)
    }

    override fun onLoad() {
        (getBinding() as TalesListFragmentBinding).arrowLeft.visibility = View.GONE
        (getBinding() as TalesListFragmentBinding).arrowRight.visibility = View.GONE
        (getBinding() as TalesListFragmentBinding).talesRecyclerView.visibility = View.INVISIBLE
        MainActivity.instance.getProgressBar().visibility = View.VISIBLE
    }

    override fun onSuccess() {
        MainActivity.instance.getProgressBar().visibility = View.INVISIBLE
        (getBinding() as TalesListFragmentBinding).talesRecyclerView.visibility = View.VISIBLE
        val navHostFragment: NavHostFragment? =
            MainActivity.instance.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        val mainFragment =
            navHostFragment?.childFragmentManager?.fragments?.find { it is MainScreenFragment }
        if (mainFragment is MainScreenFragment) {
            mainFragment.onSuccess()
        }
    }

    override fun onError(t: Throwable?) {
        (getBinding() as TalesListFragmentBinding).talesRecyclerView.visibility = View.GONE
        MainActivity.instance.getProgressBar().visibility = View.VISIBLE
        val navHostFragment: NavHostFragment? =
            MainActivity.instance.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        val mainFragment =
            navHostFragment?.childFragmentManager?.fragments?.find { it is MainScreenFragment }
        if (mainFragment is MainScreenFragment) {
            mainFragment.onError(t)
        }
        MainActivity.instance.showErrorView(::initData)
    }

    private fun enableScrollButtons() {
        (getBinding() as TalesListFragmentBinding).arrowLeft.setOnClickListener {
            var scrollItemPosition =
                ((getBinding() as TalesListFragmentBinding).talesRecyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() - 2
            if (scrollItemPosition < 0)
                scrollItemPosition = 0
            smoothScrollToPosition(scrollItemPosition)
        }
        (getBinding() as TalesListFragmentBinding).arrowRight.setOnClickListener {
            var scrollItemPosition =
                ((getBinding() as TalesListFragmentBinding).talesRecyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() + 1
            if (scrollItemPosition >= (getBinding() as TalesListFragmentBinding).talesRecyclerView.adapter!!.itemCount)
                scrollItemPosition =
                    (getBinding() as TalesListFragmentBinding).talesRecyclerView.adapter!!.itemCount - 1
            smoothScrollToPosition(scrollItemPosition)
        }
        (getBinding() as TalesListFragmentBinding).arrowRight.visibility = View.VISIBLE
    }

    private fun hideScrollButtons() {
        (getBinding() as TalesListFragmentBinding).arrowLeft.visibility = View.GONE
        (getBinding() as TalesListFragmentBinding).arrowRight.visibility = View.GONE
    }

    protected fun checkForScrollButtons() {
        val firstCompVisPos =
            ((getBinding() as TalesListFragmentBinding).talesRecyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        val lastCompVisPos: Int =
            ((getBinding() as TalesListFragmentBinding).talesRecyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
        if ((firstCompVisPos >= 0) && (lastCompVisPos > 0) && ((getBinding() as TalesListFragmentBinding).talesRecyclerView.adapter!!.itemCount > (lastCompVisPos - firstCompVisPos) + 1)) {
            enableScrollButtons()
        } else {
            hideScrollButtons()
        }
    }

    private fun smoothScrollToPosition(position: Int) {
        val smoothScroller = object : LinearSmoothScroller(this.context) {
            override fun getHorizontalSnapPreference(): Int = SNAP_TO_START
        }
        smoothScroller.targetPosition = position
        (getBinding() as TalesListFragmentBinding).talesRecyclerView.layoutManager?.startSmoothScroll(
            smoothScroller
        )
    }
}