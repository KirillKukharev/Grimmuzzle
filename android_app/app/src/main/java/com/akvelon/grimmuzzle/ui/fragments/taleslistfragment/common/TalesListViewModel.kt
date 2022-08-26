package com.akvelon.grimmuzzle.ui.fragments.taleslistfragment.common

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.akvelon.grimmuzzle.GrimmuzzleApplication
import com.akvelon.grimmuzzle.data.DataNode
import com.akvelon.grimmuzzle.data.entities.Attribute
import com.akvelon.grimmuzzle.data.entities.Tale
import com.akvelon.grimmuzzle.data.entities.event.Event
import com.akvelon.grimmuzzle.data.entities.event.EventProcessor
import com.akvelon.grimmuzzle.data.entities.event.LiveDataWithEventObserver
import com.akvelon.grimmuzzle.data.entities.event.Status
import javax.inject.Inject

abstract class TalesListViewModel : ViewModel() {

    @Inject
    lateinit var dataNode: DataNode

    init {
        GrimmuzzleApplication.instance.injector.inject(this)
    }

    abstract fun getAdapter(): TalesPagedListAdapter

    abstract fun getList(): LiveData<PagedList<Tale>>

    fun initData(
        lifecycleOwner: LifecycleOwner,
        eventProcessor: EventProcessor<MutableList<Attribute>>
    ) {
        val attributesByTypeEvent =
            MutableLiveData<Event<MutableList<Attribute>>>()
        attributesByTypeEvent.observe(
            lifecycleOwner,
            object :
                LiveDataWithEventObserver<MutableList<Attribute>>(eventProcessor) {
                override fun onChanged(event: Event<MutableList<Attribute>>) {
                    when (event.status) {
                        Status.SUCCESS -> {
                            event.data?.let { data ->
                                getAdapter().attributesByType = data
                                getList().observe(lifecycleOwner, Observer {
                                    getAdapter().submitList(it)
                                })
                                getList().value?.dataSource?.invalidate()
                            }
                        }
                        else -> {
                        }
                    }
                    super.onChanged(event)
                }
            })
        dataNode.getAttributes(attributesByTypeEvent, lifecycleOwner)
    }
}