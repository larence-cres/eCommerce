package com.sample.ecommerce.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.sample.ecommerce.R

class MainActivity : AppCompatActivity() {

    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val windowInsetController = ViewCompat.getWindowInsetsController(window.decorView)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        mNavController = navHostFragment.navController

        mNavController.addOnDestinationChangedListener { _, destination, args ->
            when (destination.id) {
                R.id.profileFragment -> {
                    setStatusBarColor(R.color.colorPrimary)
                    windowInsetController?.isAppearanceLightStatusBars = false
                }
                else -> {
                    setStatusBarColor(R.color.background)
                    windowInsetController?.isAppearanceLightStatusBars = true
                }
            }
        }

        supportActionBar!!.hide()
    }

    private fun setStatusBarColor(color: Int) {
        val window: Window = window
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(
                this,
                color
            )
        }
    }

}