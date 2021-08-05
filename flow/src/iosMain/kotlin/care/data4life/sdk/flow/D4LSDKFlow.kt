package care.data4life.sdk.flow

import care.data4life.sdk.lang.PlatformError
import care.data4life.sdk.util.coroutine.DomainErrorMapperContract
import co.touchlab.stately.freeze
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

actual class D4LSDKFlow<T : Any> private actual constructor(
    defaultScope: CoroutineScope,
    internalFlow: Flow<T>,
    domainErrorMapper: DomainErrorMapperContract,
) {
    private val flow = internalFlow
    private val scope = defaultScope
    private val errorMapper = domainErrorMapper

    init {
        freeze()
    }

    actual val ktFlow: Flow<T>
        get() = flow.freeze()

    actual fun subscribe(
        onEach: (item: T) -> Unit,
        onError: (error: PlatformError) -> Unit,
        onComplete: (() -> Unit)
    ): Job {
        return flow
            .onEach { item -> onEach(item) }
            .catch { error -> onError(errorMapper.mapError(error)) }
            .onCompletion { onComplete.invoke() }
            .launchIn(scope)
            .freeze()
    }

    actual companion object : D4LSDKFlowFactoryContract {
        actual override fun <T : Any> getInstance(
            defaultScope: CoroutineScope,
            internalFlow: Flow<T>,
            domainErrorMapper: DomainErrorMapperContract
        ): D4LSDKFlow<T> {
            return D4LSDKFlow(
                defaultScope, internalFlow, domainErrorMapper
            )
        }
    }
}
