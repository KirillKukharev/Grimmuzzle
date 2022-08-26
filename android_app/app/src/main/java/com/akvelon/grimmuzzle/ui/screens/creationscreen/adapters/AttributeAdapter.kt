package com.akvelon.grimmuzzle.ui.screens.creationscreen.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.akvelon.grimmuzzle.R
import com.akvelon.grimmuzzle.data.entities.Attribute
import com.akvelon.grimmuzzle.databinding.AttributeCheckboxBinding


class AttributeAdapter:
    RecyclerView.Adapter<AttributeAdapter.ViewHolder>() {
    private var attributes: MutableList<Attribute> = mutableListOf()
    private lateinit var reattachCallback: () -> Unit
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<AttributeCheckboxBinding>(
            LayoutInflater.from(viewGroup.context), R.layout.attribute_checkbox, viewGroup, false
        )
        return ViewHolder(binding.root, binding)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val attribute = attributes[i]
        viewHolder.bind(attribute)

    }

    override fun getItemCount(): Int {
        return attributes.size
    }

    fun setReattachCallback(reattachCallback: () -> Unit) {
        this.reattachCallback = reattachCallback
    }

    fun setAttributes(attributes: MutableList<Attribute>) {
        this.attributes = attributes
        this.notifyDataSetChanged()
    }

    private fun changeSelectionStatus(attribute: Attribute, attributes: MutableList<Attribute>) {
        if (attribute.getType().isMultiple()) {
            if (attribute.isSelected.get()) {
                attribute.isSelected.set(false)
            } else {
                attribute.isSelected.set(true)
            }
        } else {
            attributes.forEach {
                it.isSelected.set(false)
            }
            attribute.isSelected.set(true)
        }
        this.reattachCallback()
    }

    inner class ViewHolder(itemView: View, val binding: AttributeCheckboxBinding) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(attribute: Attribute) {
            val attributeContainer = binding.attributeContainer
            binding.attribute = attribute
            attributeContainer.setOnClickListener {
                changeSelectionStatus(attribute, attributes)
            }
        }
    }
}