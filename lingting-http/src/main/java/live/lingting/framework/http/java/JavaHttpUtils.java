package live.lingting.framework.http.java;

import live.lingting.framework.flow.FutureSubscriber;
import live.lingting.framework.util.FileUtils;
import live.lingting.framework.util.StreamUtils;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author lingting 2024-09-14 17:47
 */
@UtilityClass
public class JavaHttpUtils {

	public static File write(HttpRequest.BodyPublisher publisher) {
		FutureSubscriber<File, ByteBuffer> subscriber = new FutureSubscriber<>() {

			@SneakyThrows
			@Override
			public File convert(List<ByteBuffer> list) {
				File file = FileUtils.createTemp(".http");

				try (FileOutputStream out = new FileOutputStream(file)) {
					for (ByteBuffer buffer : list) {
						int remaining = buffer.remaining();
						byte[] bytes = new byte[remaining];
						buffer.get(bytes);
						out.write(bytes);
					}
				}

				return file;
			}
		};

		publisher.subscribe(subscriber);
		return subscriber.get();
	}

	public static String toString(HttpRequest.BodyPublisher publisher) throws IOException {
		File file = write(publisher);
		try (FileInputStream stream = new FileInputStream(file)) {
			return StreamUtils.toString(stream);
		}
	}

}
