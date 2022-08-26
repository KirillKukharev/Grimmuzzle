package com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common

import android.os.Bundle
import android.view.View
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.akvelon.grimmuzzle.R
import com.akvelon.grimmuzzle.data.entities.Attribute
import com.akvelon.grimmuzzle.data.entities.Tale
import com.akvelon.grimmuzzle.databinding.TaleViewHolderBinding
import com.akvelon.grimmuzzle.ui.MainActivity

abstract class TalesPagedListAdapter(
    diffItemCallback: DiffUtil.ItemCallback<Tale>
) :
    PagedListAdapter<Tale, RecyclerView.ViewHolder>(diffItemCallback) {
    var attributesByType: MutableList<Attribute>? = null
        set(value) {
            if (value != field) {
                field = value
                notifyDataSetChanged()
            }
        }

    open fun updateTalesList(talesList: List<Tale>) {
        if (talesList.isNotEmpty()) {
            currentList?.let {
                for ((index, tale) in it.withIndex()) {
                    val newTale = talesList.find { newTale -> newTale.id == tale.id }
                    if (newTale != null) {
                        tale.update(newTale)
                        notifyItemChanged(index)
                    }
                }
            }
        }
    }

    open inner class TaleViewHolder(val binding: TaleViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        open fun bind(tale: Tale?, position: Int) {
            if (tale == null) {
                return
            }
            binding.tale = tale
            setBackgroundImageURL(tale)
            setHeroImagesURLs(tale)
            binding.taleConstraintLayout.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("id", tale.id)
                MainActivity.instance.navController.navigate(
                    R.id.action_mainScreenFragment_to_readingScreenFragment,
                    bundle
                )
            }
        }

        private fun setBackgroundImageURL(tale: Tale) {
            binding.locationImageURL = attributesByType?.let { attributes ->
                attributes.find { attr -> attr.getType().getLabel() == "Where" && attr.getID() == tale.input.inputMap["Where"]!![0]}!!.getFullHDImgURL()
            }
        }

        private fun setHeroImagesURLs(tale: Tale) {
            val whoList : List<Attribute> = attributesByType?.let {
                it.filter { attr -> attr.getType().getLabel() == "Who" }
            } ?: return
            val taleCharacters = tale.input.inputMap["Who"]!!
            binding.mainHeroImageURL =
                whoList.find { it.getID() == taleCharacters[0] }!!.getImgURL()
            binding.minorHero1CardView.visibility = View.GONE
            binding.minorHero2CardView.visibility = View.GONE
            if (taleCharacters.size >= 2) {
                binding.minorHero1ImageURL =
                    whoList.find { it.getID() == taleCharacters[1] }!!.getImgURL()
                binding.minorHero1CardView.visibility = View.VISIBLE
            }
            if (taleCharacters.size >= 3) {
                binding.minorHero2ImageURL =
                    whoList.find { it.getID() == taleCharacters[2] }!!.getImgURL()
                binding.minorHero2CardView.visibility = View.VISIBLE
            }
        }
    }
}
