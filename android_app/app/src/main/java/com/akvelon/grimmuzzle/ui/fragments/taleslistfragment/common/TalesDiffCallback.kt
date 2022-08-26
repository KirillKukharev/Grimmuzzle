package com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common

import androidx.recyclerview.widget.DiffUtil
import com.akvelon.grimmuzzle.data.entities.Tale

class TalesDiffCallback : DiffUtil.ItemCallback<Tale>() {
    override fun areItemsTheSame(oldItem: Tale, newItem: Tale): Boolean {
        return oldItem.id == newItem.id
    }

    //Now can only change tale title
    override fun areContentsTheSame(oldItem: Tale, newItem: Tale): Boolean {
        return oldItem == newItem
    }
}


