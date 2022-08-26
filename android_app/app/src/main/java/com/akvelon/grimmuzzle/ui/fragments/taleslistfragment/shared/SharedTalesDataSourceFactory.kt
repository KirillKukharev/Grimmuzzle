package com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.shared

import androidx.paging.DataSource
import com.akvelon.grimmuzzle.data.DataNode
import com.akvelon.grimmuzzle.data.entities.Tale
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common.TalesDataSourceFactory

class SharedTalesDataSourceFactory(
    override val dataNode: DataNode
) : TalesDataSourceFactory(dataNode) {

    override fun create(): DataSource<Int, Tale> {
        return SharedTalesDataSource(
            dataNode
        )
    }
}