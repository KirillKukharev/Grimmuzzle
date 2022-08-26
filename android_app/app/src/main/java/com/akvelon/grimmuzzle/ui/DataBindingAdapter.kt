package com.akvelon.grimmuzzle.ui

import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingComponent
import com.akvelon.grimmuzzle.R
import com.squareup.picasso.Picasso

object DataBindingAdapter : DataBindingComponent {

    fun getBindingAdapter(): DataBindingAdapter {
        return this
    }

    @JvmStatic
    @BindingAdapter(value = ["app:AttributeChecked"])
    fun makeChecked(cardView: CardView, isChecked: Boolean) {
        val checkMark = cardView.findViewById<ImageView>(R.id.check_mark)
        val attrConstrain = cardView.findViewById<ConstraintLayout>(R.id.attribute_constraint)
        if (isChecked) {
            attrConstrain.foreground = ResourcesCompat.getDrawable(
                cardView.resources,
                R.drawable.attribute_container_checked,
                cardView.context.theme
            )
            checkMark.visibility = View.VISIBLE
        } else {
            attrConstrain.foreground = ResourcesCompat.getDrawable(
                cardView.resources,
                R.drawable.attribute_container,
                cardView.context.theme
            )
            checkMark.visibility = View.INVISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["app:url"])
    fun setImageURL(view: View, url: String?) {
        if (!url.isNullOrEmpty()) {
            if (view is ImageView) {
                Picasso.get().load(url).fit().into(view)
            }
        }
    }


}