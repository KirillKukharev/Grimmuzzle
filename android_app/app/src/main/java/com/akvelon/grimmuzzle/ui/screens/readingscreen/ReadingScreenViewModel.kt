package com.akvelon.grimmuzzle.ui.screens.readingscreen

import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.akvelon.grimmuzzle.GrimmuzzleApplication
import com.akvelon.grimmuzzle.R
import com.akvelon.grimmuzzle.data.DataNode
import com.akvelon.grimmuzzle.data.api.GrimmuzzleAPI
import com.akvelon.grimmuzzle.data.entities.Attribute
import com.akvelon.grimmuzzle.data.entities.Tale
import com.akvelon.grimmuzzle.data.entities.event.Event
import com.akvelon.grimmuzzle.data.entities.event.EventProcessor
import com.akvelon.grimmuzzle.data.entities.event.LiveDataWithEventObserver
import com.akvelon.grimmuzzle.data.entities.event.Status
import com.akvelon.grimmuzzle.ui.MainActivity
import com.akvelon.grimmuzzle.ui.dialogs.RenamingDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ReadingScreenViewModel : ViewModel() {

    val tale = ObservableField<Tale>()
    private val taleEvent: MutableLiveData<Event<Tale>> = MutableLiveData()
    var backgroundImageURL = ObservableField<String>()
    var textChunksList: MutableList<String> = mutableListOf()
    var currTextChunkIndex = ObservableField(0)

    @Inject
    lateinit var grimmuzzleApi: GrimmuzzleAPI

    @Inject
    lateinit var renamingDialog: RenamingDialogFragment

    @Inject
    lateinit var dataNode: DataNode

    init {
        GrimmuzzleApplication.instance.injector.inject(this)
    }

    private fun getAttributesLiveData(
        lifecycleOwner: LifecycleOwner,
        eventProcessor: EventProcessor<Any>
    ): MutableLiveData<Event<MutableList<Attribute>>> {
        val liveDataAttributes =
            MutableLiveData<Event<MutableList<Attribute>>>()
        liveDataAttributes.observe(
            lifecycleOwner,
            object : LiveDataWithEventObserver<Any>(eventProcessor) {
                override fun onChanged(event: Event<Any>) {
                    when (event.status) {
                        Status.SUCCESS -> event.data.let {
                            if (it is MutableList<*>) {
                                val attributesWhere = it.filter { attr ->
                                    (attr as Attribute).getType().getLabel() == "Where"
                                } as MutableList<Attribute>
                                if (tale.get() != null) {
                                    backgroundImageURL.set(attributesWhere.find { attribute ->
                                        attribute.getID() == tale.get()!!.input.inputMap["Where"]!![0]
                                    }?.getFullHDImgURL())
                                    currTextChunkIndex.notifyChange()
                                    super.onChanged(event)
                                }
                            }
                        }
                        else -> super.onChanged(event)
                    }
                }
            })
        dataNode.getAttributes(liveDataAttributes, lifecycleOwner)
        return liveDataAttributes
    }

    fun createTale(
        title: String,
        input: HashMap<String, List<Int>>,
        length: Int,
        lifecycleOwner: LifecycleOwner,
        eventProcessor: EventProcessor<Any>
    ) {
        val liveDataAttributes =
            getAttributesLiveData(
                lifecycleOwner,
                eventProcessor
            )
        taleEvent.observe(lifecycleOwner, object : LiveDataWithEventObserver<Any>(eventProcessor) {
            override fun onChanged(event: Event<Any>) {
                when (event.status) {
                    Status.SUCCESS -> {
                        if (event.data == null) {
                            Toast.makeText(
                                MainActivity.instance,
                                MainActivity.instance.getString(R.string.toast_getting_story_from_server_failed),
                                Toast.LENGTH_LONG
                            ).show()
                            return
                        } else {
                            event.data.let { data ->
                                val tale = data as Tale
                                this@ReadingScreenViewModel.tale.set(tale)
                                liveDataAttributes.value?.let {
                                    if (it.status == Status.SUCCESS) {
                                        val attributesWhere = it.data!!.filter { attr ->
                                            attr.getType().getLabel() == "Where"
                                        }
                                        backgroundImageURL.set(attributesWhere.find { attribute ->
                                            attribute.getID() == tale.input.inputMap["Where"]!![0]
                                        }?.getFullHDImgURL())
                                        currTextChunkIndex.notifyChange()
                                        super.onChanged(event)
                                    }
                                }
                            }
                        }
                    }
                    else -> super.onChanged(event)
                }
            }
        })
        dataNode.createTale(title, input, length, taleEvent)
    }

    fun loadExistingTale(
        id: String,
        lifecycleOwner: LifecycleOwner,
        eventProcessor: EventProcessor<Any>
    ) {
        val liveDataAttributes =
            getAttributesLiveData(
                lifecycleOwner,
                eventProcessor
            )
        taleEvent.observe(lifecycleOwner, object : LiveDataWithEventObserver<Any>(eventProcessor) {
            override fun onChanged(event: Event<Any>) {
                when (event.status) {
                    Status.SUCCESS -> event.data?.let { data ->
                        val tale = data as Tale
                        this@ReadingScreenViewModel.tale.set(tale)
                        liveDataAttributes.value?.let {
                            if (it.status == Status.SUCCESS) {
                                val attributesWhere = it.data!!.filter { attr ->
                                    attr.getType().getLabel() == "Where"
                                }
                                backgroundImageURL.set(attributesWhere.find { attribute ->
                                    attribute.getID() == tale.input.inputMap["Where"]!![0]
                                }?.getFullHDImgURL())
                                currTextChunkIndex.notifyChange()
                                super.onChanged(event)
                            }
                        }
                    }
                    else -> super.onChanged(event)
                }
            }
        })
        dataNode.getTaleById(id, taleEvent)
    }

    fun shareTale(
        id: String,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) {
        val request = grimmuzzleApi.shareTaleById(id)
        request.enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                onFailure()
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                onSuccess()
            }
        })
    }

    // When press back page button
    fun leafPageBack() {
        if (currTextChunkIndex.get()!! > 0) currTextChunkIndex.set(
            currTextChunkIndex.get()?.minus(1)
        )
    }

    // When press forward page button
    fun leafPageForward() {
        if (currTextChunkIndex.get()!! < textChunksList.lastIndex) currTextChunkIndex.set(
            currTextChunkIndex.get()?.plus(1)
        )
    }
}