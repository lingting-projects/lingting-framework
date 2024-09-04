package live.lingting.framework.function;

/**
 * @author lingting 2023-12-22 11:49
 */
@FunctionalInterface
public interface ThrowingBiConsumerE<T, D, E extends Throwable> {

	void accept(T t, D d) throws E;

}
