package com.android.hmal.ui.activity

import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.color.DynamicColors
import com.google.android.hmal.R
import com.google.android.hmal.databinding.ActivityMainBinding
import com.android.hmal.ui.util.ThemeUtils
import rikka.material.app.MaterialActivity

class MainActivity : MaterialActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ThemeUtils.isSystemAccent) DynamicColors.applyToActivityIfAvailable(this)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        setupWithNavController(binding.bottomNav, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onApplyUserThemeResource(theme: Resources.Theme, isDecorView: Boolean) {
        if (!ThemeUtils.isSystemAccent) theme.applyStyle(ThemeUtils.colorThemeStyleRes, true)
        theme.applyStyle(ThemeUtils.getNightThemeStyleRes(this), true)
    }

    override fun computeUserThemeKey() = ThemeUtils.colorTheme + ThemeUtils.getNightThemeStyleRes(this)
}
