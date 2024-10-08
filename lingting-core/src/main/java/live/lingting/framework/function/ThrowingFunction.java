package live.lingting.framework.function;

/**
 * @author lingting 2023/2/2 17:36
 */
@FunctionalInterface
@SuppressWarnings("java:S112")
public interface ThrowingFunction<T, R> extends ThrowableFunction<T, R> {

	R apply(T t) throws Exception;

}
