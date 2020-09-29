package com.aiwnext.rc.workers

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

object Workers {

    private const val DEDICATED = "dedicated"
    private const val MERGED = "merged"

    private val customContexts: Map<String, CoroutineContext> = mapOf()

    fun default() = Dispatchers.Default
    fun ui() = Dispatchers.Main
    fun io() = Dispatchers.IO
    fun dedicated() = newSingleThreadContext("${DEDICATED}-${System.nanoTime()}")
    fun merged(threadCount: Int) = newFixedThreadPoolContext(threadCount, "${MERGED}-${System.nanoTime()}")

    fun createWorker(workerName: String, worker: CoroutineContext): CoroutineContext {
        val newWorker = Pair(workerName, worker)
        customContexts.plus(newWorker)
        return newWorker.second
    }

    fun getWorker(workerName: String): CoroutineContext? {
        return customContexts[workerName]
    }
 }
