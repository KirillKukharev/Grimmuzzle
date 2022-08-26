package com.akvelon.grimmuzzle.ui.dialogs

import androidx.databinding.ObservableField
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.akvelon.grimmuzzle.GrimmuzzleApplication
import com.akvelon.grimmuzzle.data.DataNode
import com.akvelon.grimmuzzle.data.entities.event.Event
import com.akvelon.grimmuzzle.data.entities.event.EventProcessor
import com.akvelon.grimmuzzle.data.entities.event.LiveDataWithEventObserver
import javax.inject.Inject

class RenamingDialogViewModel : ViewModel() {

    val taleName = ObservableField<String>("Name")

    @Inject
    lateinit var dataNode: DataNode

    init {
        GrimmuzzleApplication.instance.injector.inject(this)
    }

    fun renameTale(
        id: String,
        name: String,
        lifecycleOwner: LifecycleOwner,
        eventProcessor: EventProcessor<String>
    ) {
        val liveData = MutableLiveData<Event<String>>()
        liveData.observe(lifecycleOwner, LiveDataWithEventObserver<String>(eventProcessor))
        dataNode.renameTale(id, name, liveData)
    }
}