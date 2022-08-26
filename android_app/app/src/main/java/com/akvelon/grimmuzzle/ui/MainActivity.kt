package com.akvelon.grimmuzzle.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.akvelon.grimmuzzle.R
import com.akvelon.grimmuzzle.data.entities.event.EventProcessor
import com.akvelon.grimmuzzle.ui.errorhandler.ErrorHandlerView


class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var instance: MainActivity
    }

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        setContentView(R.layout.activity_main)
        navController = findNavController(R.id.nav_host_fragment)
    }

    override fun onBackPressed() {
        val id = navController.currentDestination?.id
        if (id == R.id.readingScreenFragment)
            navController.popBackStack(R.id.mainScreenFragment, false)
        else
            super.onBackPressed()
    }

    // throwErrorForFragment
    fun onErrorForCurrentFragment(classFragment: Class<out Fragment>, t: Throwable?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mainExecutor.execute {
                val navHostFragment: NavHostFragment? =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
                var fragment =
                    navHostFragment!!.childFragmentManager.fragments.find { it::class.java == classFragment }
                if (fragment is EventProcessor<*>) {
                    fragment.onError(t)
                } else {
                    fragment =
                        supportFragmentManager.fragments.find { it::class.java == classFragment }
                    if (fragment is EventProcessor<*>) {
                        fragment.onError(t)
                    }
                }
            }
        }
    }

    fun getProgressBar(): ProgressBar = instance.findViewById(R.id.progressBar)
    fun hideErrorView() {
        instance.findViewById<ErrorHandlerView>(R.id.error_screen).visibility = View.GONE
    }

    fun showErrorView(action: () -> Unit) {
        val errorScreen = findViewById<ErrorHandlerView>(R.id.error_screen)
        errorScreen.show(action)
    }
}