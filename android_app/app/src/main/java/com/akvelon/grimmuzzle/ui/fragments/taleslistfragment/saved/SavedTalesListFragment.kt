package com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.akvelon.grimmuzzle.R
import com.akvelon.grimmuzzle.databinding.TalesListFragmentBinding
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common.TalesListFragment
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common.TalesListViewModel

class SavedTalesListFragment : TalesListFragment() {

    lateinit var binding: TalesListFragmentBinding
    lateinit var viewModel: SavedTalesListViewModel

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

    override fun getBinding(): ViewDataBinding {
        return binding
    }

    override fun initViewModel(): TalesListViewModel {
        viewModel = ViewModelProvider(this).get(SavedTalesListViewModel::class.java)
        return viewModel
    }
}