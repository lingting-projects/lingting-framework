package live.lingting.framework.grpc.simple

import io.grpc.ForwardingServerCallListener
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler

/**
 * @author lingting 2023-12-18 19:10
 */
open class ForwardingServerOnCallListener<S, R> protected constructor(call: ServerCall<S?, R?>?, headers: Metadata?, next: ServerCallHandler<S?, R?>) : ForwardingServerCallListener<S?>() {
    private var delegate: ServerCall.Listener<S?>? = null

    init {
        try {
            this.delegate = next.startCall(call, headers)
        } catch (e: Exception) {
            onFinally()
            throw e
        }
    }

    override fun delegate(): ServerCall.Listener<S?>? {
        return delegate
    }

    override fun onMessage(message: S?) {
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

    fun onMessageBefore(message: S?) {
        //
    }

    fun onMessageAfter(message: S?) {
        //
    }

    fun onHalfCloseBefore() {
        //
    }

    fun onHalfCloseAfter() {
        //
    }

    fun onCancelBefore() {
        //
    }

    fun onCancelAfter() {
        //
    }

    fun onCompleteBefore() {
        //
    }

    fun onCompleteAfter() {
        //
    }

    fun onReadyBefore() {
        //
    }

    fun onReadyAfter() {
        //
    }

    open fun onFinally() {
        //
    }
}
