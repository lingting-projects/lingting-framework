package live.lingting.framework.function;

/**
 * @author lingting 2023/1/16 17:46
 */
@FunctionalInterface
@SuppressWarnings("java:S112")
public interface ThrowableFunction<T, R> {

	R apply(T t) throws Throwable;

}
