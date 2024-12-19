package live.lingting.framework.grpc.simple

import io.grpc.ForwardingServerCallListener
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler

/**
 * @author lingting 2023-12-18 19:10
 */
open class ForwardingServerOnCallListener<ReqT, RespT> protected constructor(
    val call: ServerCall<ReqT, RespT>,
    val headers: Metadata,
    val next: ServerCallHandler<ReqT, RespT>,
) : ForwardingServerCallListener<ReqT>() {

    val delegate: ServerCall.Listener<ReqT> by lazy {
        try {
            next.startCall(call, headers)
        } catch (e: Exception) {
            onFinally()
            throw e
        }
    }

    override fun delegate(): ServerCall.Listener<ReqT> {
        return delegate
    }

    override fun onMessage(message: ReqT) {
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

    open fun onMessageBefore(message: ReqT) {
        //
    }

    open fun onMessageAfter(message: ReqT) {
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
