package com.akvelon.grimmuzzle.di.talesmodule

import androidx.paging.PagedList
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common.TalesDiffCallback
import dagger.Module
import dagger.Provides
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Module
class TalesRecyclerModule {

    @Provides
    fun getDiffCallback(): TalesDiffCallback {
        return TalesDiffCallback()
    }

    @Provides
    fun getPagedListConfig(): PagedList.Config {
        return PagedList.Config.Builder().setInitialLoadSizeHint(6).setEnablePlaceholders(false)
            .setPageSize(4).build()
    }

    @Provides
    fun getExecutorService(): ExecutorService {
        return Executors.newSingleThreadExecutor()
    }
}
