package com.akvelon.grimmuzzle.ui.screens.creationscreen

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.akvelon.grimmuzzle.GrimmuzzleApplication
import com.akvelon.grimmuzzle.data.DataNode
import com.akvelon.grimmuzzle.data.entities.Attribute
import com.akvelon.grimmuzzle.data.entities.event.Event
import com.akvelon.grimmuzzle.data.entities.event.EventProcessor
import com.akvelon.grimmuzzle.data.entities.event.LiveDataWithEventObserver
import com.akvelon.grimmuzzle.ui.screens.creationscreen.adapters.AttributePagesAdapter
import javax.inject.Inject


class CreationScreenViewModel : ViewModel() {
    @Inject
    lateinit var attributesPagesAdapter: AttributePagesAdapter

    @Inject
    lateinit var dataNode: DataNode

    init {
        GrimmuzzleApplication.instance.injector.inject(this)
    }

    fun initAttributesByType(
        lifecycleOwner: LifecycleOwner,
        eventProcessor: EventProcessor<MutableList<Attribute>>
    ) {
        val attributesEvent =
            MutableLiveData<Event<MutableList<Attribute>>>()
        attributesEvent.observe(
            lifecycleOwner,
            LiveDataWithEventObserver(eventProcessor)
        )
        dataNode.getAttributes(attributesEvent, lifecycleOwner)
    }
}


