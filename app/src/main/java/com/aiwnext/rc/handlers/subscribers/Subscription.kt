package com.aiwnext.rc.handlers.subscribers

import kotlinx.coroutines.*
import com.aiwnext.rc.handlers.Completable
import com.aiwnext.rc.scope.RCScope
import com.aiwnext.rc.ext.e
import com.aiwnext.rc.streaming.Subscriber
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class Subscription<S : Any?>(private val taskContext: CoroutineContext,
                             private val job: () -> S) : Completable, RCScope {

    private var completeContext: CoroutineContext? = null
    private var scheduleContext: CoroutineContext? = null

    private var subscribeContexts: ArrayList<CoroutineContext?> = arrayListOf()

    private var subscribers: ArrayList<Subscriber<S>?> = arrayListOf()

    private var completeJob: (() -> Unit)? = null
    private var scheduleJob: (() -> Unit)? = null

    private var dispose = true

    override fun doOnSchedule(job: () -> Unit): Subscription<S> {
        this.scheduleJob = job
        return this
    }

    override fun doOnComplete(job: () -> Unit): Subscription<S> {
        this.completeJob = job
        return this
    }

    override fun setOnScheduleWorker(worker: CoroutineContext): Subscription<S> {
        this.scheduleContext = worker
        return this
    }

    override fun setOnCompleteWorker(worker: CoroutineContext): Subscription<S> {
        this.completeContext = worker
        return this
    }

    fun<C: Subscriber<S>> subscribe(subscriber: C, subscribeContext: CoroutineContext): Subscription<S> {
        subscribers.add(subscriber)
        subscribeContexts.add(subscribeContext)
        return this
    }

    override fun execute() {
        GlobalScope.launch(taskContext) {
            schedule()
            subscribe()
            complete()
            disposeAll()
        }
    }

    private suspend fun subscribe() {
        forSubscriptionAsync().await().apply {
            (0 until subscribeContexts.size).forEach {
                withContext(subscribeContexts[it] ?: coroutineContext) {
                    if (this@apply is Exception) {
                        subscribers[it]?.onError(this@apply)
                    } else {
                        subscribers[it]?.onComplete(this@apply as S)
                    }
                }
            }
        }
    }

    override fun disposeOnComplete(dispose: Boolean): Subscription<S> {
        this.dispose = dispose
        return this
    }

    private suspend fun schedule() {
        withContext(scheduleContext ?: coroutineContext) { scheduleJob?.invoke() }
    }

    private suspend fun complete() {
        withContext(completeContext ?: coroutineContext) { completeJob?.invoke() }
    }

    private fun forSubscriptionAsync() = GlobalScope.async(taskContext) {
        return@async e { job.invoke() }
    }

    private fun disposeAll() = dispose.let {
        if (it) { arrayOf(completeContext, scheduleContext, taskContext).forEach { it?.cancel() } }
    }
}
