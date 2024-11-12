package live.lingting.framework.grpc.simple;

import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;

/**
 * @author lingting 2023-12-18 19:10
 */

public class ForwardingServerOnCallListener<S, R> extends ForwardingServerCallListener<S> {

	private final ServerCall.Listener<S> delegate;

	protected ForwardingServerOnCallListener(ServerCall<S, R> call, Metadata headers, ServerCallHandler<S, R> next) {
		try {
			this.delegate = next.startCall(call, headers);
		}
		catch (Exception e) {
			onFinally();
			throw e;
		}
	}

	@Override
	protected ServerCall.Listener<S> delegate() {
		return delegate;
	}

	@Override
	public void onMessage(S message) {
		onMessageBefore(message);
		super.onMessage(message);
		onMessageAfter(message);
	}

	@Override
	public void onHalfClose() {
		onHalfCloseBefore();
		super.onHalfClose();
		onHalfCloseAfter();
		onFinally();
	}

	@Override
	public void onCancel() {
		onCancelBefore();
		super.onCancel();
		onCancelAfter();
		onFinally();
	}

	@Override
	public void onComplete() {
		onCompleteBefore();
		super.onComplete();
		onCompleteAfter();
		onFinally();
	}

	@Override
	public void onReady() {
		onReadyBefore();
		super.onReady();
		onReadyAfter();
	}

	public void onMessageBefore(S message) {
		//
	}

	public void onMessageAfter(S message) {
		//
	}

	public void onHalfCloseBefore() {
		//
	}

	public void onHalfCloseAfter() {
		//
	}

	public void onCancelBefore() {
		//
	}

	public void onCancelAfter() {
		//
	}

	public void onCompleteBefore() {
		//
	}

	public void onCompleteAfter() {
		//
	}

	public void onReadyBefore() {
		//
	}

	public void onReadyAfter() {
		//
	}

	public void onFinally() {
		//
	}

}
