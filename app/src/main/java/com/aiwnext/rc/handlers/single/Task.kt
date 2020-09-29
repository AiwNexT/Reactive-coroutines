package com.aiwnext.rc.handlers.single

import kotlinx.coroutines.*
import com.aiwnext.rc.handlers.Completable
import com.aiwnext.rc.scope.RCScope
import kotlin.coroutines.CoroutineContext

class Task(private val taskContext: CoroutineContext,
           private val job: () -> Unit): Completable, RCScope {

    private var onScheduleJob: (() -> Unit)? = null
    private var onCompleteJob: (() -> Unit)? = null

    private var scheduleContext: CoroutineContext? = null
    private var completeContext: CoroutineContext? = null

    private var dispose = true

    override fun doOnSchedule(job: () -> Unit): Task {
        this.onScheduleJob = job
        return this
    }

    override fun doOnComplete(job: () -> Unit): Task {
        this.onCompleteJob = job
        return this
    }

    override fun setOnScheduleWorker(worker: CoroutineContext): Task {
        this.scheduleContext = worker
        return this
    }

    override fun setOnCompleteWorker(worker: CoroutineContext): Task {
        this.completeContext = worker
        return this
    }

    override fun execute() {
        GlobalScope.launch(taskContext) {
            withContext(scheduleContext ?: coroutineContext) { onScheduleJob?.invoke() }
            job.invoke()
            withContext(completeContext ?: coroutineContext) { onCompleteJob?.invoke() }
            disposeAll()
        }
    }

    override fun disposeOnComplete(dispose: Boolean): Completable {
        this.dispose = dispose
        return this
    }

    private fun disposeAll() = dispose.let {
        if (it) { arrayOf(taskContext, scheduleContext, completeContext).forEach { it?.cancel() } }
    }
}
