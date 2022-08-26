package com.akvelon.grimmuzzle.di

import androidx.paging.PagedList
import com.akvelon.grimmuzzle.GrimmuzzleApplication
import com.akvelon.grimmuzzle.data.DataNode
import com.akvelon.grimmuzzle.data.api.GrimmuzzleAPI
import com.akvelon.grimmuzzle.di.talesmodule.TalesRecyclerModule
import com.akvelon.grimmuzzle.ui.dialogs.RenamingDialogViewModel
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common.TalesDiffCallback
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common.TalesListViewModel
import com.akvelon.grimmuzzle.ui.screens.creationscreen.CreationScreenViewModel
import com.akvelon.grimmuzzle.ui.screens.mainscreen.MainScreenViewModel
import com.akvelon.grimmuzzle.ui.screens.readingscreen.ReadingScreenViewModel
import dagger.Component
import java.util.concurrent.ExecutorService
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, GrimmuzzleAPIModule::class, TalesRecyclerModule::class, CreationScreenRecyclerModule::class, DialogModule::class, DataNodeModule::class])
interface MainComponent {
    fun inject(obj: GrimmuzzleApplication)
    fun inject(obj: ReadingScreenViewModel)
    fun inject(obj: CreationScreenViewModel)
    fun inject(obj: TalesListViewModel)
    fun inject(obj: RenamingDialogViewModel)
    fun inject(obj: MainScreenViewModel)

    fun getGrimmuzzleAPI(): GrimmuzzleAPI

    fun getDataNode(): DataNode

    fun getPagedListConfig(): PagedList.Config

    fun getTalesDiffCallback(): TalesDiffCallback

    fun getExecutorService(): ExecutorService
}
