package live.lingting.framework.system;

import live.lingting.framework.util.StreamUtils;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.Duration;

/**
 * @author lingting 2022/6/25 12:01
 */
@Getter
public class CommandResult {

	protected final Command command;

	protected final int exitCode;

	protected final long end;

	protected final Duration duration;

	protected String stdOut;

	protected String stdErr;

	public CommandResult(Command command, int exitCode) {
		this.command = command;
		this.exitCode = exitCode;
		this.end = System.currentTimeMillis();
		this.duration = Duration.ofMillis(end - command.startTime);
	}

	@SneakyThrows
	public synchronized String getStdOut() {
		if (stdOut == null) {
			InputStream stream = stdOut();
			stdOut = new String(StreamUtils.read(stream), command.charset);
		}
		return stdOut;
	}

	@SneakyThrows
	public synchronized String getStdErr() {
		if (stdErr == null) {
			InputStream stream = stdErr();
			stdErr = new String(StreamUtils.read(stream), command.charset);
		}
		return stdErr;
	}

	public InputStream stdOut() throws IOException {
		return Files.newInputStream(command.stdOut.toPath());
	}

	public InputStream stdErr() throws IOException {
		return Files.newInputStream(command.stdErr.toPath());
	}

	public void clean() {
		command.clean();
	}

}
