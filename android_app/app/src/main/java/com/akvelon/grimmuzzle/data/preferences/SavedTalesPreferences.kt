package com.akvelon.grimmuzzle.data.preferences

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.databinding.ObservableField
import com.akvelon.grimmuzzle.R
import com.akvelon.grimmuzzle.ui.MainActivity

object SavedTalesPreferences {
    var savedIds = ObservableField<MutableList<String>>()

    private fun getListSavedIds(): Pair<SharedPreferences, MutableList<String>> {
        val pref = MainActivity.instance.getPreferences(Context.MODE_PRIVATE)
        val savedIdsString =
            StringBuilder(
                pref.getString(
                    MainActivity.instance.getString(R.string.saved_tales_ids),
                    ""
                ) ?: ""
            )
        val resultList =
            if (savedIdsString.isBlank()) emptyList<String>().toMutableList() else savedIdsString.split(
                " "
            ).toMutableList()
        if (resultList != savedIds.get()) {
            savedIds.set(resultList)
        }
        return Pair(pref, savedIds.get()!!)
    }

    fun updateSavedIds() {
        getListSavedIds()
    }

    fun insertSavedId(InsertId: String) {
        val getListSavedIds = getListSavedIds()
        val pref = getListSavedIds.first
        val res = mutableListOf<String>()
        res.addAll(getListSavedIds.second)
        if (res.contains(InsertId)) {
            return
        }
        res.add(0, InsertId)
        with(pref.edit()) {
            putString(
                MainActivity.instance.getString(R.string.saved_tales_ids),
                res.joinToString(" ")
            )
            apply()
        }
        savedIds.set(res)
        Toast.makeText(
            MainActivity.instance,
            R.string.tale_has_been_saved_text,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun deleteSavedId(DeletedId: String) {
        val getListSavedIds = getListSavedIds()
        val pref = getListSavedIds.first
        val res = mutableListOf<String>()
        res.addAll(getListSavedIds.second)
        if (!res.contains(DeletedId)) {
            return
        }
        res.remove(DeletedId)
        with(pref.edit()) {
            putString(
                MainActivity.instance.getString(R.string.saved_tales_ids),
                res.joinToString(" ")
            )
            apply()
        }
        savedIds.set(res)
        Toast.makeText(
            MainActivity.instance,
            R.string.tale_has_been_deleted,
            Toast.LENGTH_SHORT
        ).show()
    }
}