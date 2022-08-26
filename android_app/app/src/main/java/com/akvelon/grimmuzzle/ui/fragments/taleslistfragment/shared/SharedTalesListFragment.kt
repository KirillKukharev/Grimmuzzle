package com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.shared

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.akvelon.grimmuzzle.R
import com.akvelon.grimmuzzle.data.entities.event.EventProcessor
import com.akvelon.grimmuzzle.databinding.TalesListFragmentBinding
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common.TalesListFragment

class SharedTalesListFragment : TalesListFragment() {

    lateinit var binding: TalesListFragmentBinding
    lateinit var viewModel: SharedTalesListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.tales_list_fragment,
                container,
                false
            )
        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //To prevent blinking we switch off animator
        binding.talesRecyclerView.itemAnimator = null
        setRefreshOnClickListener()
    }

    override fun onSuccess() {
        super.onSuccess()
        binding.refreshButton.visibility = View.VISIBLE
    }

    override fun getBinding(): ViewDataBinding {
        return binding
    }

    override fun initViewModel(): SharedTalesListViewModel {
        viewModel = ViewModelProvider(this).get(SharedTalesListViewModel::class.java)
        return viewModel
    }

    private fun setRefreshOnClickListener() {
        binding.refreshButton.setOnClickListener {
            if (binding.refreshButton.drawable is AnimatedVectorDrawable) {
                (binding.refreshButton.drawable as AnimatedVectorDrawable).start()
            }
            val eventProcessor = object : EventProcessor<Any> {
                override fun onLoad() {
                    this@SharedTalesListFragment.onLoad()
                }

                override fun onSuccess() {
                    this@SharedTalesListFragment.successAfterLayoutComplete = true
                }

                override fun onError(t: Throwable?) {
                    this@SharedTalesListFragment.onError(t)
                }
            }
            viewModel.setNewPagedTalesList(
                this@SharedTalesListFragment,
                eventProcessor
            )
        }
    }
}