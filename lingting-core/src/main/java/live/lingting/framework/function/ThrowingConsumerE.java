package live.lingting.framework.function;

/**
 * @author lingting 2023-12-22 11:49
 */
@FunctionalInterface
public interface ThrowingConsumerE<T, E extends Throwable> {

	void accept(T t) throws E;

}
