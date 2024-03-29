package live.lingting.framework.thread;

import java.util.Stack;

/**
 * @author lingting 2024-03-29 13:30
 */
public class StackThreadLocal<T> {

	protected final ThreadLocal<Stack<T>> local = new ThreadLocal<>();

	protected Stack<T> getStack() {
		Stack<T> stack = local.get();
		if (stack == null) {
			stack = new Stack<>();
			local.set(stack);
		}
		return stack;
	}

	public void put(T t) {
		Stack<T> stack = getStack();
		stack.push(t);
	}

	public T get() {
		Stack<T> stack = getStack();
		if (stack.empty()) {
			return null;
		}
		return stack.peek();
	}

	public T pop() {
		Stack<T> stack = getStack();
		if (stack.empty()) {
			return null;
		}
		T pop = stack.pop();
		if (stack.empty()) {
			local.remove();
		}
		return pop;
	}

}
