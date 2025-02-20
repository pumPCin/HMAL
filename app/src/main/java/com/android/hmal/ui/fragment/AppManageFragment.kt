package com.android.hmal.ui.fragment

import android.os.Bundle
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.hmal.R
import com.android.hmal.service.ConfigManager
import com.android.hmal.ui.adapter.AppManageAdapter
import com.android.hmal.ui.util.navController

class AppManageFragment : AppSelectFragment() {

    override val firstComparator: Comparator<String> = Comparator.comparing(ConfigManager::isHideEnabled).reversed()

    override val adapter = AppManageAdapter {
        val args = AppSettingsFragmentArgs(it)
        navController.navigate(R.id.nav_app_settings, args.toBundle())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }
}
