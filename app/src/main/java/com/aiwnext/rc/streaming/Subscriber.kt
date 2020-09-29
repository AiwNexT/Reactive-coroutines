package com.aiwnext.rc.streaming

interface Subscriber<in S: Any?> {

    fun onComplete(result: S)
    fun onError(e: Exception) { }
}
