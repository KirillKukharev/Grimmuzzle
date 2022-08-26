package com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.shared

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.akvelon.grimmuzzle.GrimmuzzleApplication
import com.akvelon.grimmuzzle.data.entities.Tale
import com.akvelon.grimmuzzle.data.entities.event.EventProcessor
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common.TalesListViewModel
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common.TalesPagedListAdapter
import javax.inject.Inject

class SharedTalesListViewModel : TalesListViewModel() {
    @Inject
    lateinit var adapter: SharedTalesPagedListAdapter

    @Inject
    lateinit var pagedListFactory: SharedTalesPagedListFactory

    var talesList: LiveData<PagedList<Tale>>

    init {
        GrimmuzzleApplication.instance.injector.inject(this)
        GrimmuzzleApplication.instance.sharedTalesListInjector.inject(this)
        talesList = pagedListFactory.create()
    }

    override fun getAdapter(): TalesPagedListAdapter {
        return adapter
    }

    override fun getList(): LiveData<PagedList<Tale>> {
        return talesList
    }

    fun setNewPagedTalesList(lifecycleOwner: LifecycleOwner, eventProcessor: EventProcessor<*>) {
        eventProcessor.onLoad()
        getAdapter().submitList(null) {
            talesList = pagedListFactory.create()
            talesList.observe(lifecycleOwner, Observer {
                getAdapter().submitList(it) { eventProcessor.onSuccess() }
            })
        }
    }
}