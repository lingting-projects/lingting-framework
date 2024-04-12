package live.lingting.framework.thread;

import java.util.Stack;

/**
 * @author lingting 2024-03-29 13:30
 */
public class StackThreadLocal<T> {

	protected final ThreadLocal<Stack<T>> local = ThreadLocal.withInitial(Stack::new);

	public void put(T t) {
		Stack<T> stack = local.get();
		stack.push(t);
	}

	public T get() {
		Stack<T> stack = local.get();
		if (stack.empty()) {
			return null;
		}
		return stack.peek();
	}

	public T pop() {
		Stack<T> stack = local.get();
		if (stack.empty()) {
			return null;
		}
		return stack.pop();
	}

}
