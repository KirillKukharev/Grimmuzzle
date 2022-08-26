package com.akvelon.grimmuzzle.data.entities.event

interface EventProcessor<T> {
    fun onLoad() {

    }

    fun onSuccess(result: T?) {

    }

    fun onSuccess() {

    }

    fun onError(t: Throwable?) {

    }
}