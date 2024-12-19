package live.lingting.framework.grpc.simple

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ForwardingClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor

/**
 * @author lingting 2023-12-18 19:13
 */
open class ForwardingClientOnCall<S, R>(
    method: MethodDescriptor<S, R>,
    callOptions: CallOptions, next: Channel,
) : ForwardingClientCall<S, R>() {

    val delegate: ClientCall<S, R> by lazy {
        try {
            next.newCall(method, callOptions)
        } catch (e: Exception) {
            onFinally()
            throw e
        }
    }

    override fun delegate(): ClientCall<S, R> {
        return delegate
    }

    override fun start(responseListener: Listener<R>, headers: Metadata) {
        onStartBefore(responseListener, headers)
        super.start(responseListener, headers)
        onStartAfter(responseListener, headers)
    }

    override fun sendMessage(message: S) {
        onSendMessageBefore(message)
        super.sendMessage(message)
        onSendMessageAfter(message)
    }

    override fun halfClose() {
        onHalfCloseBefore()
        super.halfClose()
        onHalfCloseAfter()
        onFinally()
    }

    open fun onStartBefore(responseListener: Listener<R>, headers: Metadata) {
        //
    }

    open fun onStartAfter(responseListener: Listener<R>, headers: Metadata) {
        //
    }

    open fun onSendMessageBefore(message: S) {
        //
    }

    open fun onSendMessageAfter(message: S) {
        //
    }

    open fun onHalfCloseBefore() {
        //
    }

    open fun onHalfCloseAfter() {
        //
    }

    open fun onFinally() {
        //
    }
}
