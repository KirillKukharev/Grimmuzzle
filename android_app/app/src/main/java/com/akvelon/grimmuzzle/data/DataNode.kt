package com.akvelon.grimmuzzle.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.akvelon.grimmuzzle.data.api.GrimmuzzleAPI
import com.akvelon.grimmuzzle.data.entities.Attribute
import com.akvelon.grimmuzzle.data.entities.Tale
import com.akvelon.grimmuzzle.data.entities.dto.AttributesByTypeDTO
import com.akvelon.grimmuzzle.data.entities.dto.TaleForRenamingDTO
import com.akvelon.grimmuzzle.data.entities.dto.TaleInputDTO
import com.akvelon.grimmuzzle.data.entities.event.Event
import com.akvelon.grimmuzzle.ui.MainActivity
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DataNode(val api: GrimmuzzleAPI) {

    private val maxStale = 60 * 60 * 24 * 7
    private val maxAge = 600
    private val attributes = ObservableField<MutableList<Attribute>>()

    val pendingTalesUpdates = mutableListOf<Tale>()

    companion object {
        fun hasNetwork(): Boolean {
            val connectivityManager =
                MainActivity.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                connectivityManager.run {
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                        ?.run {
                            return when {
                                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                                else -> false
                            }
                        }
                }
            } else {
                connectivityManager.run {
                    connectivityManager.activeNetworkInfo?.run {
                        if (type == ConnectivityManager.TYPE_WIFI) {
                            return true
                        } else if (type == ConnectivityManager.TYPE_MOBILE) {
                            return true
                        }
                    }
                }
            }
            return false
        }
    }

    fun getTaleById(
        id: String,
        liveData: MutableLiveData<Event<Tale>>,
        forceNetwork: Boolean = false
    ) {
        var useNetwork = forceNetwork
        val tale = pendingTalesUpdates.find { tale -> tale.id == id }
        tale?.let {
            useNetwork = true
        }
        val request = api.getTaleById(getHeader(useNetwork), id)
        liveData.value = Event.loading()
        request.enqueue(object : Callback<Tale> {
            override fun onResponse(call: Call<Tale>, response: Response<Tale>) {
                if (response.body() != null) {
                    liveData.value = Event.success(response.body())
                    tale?.let { pendingTalesUpdates.remove(it) }
                } else {
                    liveData.value = Event.error(Throwable(NullPointerException()))
                }
            }

            override fun onFailure(call: Call<Tale>, t: Throwable) {
                liveData.value = Event.error(t)
            }
        })
    }

    fun getTaleById(
        id: String,
        taleObservable: ObservableField<Event<Tale>>,
        async: Boolean = true,
        forceNetwork: Boolean = false
    ) {
        var useNetwork = forceNetwork
        val tale = pendingTalesUpdates.find { tale -> tale.id == id }
        tale?.let {
            useNetwork = true
        }
        taleObservable.set(Event.loading())
        val request = api.getTaleById(getHeader(useNetwork), id)
        if (async) {
            request.enqueue(object : Callback<Tale> {
                override fun onResponse(call: Call<Tale>, response: Response<Tale>) {
                    if (response.body() != null) {
                        taleObservable.set(Event.success(response.body()))
                        tale?.let { pendingTalesUpdates.remove(it) }
                    } else {
                        taleObservable.set(Event.error(Throwable(NullPointerException())))
                    }
                }

                override fun onFailure(call: Call<Tale>, t: Throwable) {
                    taleObservable.set(Event.error(t))
                }
            })
        } else {
            try {
                val respondedTale = api.getTaleById(getHeader(useNetwork), id).execute().body()
                if (respondedTale != null) {
                    taleObservable.set(Event.success(respondedTale))
                    tale?.let { pendingTalesUpdates.remove(it) }
                } else {
                    taleObservable.set(Event.error(Throwable(java.lang.NullPointerException())))
                }
            } catch (t: Throwable) {
                taleObservable.set(Event.error(t))
            }
        }
    }

    fun createTale(
        title: String,
        input: HashMap<String, List<Int>>,
        length: Int,
        liveData: MutableLiveData<Event<Tale>>
    ) {
        liveData.value = Event.loading()
        val request = api.createTale(TaleInputDTO(title, length, input))
        request.enqueue(object : Callback<Tale> {
            override fun onResponse(call: Call<Tale>, response: Response<Tale>) {
                liveData.value = Event.success(response.body())
            }

            override fun onFailure(call: Call<Tale>, t: Throwable) {
                liveData.value = Event.error(t)
            }
        })
    }

    fun getSharedTales(
        startIndex: Int,
        count: Int,
        talesEventObservable: ObservableField<Event<MutableList<Tale>>>,
        async: Boolean = true,
        forceNetwork: Boolean = false
    ) {
        talesEventObservable.set(Event.loading())
        val request = api.getSharedTalesPreviewByRange(getHeader(forceNetwork), startIndex, count)
        if (async) {
            request.enqueue(object : Callback<List<Tale>> {
                override fun onResponse(
                    call: Call<List<Tale>>,
                    response: Response<List<Tale>>
                ) {
                    if (response.body() != null) {
                        talesEventObservable.set(Event.success(response.body()!!.toMutableList()))
                    } else {
                        talesEventObservable.set(Event.error(Throwable(NullPointerException())))
                    }
                }

                override fun onFailure(call: Call<List<Tale>>, t: Throwable) {
                    talesEventObservable.set(Event.error(t))
                }
            })
        } else {
            try {
                val respondedTalesList =
                    api.getSharedTalesPreviewByRange(getHeader(forceNetwork), startIndex, count)
                        .execute().body()
                if (respondedTalesList != null) {
                    talesEventObservable.set(Event.success(respondedTalesList.toMutableList()))
                } else {
                    talesEventObservable.set(Event.error(Throwable(NullPointerException())))
                }
            } catch (t: Throwable) {
                talesEventObservable.set(Event.error(t))
            }
        }
    }

    fun getAttributes(
        liveData: MutableLiveData<Event<MutableList<Attribute>>>,
        lifecycleOwner: LifecycleOwner?,
        forceNetwork: Boolean = false
    ) {
        if (attributes.get() != null) {
            liveData.value = Event.success(attributes.get())
        } else {
            liveData.value = Event.loading()
        }
        val onPropertyChangedCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (lifecycleOwner != null) {
                    when (lifecycleOwner.lifecycle.currentState) {
                        Lifecycle.State.DESTROYED -> attributes.removeOnPropertyChangedCallback(this)
                        else -> {
                            if (attributes.get() != null) {
                                liveData.value = Event.success(attributes.get())
                            } else {
                                liveData.value = Event.error(NullPointerException())
                            }
                        }
                    }
                } else {
                    if (attributes.get() != null) {
                        liveData.value = Event.success(attributes.get())
                    } else {
                        liveData.value = Event.error(NullPointerException())
                    }
                }
            }
        }
        attributes.addOnPropertyChangedCallback(onPropertyChangedCallback)
        val request = api.getAllAttributes(getHeader(forceNetwork))
        request.enqueue(object : Callback<Map<String, AttributesByTypeDTO>> {
            override fun onResponse(
                call: Call<Map<String, AttributesByTypeDTO>>,
                response: Response<Map<String, AttributesByTypeDTO>>
            ) {
                val result : MutableList<Attribute> = mutableListOf()
                if (response.body() == null) {
                    liveData.value = Event.error(NullPointerException())
                }
                response.body()?.forEach { (key, value) ->
                    result.addAll(value.convertFromDTO(key))
                }
                if (attributes.get() != result) {
                    var picCount = 0
                    var picLoaded = 0
                    val onAllImagesLoaded = object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            if (picCount == ++picLoaded) {
                                attributes.set(result)
                            }
                        }

                        override fun onError(e: Exception?) {
                            liveData.value = Event.error(e)
                        }

                    }
                    for (attribute in result) {
                            attribute.getFullHDImgURL()?.let { url ->
                                picCount++
                                Picasso.get().load(url).fetch(onAllImagesLoaded)
                            }
                            attribute.getImgURL().let {url ->
                                picCount++
                                Picasso.get().load(url).fetch(onAllImagesLoaded)
                        }
                    }
                }
            }

            override fun onFailure(
                call: Call<Map<String, AttributesByTypeDTO>>,
                t: Throwable
            ) {
                liveData.value = Event.error(t)
            }
        })
    }

    fun renameTale(
        id: String,
        name: String,
        liveData: MutableLiveData<Event<String>>
    ) {
        val request = api.renameTale(TaleForRenamingDTO(id, name))
        liveData.value = Event.loading()
        request.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                liveData.value = Event.success(name)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                liveData.value = Event.error(t)
            }
        })
    }

    private fun getHeader(networkForce: Boolean): String {
        return if (hasNetwork()) {
            if (networkForce) {
                "no-cache"
            } else {
                "public, max-age=$maxAge"
            }
        } else {
            "public, only-if-cached, max-stale=$maxStale"
        }
    }

    fun forceUpdateTaleOnNextCall(tale: Tale) {
        val taleFromList = pendingTalesUpdates.find { it.id == tale.id }
        if (taleFromList == null) {
            pendingTalesUpdates.add(tale)
        } else {
            taleFromList.update(tale)
        }
    }
}