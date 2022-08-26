package com.akvelon.grimmuzzle.di

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import com.akvelon.grimmuzzle.GrimmuzzleApplication
import com.akvelon.grimmuzzle.ui.MainActivity
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule {
    @Singleton
    @Provides
    fun getApplication(): GrimmuzzleApplication {
        return GrimmuzzleApplication.instance
    }

    @Singleton
    @Provides
    fun getApplicationContext(): Context {
        return GrimmuzzleApplication.instance
    }

    @Singleton
    @Provides
    fun getMainActivity(): MainActivity {
        return MainActivity.instance
    }

    @Singleton
    @Provides
    fun getFragmentActivity(): FragmentActivity {
        return MainActivity.instance
    }

    @Singleton
    @Provides
    fun getMainLooperHandler(): Handler {
        return Handler(Looper.getMainLooper())
    }
}