package com.android.hmal.ui.util

import android.widget.Toast
import androidx.annotation.StringRes
import com.android.hmal.sysApp

fun makeToast(@StringRes resId: Int) {
    Toast.makeText(sysApp, resId, Toast.LENGTH_SHORT).show()
}
