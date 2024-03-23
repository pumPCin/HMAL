package icu.nullptr.hidemyapplist.xposed.hook

import android.content.ComponentName
import android.content.Intent
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.invokeMethodAs
import com.github.kyuubiran.ezxhelper.utils.loadClass
import de.robv.android.xposed.XC_MethodHook
import icu.nullptr.hidemyapplist.common.Constants
import icu.nullptr.hidemyapplist.xposed.HMAService
import icu.nullptr.hidemyapplist.xposed.Utils
import icu.nullptr.hidemyapplist.xposed.Utils.getBinderCaller
import java.lang.reflect.Method

class PmsHookLegacy(private val service: HMAService) : IFrameworkHook {

    companion object {
        private const val TAG = "HMAL-PHL"
    }

    private val hooks = mutableSetOf<XC_MethodHook.Unhook>()

    @Suppress("UNCHECKED_CAST")
    private fun removeList(
        method: Method,
        isParceled: Boolean,
        pkgNameObjList: List<String>
    ) = method.hookAfter { param ->
        if (param.hasThrowable()) return@hookAfter
        val caller = param.thisObject.getBinderCaller()
        if (caller !in service.config.scope) return@hookAfter

        var isHidden = false
        val list =
            if (isParceled) param.result.invokeMethodAs<MutableList<Any>>("getList")!!
            else param.result as MutableList<Any>
        val iterator = list.iterator()
        val removed = mutableListOf<String>()

        while (iterator.hasNext()) {
            val pkg = Utils.getRecursiveField(iterator.next(), pkgNameObjList) as String?
            if (service.shouldHide(caller, pkg)) {
                iterator.remove()
                isHidden = true
            }
        }

        if (isHidden) {
            service.filterCount++
        }
    }

    private fun setResult(
        method: Method,
        result: Any?
    ) = method.hookAfter { param ->
        val caller = param.thisObject.getBinderCaller()
        if (caller !in service.config.scope) return@hookAfter

        if (service.shouldHide(caller, param.args[0] as String?)) {
            service.filterCount++
            param.result = result
        }
    }

    private fun resolveIntent(
        method: Method,
        result: Any?
    ) = method.hookAfter { param ->
        val caller = param.thisObject.getBinderCaller()
        if (caller !in service.config.scope) return@hookAfter

        when (val it = param.args[0]) {
            is Intent -> listOf(it.action, it.component?.packageName)
            is ComponentName -> listOf(it.packageName, it.className)
            else -> emptyList()
        }.forEach {
            if (service.shouldHide(caller, it)) {
                service.filterCount++
                param.result = result
                return@hookAfter
            }
        }
    }

    override fun load() {
        val pmMethods = buildSet<Method> {
            addAll(service.pms::class.java.declaredMethods)
            val pmsClass = loadClass(Constants.CLASS_PMS)
            if (service.pms::class.java != pmsClass) { // Has custom pms
                val added = mapTo(mutableSetOf()) { it.name }
                pmsClass.declaredMethods.forEach {
                    if (!added.contains(it.name)) add(it)
                }
            }
        }
        for (method in pmMethods) when (method.name) {
            "getAllPackages"
            -> hooks += removeList(method, false, listOf())

            "getInstalledPackages",
            "getInstalledApplications",
            "getPackagesHoldingPermissions",
            "queryInstrumentation"
            -> hooks += removeList(method, true, listOf("packageName"))

            "getPackageInfo",
            "getPackageGids",
            "getApplicationInfo",
            "getInstallSourceInfo",
            "getInstallerPackageName",
            "getLaunchIntentForPackage",
            "getLeanbackLaunchIntentForPackage"
            -> hooks += setResult(method, null)

            "queryIntentActivities",
            "queryIntentActivityOptions",
            "queryIntentReceivers",
            "queryIntentServices",
            "queryIntentContentProviders"
            -> hooks += removeList(method, true, listOf("activityInfo", "packageName"))

            "getActivityInfo",
            "resolveActivity",
            "resolveActivityAsUser"
            -> hooks += resolveIntent(method, null)

            "getPackageUid"
            -> hooks += setResult(method, -1)
        }
    }

    override fun unload() {
        hooks.forEach(XC_MethodHook.Unhook::unhook)
        hooks.clear()
    }
}
