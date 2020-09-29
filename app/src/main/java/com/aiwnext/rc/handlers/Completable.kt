package com.aiwnext.rc.handlers

import kotlin.coroutines.CoroutineContext

interface Completable {

    fun doOnSchedule(job: () -> Unit): Completable
    fun doOnComplete(job: () -> Unit): Completable

    fun setOnScheduleWorker(worker: CoroutineContext): Completable
    fun setOnCompleteWorker(worker: CoroutineContext): Completable

    fun disposeOnComplete(dispose: Boolean = true): Completable

    fun execute()
}
