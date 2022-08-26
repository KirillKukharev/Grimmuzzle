package com.akvelon.grimmuzzle.ui.errorhandler

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.akvelon.grimmuzzle.R
import com.akvelon.grimmuzzle.databinding.ErrorHandlerViewBinding

class ErrorHandlerView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private var OnErrorAction: () -> Unit = { }
    private lateinit var binding: ErrorHandlerViewBinding

    init {
        LayoutInflater.from(context).inflate(R.layout.error_handler_view, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.error_handler_view,
            this,
            true
        )
        binding.reloadButton.setOnClickListener {
            OnErrorAction.invoke()
            visibility = GONE
        }
    }

    fun show(callback: () -> Unit) {
        visibility = VISIBLE
        OnErrorAction = callback
    }
}