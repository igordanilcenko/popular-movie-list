package com.ihardanilchanka.sampleapp.helper

import androidx.lifecycle.Observer
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * A LiveData observer blocking the working thread until received data.
 */
class TestBlockingObserver<T>(
    private val timeoutMillis: Long = 3000L
) : Observer<T> {

    private val queue = LinkedList<T>()
    private var countDownLatch: CountDownLatch? = null

    override fun onChanged(t: T) {
        queue.add(t)
        countDownLatch?.countDown()
    }

    fun waitForValue(): T {
        if (queue.size > 0) {
            return queue.poll()!!
        }
        countDownLatch = CountDownLatch(1)
        countDownLatch!!.await(timeoutMillis, TimeUnit.MILLISECONDS)
        val result = queue.poll()
        if (result != null) {
            return result
        } else {
            throw TimeoutException("No value received until timeout")
        }
    }
}