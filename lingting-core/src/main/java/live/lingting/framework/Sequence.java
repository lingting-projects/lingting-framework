package live.lingting.framework;

import live.lingting.framework.util.ClassUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * 排序用
 *
 * @author lingting 2024-01-30 11:15
 */
public interface Sequence {

	SequenceComparator INSTANCE_ASC = new SequenceComparator(true, 0);

	SequenceComparator INSTANCE_DESC = new SequenceComparator(false, 0);

	static <T> void asc(List<T> list) {
		list.sort(INSTANCE_ASC);
	}

	static <T> List<T> asc(Collection<T> collection) {
		return collection.stream().sorted(INSTANCE_ASC).toList();
	}

	static <T> void desc(List<T> list) {
		list.sort(INSTANCE_DESC);
	}

	static <T> List<T> desc(Collection<T> collection) {
		return collection.stream().sorted(INSTANCE_DESC).toList();
	}

	int getSequence();

	@RequiredArgsConstructor
	class SequenceComparator implements Comparator<Object> {

		private final boolean isAsc;

		private final int defaultSequence;

		@Override
		public int compare(Object o1, Object o2) {
			int i1 = find(o1);
			int i2 = find(o2);

			if (i1 == i2) {
				return 0;
			}

			boolean isLeft = isAsc ? i1 < i2 : i1 > i2;
			// 是否o1排o2前面
			return isLeft ? -1 : 1;
		}

		/**
		 * 获取当前排序规则内, 最低优先级的值
		 */
		protected int lowerSequence() {
			return isAsc ? Integer.MAX_VALUE : Integer.MIN_VALUE;
		}

		/**
		 * 返回排序在前面的优先级
		 */
		protected int high(int o1, int o2) {
			return isAsc ? Math.min(o1, o2) : Math.max(o1, o2);
		}

		protected int find(Object obj) {
			if (obj == null) {
				return defaultSequence;
			}

			int orderSequence = obj instanceof Sequence sequence ? sequence.getSequence() : defaultSequence;
			int orderSpring = findBySpring(obj);
			return high(orderSequence, orderSpring);
		}

		protected int findBySpring(Object obj) {
			if (!ClassUtils.isPresent("org.springframework.core.annotation.Order", getClass().getClassLoader())) {
				return defaultSequence;
			}
			// 注解上的排序值
			int oa = defaultSequence;
			// 类方法上的排序值
			int om = defaultSequence;

			Order annotation = obj.getClass().getAnnotation(Order.class);
			if (annotation != null) {
				oa = annotation.value();
			}

			if (obj instanceof Ordered ordered) {
				om = ordered.getOrder();
			}

			return high(oa, om);
		}

	}

}
