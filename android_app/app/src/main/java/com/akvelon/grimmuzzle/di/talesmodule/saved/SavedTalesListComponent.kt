package com.akvelon.grimmuzzle.di.talesmodule.saved

import com.akvelon.grimmuzzle.di.MainComponent
import com.akvelon.grimmuzzle.di.scopes.SavedTalesListScope
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.saved.SavedTalesListViewModel
import dagger.Component

@SavedTalesListScope
@Component(modules = [SavedTalesListModule::class], dependencies = [MainComponent::class])
interface SavedTalesListComponent {
    fun inject(obj: SavedTalesListViewModel)
}