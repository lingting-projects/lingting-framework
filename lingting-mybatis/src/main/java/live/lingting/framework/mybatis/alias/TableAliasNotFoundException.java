package live.lingting.framework.mybatis.alias;

/**
 * TableAlias 注解没有找到时抛出的异常
 *
 * @author hccake
 * @see TableAlias
 */
public class TableAliasNotFoundException extends RuntimeException {

	public TableAliasNotFoundException() {
	}

	public TableAliasNotFoundException(String message) {
		super(message);
	}

	public TableAliasNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public TableAliasNotFoundException(Throwable cause) {
		super(cause);
	}

}
