package live.lingting.framework.grpc.simple;

import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;

/**
 * @author lingting 2023-12-18 19:10
 */

public class ForwardingServerOnCallListener<S> extends ForwardingServerCallListener<S> {

	private final ServerCall.Listener<S> delegate;

	protected <R> ForwardingServerOnCallListener(ServerCall<S, R> call, Metadata headers,
			ServerCallHandler<S, R> next) {
		this(next.startCall(call, headers));
	}

	protected ForwardingServerOnCallListener(ServerCall.Listener<S> delegate) {
		this.delegate = delegate;
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
	}

	@Override
	public void onCancel() {
		onCancelBefore();
		super.onCancel();
		onCancelAfter();
	}

	@Override
	public void onComplete() {
		onCompleteBefore();
		super.onComplete();
		onCompleteAfter();
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

}
