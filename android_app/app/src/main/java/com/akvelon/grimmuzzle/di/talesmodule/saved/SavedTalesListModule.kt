package com.akvelon.grimmuzzle.di.talesmodule.saved

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.akvelon.grimmuzzle.data.DataNode
import com.akvelon.grimmuzzle.data.entities.Tale
import com.akvelon.grimmuzzle.di.scopes.SavedTalesListScope
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common.TalesDiffCallback
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.saved.SavedTalesDataSourceFactory
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.saved.SavedTalesPagedListAdapter
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executors

@Module
class SavedTalesListModule {

    @Provides
    @SavedTalesListScope
    fun getSavedTalesDataSourceFactory(dataNode: DataNode): SavedTalesDataSourceFactory {
        return SavedTalesDataSourceFactory(dataNode)
    }

    @Provides
    @SavedTalesListScope
    fun getSavedTalesPagedList(
        savedTalesDataSourceFactory: SavedTalesDataSourceFactory,
        pagedListConfig: PagedList.Config
    ): LiveData<PagedList<Tale>> {
        return LivePagedListBuilder(
            savedTalesDataSourceFactory,
            pagedListConfig
        ).setFetchExecutor(Executors.newSingleThreadExecutor()).build()
    }

    @Provides
    @SavedTalesListScope
    fun getSavedTalesPagedListAdapter(
        talesDiffCallback: TalesDiffCallback
    ): SavedTalesPagedListAdapter {
        return SavedTalesPagedListAdapter(talesDiffCallback)
    }
}