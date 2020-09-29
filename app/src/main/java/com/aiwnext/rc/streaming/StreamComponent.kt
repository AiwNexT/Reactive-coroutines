package com.aiwnext.rc.streaming

interface StreamComponent<in P: Any?, out R: Any?> {

    fun onComplete(result: P): R
    fun onError(e: Exception) { }
}
