package com.akvelon.grimmuzzle.ui.screens.creationscreen.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akvelon.grimmuzzle.R
import com.akvelon.grimmuzzle.data.entities.Attribute
import com.akvelon.grimmuzzle.databinding.AttributePageBinding


class AttributePagesAdapter : RecyclerView.Adapter<AttributePagesAdapter.AttributesPageVH>() {

    private var tabs = mutableListOf<MutableList<Attribute>>()
    private lateinit var reattachCallback: () -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttributesPageVH {
        val binding = DataBindingUtil.inflate<AttributePageBinding>(
            LayoutInflater.from(parent.context), R.layout.attribute_page, parent, false
        )
        return AttributesPageVH(binding.root, binding)

    }

    override fun getItemCount(): Int = tabs.size

    fun setTabs(attributes: MutableList<Attribute>) {
        val groupsAttr = mutableListOf<MutableList<Attribute>>()
        var tabIndex = 0
        var addedAttributesCount = 0
        while (addedAttributesCount != attributes.size) {
            val attributesByType = attributes.filter { it.getType().getTabIndex() == tabIndex }
            tabIndex++
            addedAttributesCount += attributesByType.size
            groupsAttr.add(attributesByType.toMutableList())
        }
        this.tabs = groupsAttr
        this.notifyDataSetChanged()
    }

    fun setReattachCallback(reattachCallback: () -> Unit) {
        this.reattachCallback = reattachCallback
    }

    override fun onBindViewHolder(holder: AttributesPageVH, position: Int) {
        val attr = tabs[position]
        holder.bind(attr)
    }

    private fun calculateNoOfColumns(
        holder: AttributesPageVH,
        numberOfItems: Int
    ): Int {
        val context = holder.itemView.context
        val attributeContainerWidth =
            holder.itemView.resources.getDimension(R.dimen.attribute_container_size)
        val attributeContainerDistance =
            holder.itemView.resources.getDimension(R.dimen.attribute_container_distance)
        val screenWidth = context.resources.displayMetrics.widthPixels
        val numberOfColumns =
            (screenWidth / (attributeContainerWidth + attributeContainerDistance)).toInt()
        return if (numberOfColumns > numberOfItems) numberOfItems else numberOfColumns
    }

    inner class AttributesPageVH(itemView: View, val binding: AttributePageBinding) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(attr: MutableList<Attribute>) {
            val recyclerView = binding.recyclerView
            val numberOfColumns = calculateNoOfColumns(this, attr.size)
            val attributeAdapter = AttributeAdapter()
            attributeAdapter.setReattachCallback(this@AttributePagesAdapter.reattachCallback)
            recyclerView.adapter = attributeAdapter
            attr.let { attributeAdapter.setAttributes(attr) }
            recyclerView.layoutManager =
                GridLayoutManager(itemView.context, numberOfColumns)
        }
    }
}