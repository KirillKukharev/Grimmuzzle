package com.akvelon.grimmuzzle.di.talesmodule.shared

import com.akvelon.grimmuzzle.di.MainComponent
import com.akvelon.grimmuzzle.di.scopes.SharedTalesListScope
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.shared.SharedTalesListViewModel
import dagger.Component

@SharedTalesListScope
@Component(modules = [SharedTalesListModule::class], dependencies = [MainComponent::class])
interface SharedTalesListComponent {
    fun inject(obj: SharedTalesListViewModel)
}