package live.lingting.framework.function;

/**
 * @author lingting 2023/1/21 22:56
 */
@FunctionalInterface
@SuppressWarnings("java:S112")
public interface ThrowingConsumer<T> extends ThrowableConsumer<T> {

	@Override
	void accept(T t) throws Throwable;

}
