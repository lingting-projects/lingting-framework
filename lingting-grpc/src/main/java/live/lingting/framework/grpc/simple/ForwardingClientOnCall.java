package live.lingting.framework.grpc.simple;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

/**
 * @author lingting 2023-12-18 19:13
 */
public class ForwardingClientOnCall<S, R> extends ForwardingClientCall<S, R> {

	private final ClientCall<S, R> delegate;

	public ForwardingClientOnCall(MethodDescriptor<S, R> method, CallOptions callOptions, Channel next) {
		this(next.newCall(method, callOptions));
	}

	public ForwardingClientOnCall(ClientCall<S, R> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected ClientCall<S, R> delegate() {
		return delegate;
	}

	@Override
	public void start(Listener<R> responseListener, Metadata headers) {
		onStartBefore(responseListener, headers);
		super.start(responseListener, headers);
		onStartAfter(responseListener, headers);
	}

	@Override
	public void sendMessage(S message) {
		onSendMessageBefore(message);
		super.sendMessage(message);
		onSendMessageAfter(message);
	}

	@Override
	public void halfClose() {
		onHalfCloseBefore();
		super.halfClose();
		onHalfCloseAfter();
	}

	public void onStartBefore(Listener<R> responseListener, Metadata headers) {
		//
	}

	public void onStartAfter(Listener<R> responseListener, Metadata headers) {
		//
	}

	public void onSendMessageBefore(S message) {
		//
	}

	public void onSendMessageAfter(S message) {
		//
	}

	public void onHalfCloseBefore() {
		//
	}

	public void onHalfCloseAfter() {
		//
	}

}
