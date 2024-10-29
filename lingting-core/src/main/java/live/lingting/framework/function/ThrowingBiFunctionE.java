package live.lingting.framework.function;

/**
 * @author lingting 2023/2/2 17:36
 */
@FunctionalInterface
@SuppressWarnings("java:S112")
public interface ThrowingBiFunctionE<T, D, R, E extends Exception> {

	R apply(T t, D d) throws E;

}
