package com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.saved

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.akvelon.grimmuzzle.GrimmuzzleApplication
import com.akvelon.grimmuzzle.data.entities.Tale
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common.TalesListViewModel
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common.TalesPagedListAdapter
import javax.inject.Inject

class SavedTalesListViewModel : TalesListViewModel() {
    @Inject
    lateinit var adapter: SavedTalesPagedListAdapter
    @Inject
    lateinit var talesList: LiveData<PagedList<Tale>>

    init {
        GrimmuzzleApplication.instance.injector.inject(this)
        GrimmuzzleApplication.instance.savedTalesListInjector.inject(this)
    }

    override fun getAdapter(): TalesPagedListAdapter {
        return adapter
    }

    override fun getList(): LiveData<PagedList<Tale>> {
        return talesList
    }


}