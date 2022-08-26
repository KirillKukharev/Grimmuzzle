package com.akvelon.grimmuzzle.di

import com.akvelon.grimmuzzle.data.DataNode
import com.akvelon.grimmuzzle.data.api.GrimmuzzleAPI
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataNodeModule {

    @Provides
    @Singleton
    fun getDataNode(api: GrimmuzzleAPI): DataNode {
        return DataNode(api)
    }
}