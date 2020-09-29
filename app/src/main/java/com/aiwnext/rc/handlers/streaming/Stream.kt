package com.aiwnext.rc.handlers.streaming

import kotlinx.coroutines.*
import com.aiwnext.rc.handlers.Completable
import com.aiwnext.rc.scope.RCScope
import com.aiwnext.rc.ext.e
import com.aiwnext.rc.streaming.StreamComponent
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class Stream(private val taskContext: CoroutineContext,
             private val streamStart: () -> Any) : Completable, RCScope {

    private val streamingContexts: ArrayList<CoroutineContext?> = arrayListOf()
    private val streams: ArrayList<StreamComponent<*, *>> = arrayListOf()

    private var completeJob: (() -> Unit)? = null
    private var scheduleJob: (() -> Unit)? = null

    private var completeContext: CoroutineContext? = null
    private var scheduleContext: CoroutineContext? = null

    private var dispose = true

    override fun doOnSchedule(job: () -> Unit): Stream {
        this.scheduleJob = job
        return this
    }

    override fun doOnComplete(job: () -> Unit): Stream {
        this.completeJob = job
        return this
    }

    override fun setOnScheduleWorker(worker: CoroutineContext): Stream {
        this.scheduleContext = worker
        return this
    }

    override fun setOnCompleteWorker(worker: CoroutineContext): Stream {
        this.completeContext = worker
        return this
    }

    fun <S : StreamComponent<*, *>> add(stream: S, streamContext: CoroutineContext): Stream {
        streams.add(stream)
        streamingContexts.add(streamContext)
        return this
    }

    override fun execute() {
        GlobalScope.launch(taskContext) {
            schedule()
            startStreaming()
            complete()
            disposeAll()
        }
    }

    private suspend fun startStreaming() {
        val stream = if (streams.size > 0) streams[0] else null
        onNextStreamComponentAsync(streamStart(), stream, 0).await()
    }

    private suspend fun <P : Any?, R : Any?> onNextStreamComponentAsync(prevResult: Any, stream: StreamComponent<P, R>?, id: Int): Deferred<Any?> =
            GlobalScope.async(if (id < streamingContexts.size) { streamingContexts[id] ?: taskContext } else taskContext) {
        e { stream?.onComplete(prevResult as P) }
                .apply {
                    if (this is Exception) {
                        stream?.onError(this)
                    } else {
                        if (id + 1 < streams.size) {
                            return@async onNextStreamComponentAsync(this!!, streams[id + 1], id + 1).await()
                        }
                    }
                }
    }

    override fun disposeOnComplete(dispose: Boolean): Stream {
        this.dispose = dispose
        return this
    }

    private suspend fun schedule() {
        withContext(scheduleContext ?: coroutineContext) { scheduleJob?.invoke() }
    }

    private suspend fun complete() {
        withContext(completeContext ?: coroutineContext) { completeJob?.invoke() }
    }

    private fun disposeAll() = dispose.let {
        if (it) {
            arrayOf(completeContext, scheduleContext, taskContext).forEach { it?.cancel() }
            streamingContexts.forEach { it?.cancel() }
        }
    }
}
