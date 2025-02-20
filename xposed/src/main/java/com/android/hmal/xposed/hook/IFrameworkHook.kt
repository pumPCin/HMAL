package com.android.hmal.xposed.hook

interface IFrameworkHook {

    fun load()
    fun unload()
    fun onConfigChanged() {}
}
