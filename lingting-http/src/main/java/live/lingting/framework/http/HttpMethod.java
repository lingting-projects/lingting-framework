package live.lingting.framework.http;

/**
 * @author lingting 2024-09-12 21:45
 */
public enum HttpMethod {

	GET, PUT, POST, DELETE, HEAD, OPTIONS, PATCH,

	// region webdav

	/** 用于跟踪请求-响应链,用于诊断。 */
	TRACE,
	/** 用于检索资源的属性。 */
	PROPFIND,
	/** 用于设置、删除或修改资源的属性。 */
	PROPPATCH,
	/** 用于创建新集合（类似于文件夹）。 */
	MKCOL,
	/** 用于复制资源。 */
	COPY,
	/** 用于移动资源，实际上是一个 COPY + DELETE 操作。 */
	MOVE,
	/** 用于对资源进行锁定，以防止并发编辑。 */
	LOCK,
	/** 用于解除对资源的锁定。 */
	UNLOCK,
	/** 用于执行自定义的报告请求，这是 WebDAV 分布式协作特性的一部分。 */
	REPORT,
	/** 用于版本控制，分别表示签出和签入资源。 */
	CHECKOUT,
	/** 用于版本控制，分别表示签出和签入资源。 */
	CHECKIN,
	/** 用于取消签出状态。 */
	UNCHECKOUT,
	/** 用于将资源置于版本控制之下。 */
	VERSION_CONTROL,
	/** 用于合并不同版本的资源。 */
	MERGE,

	// endregion

	// region caldav carddav
	/* 用于创建新的日历资源。 */
	MKCALENDAR,
	/** 用于检索多个通讯录条目 */
	ADDRESSBOOK_MULTIGET,
	/** 用于搜索通讯录中的条目 */
	ADDRESSBOOK_QUERY,

	// endregion

	;

}
