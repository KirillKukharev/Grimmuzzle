package com.akvelon.grimmuzzle.di

import com.akvelon.grimmuzzle.ui.screens.creationscreen.adapters.AttributeAdapter
import com.akvelon.grimmuzzle.ui.screens.creationscreen.adapters.AttributePagesAdapter
import dagger.Module
import dagger.Provides

@Module
class CreationScreenRecyclerModule()
{
    @Provides
    fun getAttributeAdapter(): AttributeAdapter {
        return AttributeAdapter()
    }

    @Provides
    fun getAttributePagesAdapter(): AttributePagesAdapter {
        return AttributePagesAdapter()
    }
}