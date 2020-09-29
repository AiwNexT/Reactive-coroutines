package com.aiwnext.rc.ext

import com.aiwnext.rc.scope.RCScope

fun <T : Any?> RCScope.e(todo: () -> T): Any? {
    return try {
        todo()
    } catch (e: Exception) {
        e
    }
}
