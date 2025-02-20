package com.android.hmal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.hmal.common.JsonConfig

class AppSettingsViewModel(val pack: Pack) : ViewModel() {

    class Factory(private val pack: Pack) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AppSettingsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AppSettingsViewModel(pack) as T
            } else throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    class Pack(
        val app: String,
        var enabled: Boolean,
        val config: JsonConfig.AppConfig
    )
}
