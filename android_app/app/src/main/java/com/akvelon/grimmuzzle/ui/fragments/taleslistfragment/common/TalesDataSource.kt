package com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common

import androidx.paging.PositionalDataSource
import com.akvelon.grimmuzzle.data.DataNode
import com.akvelon.grimmuzzle.data.entities.Tale

abstract class TalesDataSource(
    protected open val dataNode: DataNode
) : PositionalDataSource<Tale>()