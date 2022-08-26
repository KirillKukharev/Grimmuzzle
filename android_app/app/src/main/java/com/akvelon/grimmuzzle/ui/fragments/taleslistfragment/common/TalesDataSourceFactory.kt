package com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common

import androidx.paging.DataSource
import com.akvelon.grimmuzzle.data.DataNode
import com.akvelon.grimmuzzle.data.entities.Tale

abstract class TalesDataSourceFactory(
    protected open val dataNode: DataNode
) : DataSource.Factory<Int, Tale>()
