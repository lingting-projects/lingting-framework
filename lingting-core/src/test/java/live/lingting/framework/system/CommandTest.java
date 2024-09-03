package live.lingting.framework.system;

import live.lingting.framework.util.StreamUtils;
import live.lingting.framework.util.SystemUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-01-26 16:41
 */
class CommandTest {

	@Test
	void test() throws IOException, InterruptedException {
		CommandResult result;
		if (SystemUtils.isWindows()) {
			result = testWindows();
		}
		else {
			result = testLinux();
		}
		Command command = result.getCommand();
		File out = command.stdOut;
		assertTrue(Files.size(out.toPath()) > 0);
		String stdOut = result.getStdOut();
		try (InputStream stream = result.stdOut()) {
			byte[] bytes = StreamUtils.read(stream);
			String string = new String(bytes, StandardCharsets.UTF_8);
			System.out.println(string);
			assertEquals(stdOut, string);
		}
	}

	CommandResult testLinux() throws InterruptedException, IOException {
		Command command = Command.of("sh", StandardCharsets.UTF_8);
		command.exec("ls");
		command.exit();
		return command.waitFor();
	}

	CommandResult testWindows() throws IOException, InterruptedException {
		Command command = Command.of("cmd", StandardCharsets.UTF_8);
		command.exec("dir");
		command.exit();
		return command.waitFor();
	}

}
