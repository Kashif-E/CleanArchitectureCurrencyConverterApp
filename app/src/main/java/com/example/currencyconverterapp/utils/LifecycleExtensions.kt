package com.example.currencyconverterapp.utils


import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

/**
 * Flow operator that emits values from `this` upstream Flow when the [lifecycle] is
 * at least at [minActiveState] state. The emissions will be stopped when the lifecycle state
 * falls below [minActiveState] state.
 *
 * The flow will automatically start and cancel collecting from `this` upstream flow as the
 * [lifecycle] moves in and out of the target state.
 *
 * If [this] upstream Flow completes emitting items, `flowWithLifecycle` will trigger the flow
 * collection again when the [minActiveState] state is reached.
 *
 * This is NOT a terminal operator. This operator is usually followed by [collect], or
 * [onEach] and [launchIn] to process the emitted values.
 *
 * Note: this operator creates a hot flow that only closes when the [lifecycle] is destroyed or
 * the coroutine that collects from the flow is cancelled.
 *
 * ```
 * class MyActivity : AppCompatActivity() {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         /* ... */
 *         // Launches a coroutine that collects items from a flow when the Activity
 *         // is at least started. It will automatically cancel when the activity is stopped and
 *         // start collecting again whenever it's started again.
 *         lifecycleScope.launch {
 *             flow
 *                 .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
 *                 .collect {
 *                     // Consume flow emissions
 *                  }
 *         }
 *     }
 * }
 * ```
 *
 * `flowWithLifecycle` cancels the upstream Flow when [lifecycle] falls below
 * [minActiveState] state. However, the downstream Flow will be active without receiving any
 * emissions as long as the scope used to collect the Flow is active. As such, please take care
 * when using this function in an operator chain, as the order of the operators matters. For
 * example, `flow1.flowWithLifecycle(lifecycle).combine(flow2)` behaves differently than
 * `flow1.combine(flow2).flowWithLifecycle(lifecycle)`. The former continues to combine both
 * flows even when [lifecycle] falls below [minActiveState] state whereas the combination is
 * cancelled in the latter case.
 *
 * Warning: [Lifecycle.State.INITIALIZED] is not allowed in this API. Passing it as a
 * parameter will throw an [IllegalArgumentException].
 *
 * Tip: If multiple flows need to be collected using `flowWithLifecycle`, consider using
 * the [Lifecycle.repeatOnLifecycle] API to collect from all of them using a different
 * [launch] per flow instead. That's more efficient and consumes less resources as no hot flows
 * are created.
 *
 * @param lifecycle The [Lifecycle] where the restarting collecting from `this` flow work will be
 * kept alive.
 * @param minActiveState [Lifecycle.State] in which the upstream flow gets collected. The
 * collection will stop if the lifecycle falls below that state, and will restart if it's in that
 * state again.
 * @return [Flow] that only emits items from `this` upstream flow when the [lifecycle] is at
 * least in the [minActiveState].
 */
@OptIn(ExperimentalCoroutinesApi::class)
public fun <T> Flow<T>.flowWithLifecycle(
    lifecycle: Lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
): Flow<T> = callbackFlow {
    lifecycle.repeatOnLifecycle(minActiveState) {
        this@flowWithLifecycle.collect {
            send(it)
        }
    }
    close()
}
public suspend fun Lifecycle.repeatOnLifecycle(
    state: Lifecycle.State,
    block: suspend CoroutineScope.() -> Unit
) {
    require(state !== Lifecycle.State.INITIALIZED) {
        "repeatOnLifecycle cannot start work with the INITIALIZED lifecycle state."
    }

    if (currentState === Lifecycle.State.DESTROYED) {
        return
    }

    // This scope is required to preserve context before we move to Dispatchers.Main
    coroutineScope {
        withContext(Dispatchers.Main.immediate) {
            // Check the current state of the lifecycle as the previous check is not guaranteed
            // to be done on the main thread.
            if (currentState === Lifecycle.State.DESTROYED) return@withContext

            // Instance of the running repeating coroutine
            var launchedJob: Job? = null

            // Registered observer
            var observer: LifecycleEventObserver? = null
            try {
                // Suspend the coroutine until the lifecycle is destroyed or
                // the coroutine is cancelled
                suspendCancellableCoroutine<Unit> { cont ->
                    // Lifecycle observers that executes `block` when the lifecycle reaches certain state, and
                    // cancels when it falls below that state.
                    val startWorkEvent = Lifecycle.Event.upTo(state)
                    val cancelWorkEvent = Lifecycle.Event.downFrom(state)
                    val mutex = Mutex()
                    observer = LifecycleEventObserver { _, event ->
                        if (event == startWorkEvent) {
                            // Launch the repeating work preserving the calling context
                            launchedJob = this@coroutineScope.launch {
                                // Mutex makes invocations run serially,
                                // coroutineScope ensures all child coroutines finish
                                mutex.withLock {
                                    coroutineScope {
                                        block()
                                    }
                                }
                            }
                            return@LifecycleEventObserver
                        }
                        if (event == cancelWorkEvent) {
                            launchedJob?.cancel()
                            launchedJob = null
                        }
                        if (event == Lifecycle.Event.ON_DESTROY) {
                            cont.resume(Unit)
                        }
                    }
                    this@repeatOnLifecycle.addObserver(observer as LifecycleEventObserver)
                }
            } finally {
                launchedJob?.cancel()
                observer?.let {
                    this@repeatOnLifecycle.removeObserver(it)
                }
            }
        }
    }
}

/**
 * [LifecycleOwner]'s extension function for [Lifecycle.repeatOnLifecycle] to allow an easier
 * call to the API from LifecycleOwners such as Activities and Fragments.
 *
 * ```
 * class MyActivity : AppCompatActivity() {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         /* ... */
 *         // Runs the block of code in a coroutine when the lifecycle is at least STARTED.
 *         // The coroutine will be cancelled when the ON_STOP event happens and will
 *         // restart executing if the lifecycle receives the ON_START event again.
 *         lifecycleScope.launch {
 *             repeatOnLifecycle(Lifecycle.State.STARTED) {
 *                 uiStateFlow.collect { uiState ->
 *                     updateUi(uiState)
 *                 }
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * @see Lifecycle.repeatOnLifecycle
 */
public suspend fun LifecycleOwner.repeatOnLifecycle(
    state: Lifecycle.State,
    block: suspend CoroutineScope.() -> Unit
): Unit = lifecycle.repeatOnLifecycle(state, block)

