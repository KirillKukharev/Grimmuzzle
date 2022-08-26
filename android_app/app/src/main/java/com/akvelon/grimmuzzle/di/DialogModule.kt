package com.akvelon.grimmuzzle.di

import com.akvelon.grimmuzzle.ui.dialogs.RenamingDialogFragment
import dagger.Module
import dagger.Provides

@Module
class DialogModule() {
    @Provides
    fun getRenamingDialog(): RenamingDialogFragment {
        return RenamingDialogFragment()
    }
}