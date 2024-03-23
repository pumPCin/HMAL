package icu.nullptr.hidemyapplist.xposed.hook

import android.annotation.TargetApi
import android.os.Build
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import de.robv.android.xposed.XC_MethodHook
import icu.nullptr.hidemyapplist.common.CommonUtils
import icu.nullptr.hidemyapplist.common.Constants.*
import icu.nullptr.hidemyapplist.xposed.HMAService

@TargetApi(Build.VERSION_CODES.S)
class ZygoteArgsHook(private val service: HMAService) : IFrameworkHook {

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
                val uid = param.args[2] as Int
                if (uid == UID_SYSTEM) return@hookBefore
                val apps = service.pms.getPackagesForUid(uid) ?: return@hookBefore
                for (app in apps) {
                    if (service.isHookEnabled(app)) {
                        if (sAppDataIsolationEnabled) param.args[param.args.size - 3] = true
                        if (sVoldAppDataIsolationEnabled) param.args[param.args.size - 2] = true
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
