package live.lingting.framework.grpc.simple

import io.grpc.ForwardingServerCallListener
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler

/**
 * @author lingting 2023-12-18 19:10
 */
open class ForwardingServerOnCallListener<S, R> protected constructor(
    val call: ServerCall<S, R>,
    val headers: Metadata, next: ServerCallHandler<S, R>,
) : ForwardingServerCallListener<S>() {

    val delegate: ServerCall.Listener<S> by lazy {
        try {
            next.startCall(call, headers)
        } catch (e: Exception) {
            onFinally()
            throw e
        }
    }

    override fun delegate(): ServerCall.Listener<S> {
        return delegate
    }

    override fun onMessage(message: S) {
        onMessageBefore(message)
        super.onMessage(message)
        onMessageAfter(message)
    }

    override fun onHalfClose() {
        onHalfCloseBefore()
        super.onHalfClose()
        onHalfCloseAfter()
        onFinally()
    }

    override fun onCancel() {
        onCancelBefore()
        super.onCancel()
        onCancelAfter()
        onFinally()
    }

    override fun onComplete() {
        onCompleteBefore()
        super.onComplete()
        onCompleteAfter()
        onFinally()
    }

    override fun onReady() {
        onReadyBefore()
        super.onReady()
        onReadyAfter()
    }

    open fun onMessageBefore(message: S) {
        //
    }

    open fun onMessageAfter(message: S) {
        //
    }

    open fun onHalfCloseBefore() {
        //
    }

    open fun onHalfCloseAfter() {
        //
    }

    open fun onCancelBefore() {
        //
    }

    open fun onCancelAfter() {
        //
    }

    open fun onCompleteBefore() {
        //
    }

    open fun onCompleteAfter() {
        //
    }

    open fun onReadyBefore() {
        //
    }

    open fun onReadyAfter() {
        //
    }

    open fun onFinally() {
        //
    }
}
