package com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.saved

import androidx.databinding.Observable
import androidx.databinding.ObservableField
import com.akvelon.grimmuzzle.data.DataNode
import com.akvelon.grimmuzzle.data.entities.Tale
import com.akvelon.grimmuzzle.data.entities.event.Event
import com.akvelon.grimmuzzle.data.entities.event.EventProcessor
import com.akvelon.grimmuzzle.data.entities.event.OnFieldChangedWithEventCallback
import com.akvelon.grimmuzzle.data.preferences.SavedTalesPreferences
import com.akvelon.grimmuzzle.ui.MainActivity
import com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common.TalesDataSource

class SavedTalesDataSource(
    dataNode: DataNode
) : TalesDataSource(dataNode), EventProcessor<Tale> {
    private var ids: MutableList<String>
    private val loadedIds = mutableListOf<String>()

    init {
        SavedTalesPreferences.updateSavedIds()
        ids = SavedTalesPreferences.savedIds.get()!!
        val savedListChangedCallback = object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                SavedTalesPreferences.savedIds.removeOnPropertyChangedCallback(this)
                invalidate()
            }
        }
        SavedTalesPreferences.savedIds.addOnPropertyChangedCallback(savedListChangedCallback)
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Tale>) {
        val result = mutableListOf<Tale>()

        try {
            for (i in params.requestedStartPosition until params.requestedStartPosition + params.requestedLoadSize) {
                if (i in ids.indices) {
                    val taleObservable = ObservableField<Event<Tale>>()
                    taleObservable.addOnPropertyChangedCallback(OnFieldChangedWithEventCallback(this))
                    dataNode.getTaleById(ids[i], taleObservable, false)
                    taleObservable.get()?.data?.let { result.add(it) }
                }
            }
            callback.onResult(result, params.requestedStartPosition)
        } catch (e: Throwable) {
            MainActivity.instance.onErrorForCurrentFragment(SavedTalesListFragment::class.java, e)
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Tale>) {
        val result = mutableListOf<Tale>()

        try {
            for (i in params.startPosition until params.startPosition + params.loadSize) {
                if (i in ids.indices) {
                    val taleObservable = ObservableField<Event<Tale>>()
                    taleObservable.addOnPropertyChangedCallback(OnFieldChangedWithEventCallback(this))
                    dataNode.getTaleById(ids[i], taleObservable, false)
                    taleObservable.get()?.data?.let { result.add(it) }
                }
            }
            callback.onResult(result)
        } catch (e: Throwable) {
            MainActivity.instance.onErrorForCurrentFragment(SavedTalesListFragment::class.java, e)
        }
    }

    override fun onLoad() {
    }

    override fun onSuccess(result: Tale?) {
        result?.let {
            for (id in loadedIds) {
                if (it.id == id) {
                    return
                }
            }
            loadedIds.add(it.id)
        }
    }

    override fun onError(t: Throwable?) {
    }
}
