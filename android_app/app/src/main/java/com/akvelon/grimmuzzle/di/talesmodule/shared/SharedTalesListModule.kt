package com.akvelon.grimmuzzle.di.talesmodule.shared

import androidx.paging.PagedList
import com.akvelon.grimmuzzle.data.DataNode
import com.akvelon.grimmuzzle.di.scopes.SharedTalesListScope
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common.TalesDiffCallback
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.shared.SharedTalesDataSourceFactory
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.shared.SharedTalesPagedListAdapter
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.shared.SharedTalesPagedListFactory
import dagger.Module
import dagger.Provides
import java.util.concurrent.ExecutorService

@Module
class SharedTalesListModule {

    @Provides
    @SharedTalesListScope
    fun getSharedTalesDataSourceFactory(dataNode: DataNode): SharedTalesDataSourceFactory {
        return SharedTalesDataSourceFactory(dataNode)
    }

    @Provides
    @SharedTalesListScope
    fun getSharedTalesPagedListFactory(
        pagedListConfig: PagedList.Config,
        sharedTalesDataSourceFactory: SharedTalesDataSourceFactory,
        executorService: ExecutorService
    ): SharedTalesPagedListFactory {
        return SharedTalesPagedListFactory(
            pagedListConfig,
            sharedTalesDataSourceFactory,
            executorService
        )
    }

    @Provides
    @SharedTalesListScope
    fun getSharedTalesPagedListAdapter(talesDiffCallback: TalesDiffCallback): SharedTalesPagedListAdapter {
        return SharedTalesPagedListAdapter(talesDiffCallback)
    }
}