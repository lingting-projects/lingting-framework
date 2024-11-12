package live.lingting.framework.http;

/**
 * @author lingting 2024-09-12 21:45
 */
public enum HttpMethod {

	GET(false), PUT(true), POST(true), DELETE(true), HEAD(false), OPTIONS(true), PATCH(true),

	// region webdav

	/**
	 * 用于跟踪请求-响应链(true),用于诊断。
	 */
	TRACE(true),
	/**
	 * 用于检索资源的属性。
	 */
	PROPFIND(true),
	/**
	 * 用于设置、删除或修改资源的属性。
	 */
	PROPPATCH(true),
	/**
	 * 用于创建新集合（类似于文件夹）。
	 */
	MKCOL(true),
	/**
	 * 用于复制资源。
	 */
	COPY(true),
	/**
	 * 用于移动资源，实际上是一个 COPY + DELETE 操作。
	 */
	MOVE(true),
	/**
	 * 用于对资源进行锁定，以防止并发编辑。
	 */
	LOCK(true),
	/**
	 * 用于解除对资源的锁定。
	 */
	UNLOCK(true),
	/**
	 * 用于执行自定义的报告请求，这是 WebDAV 分布式协作特性的一部分。
	 */
	REPORT(true),
	/**
	 * 用于版本控制，分别表示签出和签入资源。
	 */
	CHECKOUT(true),
	/**
	 * 用于版本控制，分别表示签出和签入资源。
	 */
	CHECKIN(true),
	/**
	 * 用于取消签出状态。
	 */
	UNCHECKOUT(true),
	/**
	 * 用于将资源置于版本控制之下。
	 */
	VERSION_CONTROL(true),
	/**
	 * 用于合并不同版本的资源。
	 */
	MERGE(true),

	// endregion

	// region caldav carddav
	/* 用于创建新的日历资源。 */
	MKCALENDAR(true),
	/**
	 * 用于检索多个通讯录条目
	 */
	ADDRESSBOOK_MULTIGET(true),
	/**
	 * 用于搜索通讯录中的条目
	 */
	ADDRESSBOOK_QUERY(true),

	// endregion

	;

	private final boolean supportBody;

	private HttpMethod(boolean supportBody) {
		this.supportBody = supportBody;
	}

	public boolean allowBody() {
		return supportBody;
	}

	public boolean isSupportBody() {return this.supportBody;}
}
