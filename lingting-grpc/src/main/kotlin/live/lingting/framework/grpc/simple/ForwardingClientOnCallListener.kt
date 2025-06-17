package live.lingting.framework.grpc.simple

import io.grpc.ClientCall
import io.grpc.ForwardingClientCallListener
import io.grpc.Metadata
import io.grpc.Status

/**
 * @author lingting 2024/12/19 20:30
 */
open class ForwardingClientOnCallListener<RespT>(private val delegate: ClientCall.Listener<RespT>) : ForwardingClientCallListener<RespT>() {

    override fun delegate(): ClientCall.Listener<RespT> = delegate

    open fun onMessageBefore(message: RespT) {
        //
    }

    override fun onMessage(message: RespT) {
        onMessageBefore(message)
        super.onMessage(message)
        onMessageAfter(message)
    }

    open fun onMessageAfter(message: RespT) {
        //
    }

    open fun onHeadersBefore(headers: Metadata) {
        //
    }

    override fun onHeaders(headers: Metadata) {
        onHeadersBefore(headers)
        super.onHeaders(headers)
        onHeadersAfter(headers)
    }

    open fun onHeadersAfter(headers: Metadata) {
        //
    }

    open fun onCloseBefore(status: Status, trailers: Metadata) {
        //
    }

    override fun onClose(status: Status, trailers: Metadata) {
        onCloseBefore(status, trailers)
        super.onClose(status, trailers)
        onCloseAfter(status, trailers)
    }

    open fun onCloseAfter(status: Status, trailers: Metadata) {
        //
    }

    open fun onReadyBefore() {
        //
    }

    override fun onReady() {
        onReadyBefore()
        super.onReady()
        onReadyAfter()
    }

    open fun onReadyAfter() {
        //
    }
}
