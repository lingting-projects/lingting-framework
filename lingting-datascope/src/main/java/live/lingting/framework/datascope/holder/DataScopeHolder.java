package live.lingting.framework.datascope.holder;

import live.lingting.framework.datascope.DataScope;
import lombok.experimental.UtilityClass;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@UtilityClass
public class DataScopeHolder {

	/**
	 * 使用栈存储 List<DataScope>，便于在方法嵌套调用时使用不同的数据权限控制。
	 */
	private static final ThreadLocal<Deque<List<DataScope>>> DATA_SCOPES = ThreadLocal.withInitial(ArrayDeque::new);

	/**
	 * 获取当前的 dataScopes
	 * @return List<DataScope>
	 */
	public static List<DataScope> peek() {
		Deque<List<DataScope>> deque = DATA_SCOPES.get();
		return deque == null ? new ArrayList<>() : deque.peek();
	}

	/**
	 * 入栈一组 dataScopes
	 */
	public static void push(List<DataScope> dataScopes) {
		Deque<List<DataScope>> deque = DATA_SCOPES.get();
		if (deque == null) {
			deque = new ArrayDeque<>();
		}
		deque.push(dataScopes);
	}

	/**
	 * 弹出最顶部 dataScopes
	 */
	public static void poll() {
		Deque<List<DataScope>> deque = DATA_SCOPES.get();
		deque.poll();
		// 当没有元素时，清空 ThreadLocal
		if (deque.isEmpty()) {
			clear();
		}
	}

	/**
	 * 清除 TreadLocal
	 */
	private static void clear() {
		DATA_SCOPES.remove();
	}

}
