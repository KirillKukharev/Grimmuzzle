package com.akvelon.grimmuzzle.data.entities.event

import androidx.lifecycle.Observer


open class LiveDataWithEventObserver<T>(
    private val processor: EventProcessor<T>
) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>) {
        when (event.status) {
            Status.SUCCESS -> {
                processor.onSuccess(event.data)
                processor.onSuccess()
            }
            Status.ERROR -> {
                processor.onError(event.t)
            }
            Status.LOADING -> {
                processor.onLoad()
            }
        }
    }
}