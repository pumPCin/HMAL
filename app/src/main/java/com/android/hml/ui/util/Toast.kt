package com.android.hml.ui.util

import android.widget.Toast
import androidx.annotation.StringRes
import com.android.hml.sysApp

fun makeToast(@StringRes resId: Int) {
    Toast.makeText(sysApp, resId, Toast.LENGTH_SHORT).show()
}
