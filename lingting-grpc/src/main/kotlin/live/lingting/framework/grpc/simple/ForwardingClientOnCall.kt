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
open class ForwardingClientOnCall<ReqT, RespT>(
    method: MethodDescriptor<ReqT, RespT>,
    callOptions: CallOptions, next: Channel,
) : ForwardingClientCall<ReqT, RespT>() {

    val delegate: ClientCall<ReqT, RespT> by lazy {
        try {
            next.newCall(method, callOptions)
        } catch (e: Exception) {
            onFinally()
            throw e
        }
    }

    override fun delegate(): ClientCall<ReqT, RespT> {
        return delegate
    }

    override fun start(responseListener: Listener<RespT>, headers: Metadata) {
        onStartBefore(responseListener, headers)
        super.start(responseListener, headers)
        onStartAfter(responseListener, headers)
    }

    override fun sendMessage(message: ReqT) {
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

    open fun onStartBefore(responseListener: Listener<RespT>, headers: Metadata) {
        //
    }

    open fun onStartAfter(responseListener: Listener<RespT>, headers: Metadata) {
        //
    }

    open fun onSendMessageBefore(message: ReqT) {
        //
    }

    open fun onSendMessageAfter(message: ReqT) {
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
