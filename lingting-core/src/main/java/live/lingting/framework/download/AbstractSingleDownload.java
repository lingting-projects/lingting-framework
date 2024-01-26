package live.lingting.framework.download;

import live.lingting.framework.exception.DownloadException;
import live.lingting.framework.util.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * @author lingting 2024-01-16 19:42
 */
public abstract class AbstractSingleDownload<D extends AbstractSingleDownload<D>> extends AbstractDownload<D> {

	protected <B extends AbstractDownloadBuilder<B>> AbstractSingleDownload(B builder) {
		super(builder);
	}

	@Override
	protected void doStart() {
		File target = createTarget();
		try {
			OutputStream out = Files.newOutputStream(target.toPath());
			// 写入流到文件
			write(out);
			// 关闭流
			StreamUtils.close(out);
		}
		catch (IOException e) {
			throw new DownloadException("file download io exception! ", e);
		}
		finally {
			finished = true;
		}
	}

	/**
	 * 获取url对应的流并且写入到输出流
	 * @param out 数据写入到此流
	 * @throws IOException 异常
	 */
	protected abstract void write(OutputStream out) throws IOException;

}
