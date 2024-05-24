package live.lingting.framework.thread;

import live.lingting.framework.time.StopWatch;
import live.lingting.framework.util.CollectionUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 顶级队列线程类
 *
 * @author lingting 2021/3/2 15:07
 */
@SuppressWarnings("java:S2142")
public abstract class AbstractQueueThread<E> extends AbstractThreadContextComponent {

	/**
	 * 默认缓存数据数量
	 */
	public static final int DEFAULT_BATCH_SIZE = 500;

	/**
	 * 默认等待时长 30秒
	 */
	public static final Duration DEFAULT_BATCH_TIMEOUT = Duration.ofSeconds(30);

	/**
	 * 默认获取数据时的超时时间
	 */
	public static final Duration POLL_TIMEOUT = Duration.ofSeconds(5);

	protected final List<E> data = new ArrayList<>(getBatchSize());

	/**
	 * 用于子类自定义缓存数据数量
	 * @return long
	 */
	public int getBatchSize() {
		return DEFAULT_BATCH_SIZE;
	}

	/**
	 * 用于子类自定义等待时长
	 * @return 返回时长，单位毫秒
	 */
	public Duration getBatchTimeout() {
		return DEFAULT_BATCH_TIMEOUT;
	}

	/**
	 * 用于子类自定义 获取数据的超时时间
	 * @return 返回时长，单位毫秒
	 */
	public Duration getPollTimeout() {
		return POLL_TIMEOUT;
	}

	/**
	 * 往队列插入数据
	 * @param e 数据
	 */
	public abstract void put(E e);

	/**
	 * 数据处理前执行
	 */
	protected void preProcess() {
	}

	/**
	 * 从队列中取值
	 * @param timeout 等待时长
	 * @return E
	 * @throws InterruptedException 线程中断
	 */
	protected abstract E poll(Duration timeout) throws InterruptedException;

	/**
	 * 处理单个接收的数据
	 * @param e 接收的数据
	 * @return 返回要放入队列的数据
	 */
	protected E process(E e) {
		return e;
	}

	/**
	 * 处理所有已接收的数据
	 * @param list 所有已接收的数据
	 * @throws Exception 异常
	 */
	@SuppressWarnings("java:S112")
	protected abstract void process(List<E> list) throws Exception;

	@Override
	@SuppressWarnings("java:S1181")
	protected void doRun() throws Exception {
		preProcess();
		fill();
		if (!CollectionUtils.isEmpty(data)) {
			process(new ArrayList<>(data));
			data.clear();
		}
	}

	/**
	 * 填充数据
	 */
	protected void fill() {
		int count = 0;
		StopWatch watch = new StopWatch();
		while (count < getBatchSize()) {
			E e = poll();
			E p = process(e);

			if (p != null) {
				// 第一次插入数据
				if (count++ == 0) {
					// 记录时间
					watch.restart();
				}
				// 数据存入列表
				data.add(p);
			}

			// 需要进行处理数据了
			if (isBreak(watch)) {
				break;
			}
		}
	}

	/**
	 * 是否中断数据填充
	 */
	protected boolean isBreak(StopWatch watch) {
		// 该停止运行了
		if (!isRun()) {
			return true;
		}

		// 已有数据且超过设定的等待时间
		return isTimeout(watch);
	}

	/**
	 * 已有数据且超过设定的等待时间
	 */
	protected boolean isTimeout(StopWatch watch) {
		return !CollectionUtils.isEmpty(data) && watch.timeMillis() >= getBatchTimeout().toMillis();
	}

	public E poll() {
		E e = null;
		try {
			Duration duration = getPollTimeout();
			e = poll(duration);
		}
		catch (InterruptedException ex) {
			log.error("Class: {}; ThreadId: {}; poll interrupted!", getSimpleName(), threadId());
			interrupt();
		}
		return e;
	}

	/**
	 * 线程被中断后的处理. 如果有缓存手段可以让数据进入缓存.
	 */
	@Override
	protected void shutdown() {
		log.warn("Class: {}; ThreadId: {}; shutdown! data: {}", getSimpleName(), threadId(), data);
	}

}
