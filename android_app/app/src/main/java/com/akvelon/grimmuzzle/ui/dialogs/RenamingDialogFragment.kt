package com.akvelon.grimmuzzle.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.akvelon.grimmuzzle.R
import com.akvelon.grimmuzzle.data.entities.event.EventProcessor
import com.akvelon.grimmuzzle.databinding.RenamingDialogBinding


class RenamingDialogFragment : DialogFragment(), EventProcessor<String> {
    private lateinit var viewModel: RenamingDialogViewModel
    private lateinit var binding: RenamingDialogBinding
    private var onOKCallback: (String) -> Unit = {}
    private var onCancelCallback: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(RenamingDialogViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.renaming_dialog, container, false)
        dialog!!.window?.let { window ->
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.requestFeature(Window.FEATURE_NO_TITLE)
        }
        viewModel.taleName.set(arguments?.getString("name"))
        binding.viewModel = viewModel

        binding.cancelButton.setOnClickListener {
            onCancelCallback()
            dialog!!.dismiss()
        }
        binding.saveButton.setOnClickListener {
            saveTale()
        }
        return binding.root
    }

    override fun onSuccess(result: String?) {
        onOKCallback(result!!)
        dialog!!.dismiss()
    }

    override fun onLoad() {
    }

    override fun onError(t: Throwable?) {
        binding.inputContainer.visibility = View.GONE
        binding.errorScreen.show(::saveTale)
    }

    fun setOnResponseCallback(function: (String) -> Unit) {
        this.onOKCallback = function
    }

    fun setOnCancelCallback(function: () -> Unit) {
        this.onCancelCallback = function
    }

    private fun saveTale() {
        val id = arguments?.getString("id")
        val trimmedTaleName = viewModel.taleName.get()?.trim()!!
        if (arguments?.getString("name") != trimmedTaleName) {
            if (trimmedTaleName.length >= 3) {
                viewModel.renameTale(
                    id!!,
                    trimmedTaleName,
                    this,
                    this
                )
            }
        } else {
            onOKCallback(trimmedTaleName)
            dialog!!.dismiss()
        }
    }
}