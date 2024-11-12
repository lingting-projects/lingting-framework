package live.lingting.framework.stream;

import live.lingting.framework.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * 克隆输入流, 可直接读取, 也可以克隆出一个新流然后读取
 * <p>
 * 当直接读取时, 所有行为和文件流一致
 * </p>
 *
 * @author lingting 2024-01-09 15:41
 */
@SuppressWarnings("java:S1170")
public class FileCloneInputStream extends CloneInputStream {

	public FileCloneInputStream(File source) throws IOException {
		this(source, Files.size(source.toPath()));
	}

	public FileCloneInputStream(File source, long size) {
		super(source, size);
	}

	public FileCloneInputStream(FileCloneInputStream input) {
		this(input.source(), input.size());
	}

	public FileCloneInputStream(InputStream input) throws IOException {
		this(input instanceof FileCloneInputStream clone ? clone.source()
				: FileUtils.createTemp(input, ".clone", TEMP_DIR));
	}

	@Override
	protected InputStream newStream() throws IOException {
		return new FileInputStream(source());
	}


	@Override
	public FileCloneInputStream copy() {
		return new FileCloneInputStream(this);
	}

	@Override
	public File source() {
		return (File) source;
	}

	@Override
	public void clear() {
		FileUtils.delete(source());
	}

}
