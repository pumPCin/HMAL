package icu.nullptr.hidemyapplist.ui.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import icu.nullptr.hidemyapplist.util.PackageHelper

class AppChangeReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "ACR"

        private val actions = setOf(
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_REMOVED,
            Intent.ACTION_PACKAGE_REPLACED
        )

        fun register(context: Context) {
            val filter = IntentFilter().apply {
                actions.forEach(::addAction)
                addDataScheme("package")
            }
            context.registerReceiver(AppChangeReceiver(), filter)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action in actions) {
            PackageHelper.invalidateCache()
        }
    }
}
