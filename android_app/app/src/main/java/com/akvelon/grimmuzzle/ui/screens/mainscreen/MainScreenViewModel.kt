package com.akvelon.grimmuzzle.ui.screens.mainscreen

import androidx.lifecycle.ViewModel
import com.akvelon.grimmuzzle.GrimmuzzleApplication
import com.akvelon.grimmuzzle.data.DataNode
import com.akvelon.grimmuzzle.ui.screens.mainscreen.viewpager.TalesScreenViewPagerAdapter
import javax.inject.Inject

class MainScreenViewModel : ViewModel() {
    lateinit var adapter: TalesScreenViewPagerAdapter
    @Inject
    lateinit var dataNode: DataNode

    init {
        GrimmuzzleApplication.instance.injector.inject(this)
    }
}