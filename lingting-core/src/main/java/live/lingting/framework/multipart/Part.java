package live.lingting.framework.multipart;

import lombok.Getter;

/**
 * 分片详情
 * <p>
 * 字节范围为全包. 从第 {@link Part#start} 位到第 {@link Part#end} 个字节
 * </p>
 *
 * @author lingting 2024-09-05 14:47
 */
@Getter
public class Part {

	protected final Long index;

	protected final Long start;

	protected final Long end;

	protected final Long size;

	public Part(Long index, Long start, Long end) {
		this.index = index;
		this.start = start;
		this.end = end;
		this.size = end - start + 1;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Part part = (Part) o;
		return index.equals(part.index) && start.equals(part.start) && end.equals(part.end);
	}

	@Override
	public int hashCode() {
		int result = index.hashCode();
		result = 31 * result + start.hashCode();
		result = 31 * result + end.hashCode();
		return result;
	}

}
