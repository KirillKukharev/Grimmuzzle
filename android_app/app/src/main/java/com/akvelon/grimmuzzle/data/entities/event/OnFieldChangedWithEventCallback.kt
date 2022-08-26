package com.akvelon.grimmuzzle.data.entities.event

import androidx.databinding.Observable
import androidx.databinding.ObservableField

open class OnFieldChangedWithEventCallback<T>(private val processor: EventProcessor<T>) :
    Observable.OnPropertyChangedCallback() {
    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
        val event = (sender as ObservableField<*>).get() as Event<*>?
        event?.let {
            when (it.status) {
                Status.LOADING -> {
                    processor.onLoad()
                }
                Status.ERROR -> {
                    processor.onError(it.t!!)
                }
                Status.SUCCESS -> {
                    processor.onSuccess(event.data as T)
                    processor.onSuccess()
                }
            }
        }
    }
}