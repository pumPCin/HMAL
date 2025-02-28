package com.android.hmal.xposed.hook

import android.annotation.TargetApi
import android.content.pm.ApplicationInfo
import android.os.Build
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import de.robv.android.xposed.XC_MethodHook
import com.android.hmal.common.CommonUtils
import com.android.hmal.xposed.HMALService

@TargetApi(Build.VERSION_CODES.S)
class ZygoteArgsHook(private val service: HMALService) : IFrameworkHook {

    companion object {
        private const val TAG = "HMAL-ZAH"
        private val sAppDataIsolationEnabled = CommonUtils.isAppDataIsolationEnabled
        private val sVoldAppDataIsolationEnabled = CommonUtils.isVoldAppDataIsolationEnabled
    }

    private var hook: XC_MethodHook.Unhook? = null

    override fun load() {
        if (!service.config.forceMountData) return
        hook = findMethod("android.os.ZygoteProcess") {
            name == "startViaZygote"
        }.hookBefore { param ->
            runCatching {
                val changeId = param.args[0] as Long
                val appInfo = param.args[1] as ApplicationInfo
                if (changeId.toInt() != 143937733) return@hookBefore
                val apps = service.pms.getPackagesForUid(appInfo.uid) ?: return@hookBefore
                for (app in apps) {
                    if (service.isHookEnabled(app)) {
                        if (sAppDataIsolationEnabled) param.result = true
                        if (service.isVoldEnabled(app)) param.args[21] = true

                        val bindMountAppStorageDirs = param.args[21] as Boolean

                        return@hookBefore
                    }
                }
            }.onFailure {
                unload()
            }
        }
    }

    override fun unload() {
        hook?.unhook()
        hook = null
    }

    override fun onConfigChanged() {
        if (service.config.forceMountData) {
            if (hook == null) load()
        } else {
            if (hook != null) unload()
        }
    }
}
