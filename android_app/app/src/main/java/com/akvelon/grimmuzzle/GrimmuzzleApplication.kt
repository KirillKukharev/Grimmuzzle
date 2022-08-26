package com.akvelon.grimmuzzle

import android.app.Application
import com.akvelon.grimmuzzle.di.*
import com.akvelon.grimmuzzle.di.talesmodule.saved.DaggerSavedTalesListComponent
import com.akvelon.grimmuzzle.di.talesmodule.saved.SavedTalesListComponent
import com.akvelon.grimmuzzle.di.talesmodule.shared.DaggerSharedTalesListComponent
import com.akvelon.grimmuzzle.di.talesmodule.shared.SharedTalesListComponent

class GrimmuzzleApplication : Application() {
    companion object {
        lateinit var instance: GrimmuzzleApplication
    }

    lateinit var injector: MainComponent
    lateinit var savedTalesListInjector: SavedTalesListComponent
    lateinit var sharedTalesListInjector: SharedTalesListComponent

    override fun onCreate() {
        super.onCreate()
        instance = this
        injector = DaggerMainComponent.create()
        savedTalesListInjector =
            DaggerSavedTalesListComponent.builder().mainComponent(injector).build()
        sharedTalesListInjector =
            DaggerSharedTalesListComponent.builder().mainComponent(injector).build()
    }
}