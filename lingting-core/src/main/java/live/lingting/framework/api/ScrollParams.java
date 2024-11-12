package live.lingting.framework.api;

/**
 * @author lingting 2024-02-02 17:54
 */
public class ScrollParams<T> {

	private Long size = 10L;

	private T cursor;

	public ScrollParams(Long size, T cursor) {
		this.size = size;
		this.cursor = cursor;
	}

	public ScrollParams() {}

	public Long getSize() {return this.size;}

	public T getCursor() {return this.cursor;}

	public ScrollParams<T> setSize(Long size) {
		this.size = size;
		return this;
	}

	public ScrollParams<T> setCursor(T cursor) {
		this.cursor = cursor;
		return this;
	}
}
