package com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.saved

import androidx.paging.DataSource
import com.akvelon.grimmuzzle.data.DataNode
import com.akvelon.grimmuzzle.data.entities.Tale
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common.TalesDataSourceFactory

class SavedTalesDataSourceFactory(
    override val dataNode: DataNode
) : TalesDataSourceFactory(dataNode) {

    override fun create(): DataSource<Int, Tale> {
        return SavedTalesDataSource(
            dataNode
        )
    }
}