package live.lingting.framework.download;

import live.lingting.framework.exception.DownloadException;
import live.lingting.framework.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author lingting 2024-01-17 10:09
 */
public interface Download {

	Download start();

	Download await();

	boolean isStart();

	boolean isFinished();

	boolean isSuccess();

	File getFile() throws DownloadException;

	/**
	 * 使用下载文件覆盖指定文件
	 */
	default File transferTo(File file) throws DownloadException, IOException {
		File source = getFile();
		FileUtils.copy(source, file, true);
		return file;
	}

}
