package com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.shared

import androidx.databinding.ObservableField
import com.akvelon.grimmuzzle.data.DataNode
import com.akvelon.grimmuzzle.data.entities.Tale
import com.akvelon.grimmuzzle.data.entities.event.Event
import com.akvelon.grimmuzzle.ui.MainActivity
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common.TalesDataSource

class SharedTalesDataSource(
    override val dataNode: DataNode
) : TalesDataSource(dataNode) {

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Tale>) {
        val result = mutableListOf<Tale>()
        val talesObservable = ObservableField<Event<MutableList<Tale>>>()

        try {
            dataNode.getSharedTales(
                params.requestedStartPosition,
                params.requestedLoadSize,
                talesObservable,
                false,
                forceNetwork = true
            )
            talesObservable.get()!!.data!!.let { result.addAll(it) }
            callback.onResult(result, params.requestedStartPosition)
        } catch (e: Throwable) {
            MainActivity.instance.onErrorForCurrentFragment(SharedTalesListFragment::class.java, e)
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Tale>) {
        val result = mutableListOf<Tale>()
        val talesObservable = ObservableField<Event<MutableList<Tale>>>()

        try {
            dataNode.getSharedTales(
                params.startPosition, params.loadSize, talesObservable, false,
                forceNetwork = true
            )
            talesObservable.get()!!.data!!.let { result.addAll(it) }
            callback.onResult(result)
        } catch (e: Throwable) {
            MainActivity.instance.onErrorForCurrentFragment(SharedTalesListFragment::class.java, e)
        }
    }
}
