package com.android.hmal.util

import com.topjohnwu.superuser.Shell

object SuUtils {

    fun execPrivileged(cmd: String): Boolean {
        return Shell.cmd(cmd).exec().isSuccess && Shell.isAppGrantedRoot() == true
    }
}
