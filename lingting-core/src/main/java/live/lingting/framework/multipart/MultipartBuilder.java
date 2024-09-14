package live.lingting.framework.multipart;

import live.lingting.framework.stream.CloneInputStream;
import live.lingting.framework.util.ValueUtils;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * @author lingting 2024-09-14 10:39
 */
@Getter
public class MultipartBuilder {

	private long partSize;

	private String id = ValueUtils.simpleUuid();

	private CloneInputStream source;

	private long size;

	private long maxPartSize;

	private long minPartSize;

	private long maxPartCount;

	public MultipartBuilder partSize(long partSize) {
		this.partSize = partSize;
		return this;
	}

	public MultipartBuilder id(String id) {
		this.id = id;
		return this;
	}

	public MultipartBuilder source(InputStream source) throws IOException {
		this.source = source instanceof CloneInputStream in ? in : new CloneInputStream(source);
		return size(this.source.size());
	}

	public MultipartBuilder source(File file) throws IOException {
		return source(new CloneInputStream(file));
	}

	public MultipartBuilder size(long size) {
		this.size = size;
		return this;
	}

	public MultipartBuilder maxPartSize(long maxPartSize) {
		this.maxPartSize = maxPartSize;
		return this;
	}

	public MultipartBuilder minPartSize(long minPartSize) {
		this.minPartSize = minPartSize;
		return this;
	}

	public MultipartBuilder maxPartCount(long maxPartCount) {
		this.maxPartCount = maxPartCount;
		return this;
	}

	public Collection<Part> parts() {
		if (minPartSize > 0 && partSize < minPartSize) {
			partSize(minPartSize);
		}
		long number = Multipart.calculate(size, partSize);
		// 限制了最大分片数量. 超过之后重新分配每片大小
		if (maxPartCount > 0 && number > maxPartCount) {
			partSize(partSize + (partSize / 2));
			return parts();
		}
		return Multipart.split(size, partSize);
	}

	public Multipart build() {
		Collection<Part> parts = parts();
		File file = source == null ? null : source.file();
		return new Multipart(id, file, size, partSize, parts);
	}

}
