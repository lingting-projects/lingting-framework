package live.lingting.framework.function;

/**
 * @author lingting 2023/1/21 22:56
 */
@FunctionalInterface
@SuppressWarnings("java:S112")
public interface ThrowingSupplier<T> extends ThrowableSupplier<T> {

	@Override
	T get() throws Exception;

}
