package com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.shared

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.akvelon.grimmuzzle.data.entities.Tale
import java.util.concurrent.ExecutorService

class SharedTalesPagedListFactory(
    private val pagedListConfig: PagedList.Config,
    private val sharedTalesDataSourceFactory: SharedTalesDataSourceFactory,
    private val executorService: ExecutorService
) {

    fun create(): LiveData<PagedList<Tale>> {
        return LivePagedListBuilder(
            sharedTalesDataSourceFactory,
            pagedListConfig
        ).setFetchExecutor(executorService).build()
    }
}