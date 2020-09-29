package com.aiwnext.rc.handlers.streaming

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.aiwnext.rc.scope.RCScope
import kotlin.coroutines.CoroutineContext

class Chain: RCScope {

    private val chainContexts = arrayListOf<CoroutineContext>()
    private val jobs = arrayListOf<(() -> Unit)>()

    fun add(job: (() -> Unit), worker: CoroutineContext): Chain {
        chainContexts.add(worker)
        jobs.add(job)
        return this
    }

    fun execute() {
        GlobalScope.launch(chainContexts.first()) {
            jobs.first().invoke()
            (1 until chainContexts.size).forEach {
                withContext(chainContexts[it]) { jobs[it].invoke() }
            }
            disposeAll()
        }
    }

    private fun disposeAll() {
        chainContexts.forEach { it.cancel() }
    }
}
