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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.flow
import kotlin.test.Test
import kotlin.test.assertTrue

@DelicateCoroutinesApi
class D4LSDKFlowFactoryTest {
    @Test
    fun `It fulfils D4LSDKFlowFactory`() {
        val factory: Any = D4LSDKFlow

        assertTrue(factory is D4LSDKFlowFactoryContract)
    }

    @Test
    fun `Given getInstance is called with a Scope, Flow and DomainErrorMapper it creates a instance of D4LSDKFlow`() {
        // Given
        val scope = GlobalScope
        val flow = flow<Int> { }

        // When
        val result: Any = D4LSDKFlow.getInstance(scope, flow) { it as PlatformError }

        // Then
        assertTrue(result is D4LSDKFlow<*>)
    }
}
