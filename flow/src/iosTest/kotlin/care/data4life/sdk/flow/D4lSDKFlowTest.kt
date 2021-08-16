/*
 * Copyright (c) 2021 D4L data4life gGmbH / All rights reserved.
 *
 * D4L owns all legal rights, title and interest in and to the Software Development Kit ("SDK"),
 * including any intellectual property rights that subsist in the SDK.
 *
 * The SDK and its documentation may be accessed and used for viewing/review purposes only.
 * Any usage of the SDK for other purposes, including usage for the development of
 * applications/third-party applications shall require the conclusion of a license agreement
 * between you and D4L.
 *
 * If you are interested in licensing the SDK for your own applications/third-party
 * applications and/or if you’d like to contribute to the development of the SDK, please
 * contact D4L by email to help@data4life.care.
 */

package care.data4life.sdk.flow

import care.data4life.sdk.lang.PlatformError
import care.data4life.sdk.util.objc.NSErrorFactory
import care.data4life.sdk.util.test.coroutine.runBlockingTest
import co.touchlab.stately.isFrozen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlin.coroutines.CoroutineContext
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class D4LSDKFlowTest {
    @Test
    fun `It exposes its wrapped Flow`() {
        // Given
        val ktFlow = flow<Unit> { }

        // When
        val result = D4LSDKFlow.getInstance(
            GlobalScope,
            ktFlow,
            { NSErrorFactory.create(23, "domain", "localized", RuntimeException()) },
        ).ktFlow

        // Then
        assertSame(
            actual = result,
            expected = result
        )
    }

    @Test
    fun `Given subscribe is called it returns a Job`() {
        // Given
        val ktFlow = flow<Unit> { }

        // When
        val job: Any = D4LSDKFlow.getInstance(
            GlobalScope,
            ktFlow,
            { NSErrorFactory.create(23, "domain", "localized", RuntimeException()) },
        ).subscribe(
            {},
            {},
            {}
        )

        // Then
        assertTrue(job is Job)
    }

    @Test
    fun `Given subscribe is called with a Closure to receive emitted Items it delegates the emitted Item to there`() {
        // Given
        val item = object {}
        val ktFlow = flow<Any> {
            emit(item)
        }

        val capturedItem = Channel<Any>()

        // When
        val job = D4LSDKFlow.getInstance(
            GlobalScope,
            ktFlow,
            { NSErrorFactory.create(23, "domain", "localized", RuntimeException()) },
        ).subscribe(
            { delegatedItem ->
                GlobalScope.launch {
                    capturedItem.send(delegatedItem)
                }
            },
            {},
            {}
        )

        // Then
        runBlockingTest {
            job.join()

            val result = capturedItem.receive()

            assertSame(
                actual = result,
                expected = item
            )
            assertTrue(result.isFrozen)
        }
    }

    @Test
    fun `Given subscribe is called with a Closure to receive emitted Errors it maps then to PlatformError and delegates the emitted Errors to there`() {
        // Given
        val domainError = RuntimeException()
        val error = NSErrorFactory.create(23, "domain", "localized", RuntimeException())
        val ktFlow = flow<Any> {
            throw domainError
        }

        val capturedDomainError = Channel<Throwable>()
        val capturedError = Channel<PlatformError>()

        // When
        val job = D4LSDKFlow.getInstance(
            GlobalScope,
            ktFlow,
            { delegatedDomainError ->
                GlobalScope.launch {
                    capturedDomainError.send(delegatedDomainError)
                }
                error
            },
        ).subscribe(
            {},
            onError = { delegatedError ->
                GlobalScope.launch {
                    capturedError.send(delegatedError)
                }
            },
            {}
        )

        // Then
        runBlockingTest {
            job.join()

            val result = capturedError.receive()

            assertSame<Any>(
                actual = capturedDomainError.receive(),
                expected = domainError
            )

            assertSame<Any>(
                actual = result,
                expected = error
            )
            assertTrue(result.isFrozen)
        }
    }

    @Test
    fun `Given subscribe is called with a Closure be notified if the Flow had been completed, it calls the Closure`() {
        // Given
        val wasCalled = Channel<Boolean>()
        val ktFlow = flow<Unit> {}

        // When
        val job = D4LSDKFlow.getInstance(
            GlobalScope,
            ktFlow,
            { NSErrorFactory.create(23, "domain", "localized", RuntimeException()) },
        ).subscribe(
            {},
            {},
            onComplete = {
                GlobalScope.launch {
                    wasCalled.send(true)
                }
            }
        )

        // Then
        runBlockingTest {
            job.join()

            assertTrue(wasCalled.receive())
        }
    }

    @Test
    fun `Given a Flow had been initialized it is frozen`() {
        // Given
        val flow = D4LSDKFlow.getInstance(
            GlobalScope,
            flow<Unit> { },
        ) { NSErrorFactory.create(23, "domain", "localized", RuntimeException()) }

        // Then
        assertTrue(flow.isFrozen)
    }

    @Test
    fun `Its KtFlow  is frozen`() {
        // Given
        val flow = D4LSDKFlow.getInstance(GlobalScope, flow<Unit> {}) {
            NSErrorFactory.create(
                23,
                "domain",
                "localized",
                RuntimeException()
            )
        }

        // Then
        assertTrue(flow.ktFlow.isFrozen)
    }

    @Test
    fun `Given subscribe is called it returns a Job which is frozen`() {
        // Given
        val ktFlow = flow<Unit> { }

        // When
        val job = D4LSDKFlow.getInstance(
            GlobalScope,
            ktFlow,
            { NSErrorFactory.create(23, "domain", "localized", RuntimeException()) }
        )
            .subscribe(
                {},
                {},
                {}
            )

        // Then
        assertTrue(job.isFrozen)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `Given subscribe is called with a scope it launches it in the given scope`() {
        // Given
        val context: CoroutineContext = newSingleThreadContext("testRunner2")
        val scope = CoroutineScope(context)
        val ktFlow = flow<Unit> {}

        // When
        val job = D4LSDKFlow.getInstance(
            scope,
            ktFlow,
            { NSErrorFactory.create(23, "domain", "localized", RuntimeException()) }
        )
            .subscribe(
                {},
                {},
                {}
            )
        // Then
        assertTrue(job.isActive)
        scope.cancel()
        assertFalse(job.isActive)
    }
}
