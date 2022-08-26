package com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.saved

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.recyclerview.widget.*
import com.akvelon.grimmuzzle.R
import com.akvelon.grimmuzzle.data.entities.Tale
import com.akvelon.grimmuzzle.data.preferences.SavedTalesPreferences
import com.akvelon.grimmuzzle.databinding.NewTaleViewHolderBinding
import com.akvelon.grimmuzzle.databinding.TaleViewHolderBinding
import com.akvelon.grimmuzzle.ui.MainActivity
import com.akvelon.grimmuzzle.ui.dialogs.RenamingDialogFragment
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common.TalesPagedListAdapter

class SavedTalesPagedListAdapter(
    diffItemCallback: DiffUtil.ItemCallback<Tale>
) :
    TalesPagedListAdapter(diffItemCallback) {

    private var differ: AsyncPagedListDiffer<Tale>
    private val HEADER_TYPE = 0
    private val ITEM_TYPE = 1
    var renamingDialog = RenamingDialogFragment()

    init {
        val adapterCallback = AdapterListUpdateCallback(this)
        val listUpdateCallback = ListWithHeaderUpdateCallback(adapterCallback)
        differ = AsyncPagedListDiffer<Tale>(
            listUpdateCallback,
            AsyncDifferConfig.Builder<Tale>(diffItemCallback).build()
        )
    }

    override fun updateTalesList(talesList: List<Tale>) {
        if (talesList.isNotEmpty()) {
            currentList?.let {
                for ((index, tale) in it.withIndex()) {
                    val newTale = talesList.find { newTale -> newTale.id == tale.id }
                    if (newTale != null) {
                        tale.update(newTale)
                        notifyItemChanged(index + 1)
                    }
                }
            }
        }
    }

    override fun getItem(position: Int): Tale? {
        return differ.getItem(position - 1)
    }

    override fun submitList(pagedList: PagedList<Tale>?) {
        differ.submitList(pagedList)
    }

    override fun submitList(pagedList: PagedList<Tale>?, commitCallback: Runnable?) {
        differ.submitList(pagedList, commitCallback)
    }

    override fun getCurrentList(): PagedList<Tale>? {
        return differ.currentList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ITEM_TYPE) {
            val binding: TaleViewHolderBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.tale_view_holder,
                parent,
                false
            )
            return SavedTaleViewHolder(binding)
        } else {
            val bindingNewTale: NewTaleViewHolderBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.new_tale_view_holder,
                parent,
                false
            )
            return NewTaleViewHolder(bindingNewTale)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SavedTaleViewHolder) {
            holder.bind(getItem(position), position)
        } else {
            (holder as NewTaleViewHolder).bind()
        }
    }

    override fun getItemViewType(position: Int) =
        if (position > 0) ITEM_TYPE else HEADER_TYPE

    override fun getItemCount(): Int {
        return differ.itemCount + 1
    }

    inner class SavedTaleViewHolder(binding: TaleViewHolderBinding) : TaleViewHolder(binding) {

        override fun bind(tale: Tale?, position: Int) {
            super.bind(tale, position)
            bindMenu(binding, tale, position)
        }

        private fun bindMenu(binding: TaleViewHolderBinding, tale: Tale?, position: Int) {
            if (tale == null)
                return
            binding.taleSettingCardView.setOnClickListener {
                val wrapper: Context =
                    ContextThemeWrapper(MainActivity.instance, R.style.PopUpMenu)
                val popupMenu = PopupMenu(wrapper, it)
                popupMenu.menuInflater.inflate(R.menu.saved_tale_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.saved_tale_menu_item_edit -> {
                            val bundle = Bundle()
                            bundle.putString("id", tale.id)
                            bundle.putString("name", tale.title)
                            renamingDialog.arguments = bundle
                            renamingDialog.setOnResponseCallback { newName ->
                                tale.title = newName
                                notifyItemChanged(position)
                                Toast.makeText(
                                    MainActivity.instance,
                                    R.string.tale_has_been_renamed,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            renamingDialog.show(
                                MainActivity.instance.supportFragmentManager,
                                "RenamingDialog"
                            )
                        }
                        R.id.saved_tale_menu_item_delete ->
                            SavedTalesPreferences.deleteSavedId(tale.id)
                    }
                    true
                }
                popupMenu.show()
            }
            binding.taleSettingCardView.visibility = View.VISIBLE
        }
    }

    inner class NewTaleViewHolder(private val binding: NewTaleViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.newTaleButtonBigImage.setOnClickListener {
                MainActivity.instance.navController.navigate(R.id.action_mainScreenFragment_to_creationScreenFragment)
            }
        }
    }

    inner class ListWithHeaderUpdateCallback(private val adapterCallback: AdapterListUpdateCallback) :
        ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {
            adapterCallback.onInserted(position + 1, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            adapterCallback.onRemoved(position + 1, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapterCallback.onMoved(fromPosition + 1, toPosition + 1)
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapterCallback.onChanged(position + 1, count, payload)
        }
    }
}
