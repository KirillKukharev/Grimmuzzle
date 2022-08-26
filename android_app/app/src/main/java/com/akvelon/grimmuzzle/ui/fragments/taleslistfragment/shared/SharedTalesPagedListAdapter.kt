package com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.akvelon.grimmuzzle.R
import com.akvelon.grimmuzzle.data.entities.Tale
import com.akvelon.grimmuzzle.databinding.TaleViewHolderBinding
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common.TalesPagedListAdapter

class SharedTalesPagedListAdapter(
    diffItemCallback: DiffUtil.ItemCallback<Tale>
) :
    TalesPagedListAdapter(diffItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaleViewHolder {
        val binding: TaleViewHolderBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.tale_view_holder,
            parent,
            false
        )
        return super.TaleViewHolder(binding)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TaleViewHolder).bind(getItem(position), position)
    }
}