package frc.team5190.lib.utils.statefulvalue

import kotlinx.coroutines.experimental.channels.ChannelIterator
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.selects.SelectClause1
import kotlinx.coroutines.experimental.selects.SelectInstance
import kotlin.coroutines.experimental.CoroutineContext

class StatefulProcessedValueImpl<IN, OUT>(private val state: StatefulValue<IN>,
                                          private val processing: (IN) -> OUT) : StatefulValue<OUT> {
    override val value: OUT
        get() = processing(state.value)

    override fun openSubscription(context: CoroutineContext): ReceiveChannel<OUT> =
            ProcessedReceivedChannel(state.openSubscription(context))

    private inner class ProcessedReceivedChannel(val wrappedChannel: ReceiveChannel<IN>) : ReceiveChannel<OUT> {
        override val isClosedForReceive: Boolean
            get() = wrappedChannel.isClosedForReceive
        override val isEmpty: Boolean
            get() = wrappedChannel.isEmpty
        override val onReceive: SelectClause1<OUT>
            get() = ProcessedSelectCause1(wrappedChannel.onReceive)
        override val onReceiveOrNull: SelectClause1<OUT?>
            get() = ProcessedSelectCause1Nullable(wrappedChannel.onReceiveOrNull)

        override fun cancel(cause: Throwable?): Boolean = wrappedChannel.cancel(cause)
        override fun iterator(): ChannelIterator<OUT> = ProcessedChannelIterator(wrappedChannel.iterator())
        override fun poll(): OUT? = wrappedChannel.poll()?.let(processing)
        override suspend fun receive(): OUT = processing(wrappedChannel.receive())
        override suspend fun receiveOrNull(): OUT? = wrappedChannel.receiveOrNull()?.let(processing)
    }

    private inner class ProcessedChannelIterator(val wrappedChannel: ChannelIterator<IN>) : ChannelIterator<OUT> {
        override suspend fun hasNext(): Boolean = wrappedChannel.hasNext()
        override suspend fun next(): OUT = processing(wrappedChannel.next())
    }

    private inner class ProcessedSelectCause1(val wrappedCause: SelectClause1<IN>) : SelectClause1<OUT> {
        override fun <R> registerSelectClause1(select: SelectInstance<R>, block: suspend (OUT) -> R) =
                wrappedCause.registerSelectClause1(select) { block(processing(it)) }
    }

    private inner class ProcessedSelectCause1Nullable(val wrappedCause: SelectClause1<IN?>) : SelectClause1<OUT?> {
        override fun <R> registerSelectClause1(select: SelectInstance<R>, block: suspend (OUT?) -> R) =
                wrappedCause.registerSelectClause1(select) { block(it?.let(processing)) }
    }
}