package com.aiwnext.rc.ext

import com.aiwnext.rc.handlers.single.Task
import com.aiwnext.rc.handlers.streaming.Chain
import com.aiwnext.rc.handlers.streaming.Stream
import com.aiwnext.rc.handlers.subscribers.Subscription
import com.aiwnext.rc.scope.RCScope
import com.aiwnext.rc.workers.Workers
import kotlin.coroutines.CoroutineContext


fun RCScope.task(job: () -> Unit, worker: CoroutineContext = Workers.default()): Task {
    return Task(worker, job)
}

fun RCScope.chain(): Chain {
    return Chain()
}

fun <S : Any?> RCScope.subscription(job: () -> S, worker: CoroutineContext = Workers.default()): Subscription<S> {
    return Subscription(worker, job)
}

fun RCScope.stream(job: () -> Any, worker: CoroutineContext = Workers.default()): Stream {
    return Stream(worker, job)
}
