package live.lingting.framework.system;

import live.lingting.framework.util.FileUtils;
import live.lingting.framework.util.StringUtils;
import live.lingting.framework.util.SystemUtils;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author lingting 2022/6/25 11:55
 */
@SuppressWarnings("java:S1845")
public class Command {

	public static final File TEMP_DIR = FileUtils.createTempDir("command");

	public static final String ENTER = SystemUtils.lineSeparator();

	public static final String EXIT = "exit";

	@Getter
	protected final String init;

	protected final Process process;

	protected final OutputStream stdIn;

	/**
	 * 标准输出
	 */
	protected final File stdOut;

	protected final File stdErr;

	@Getter
	protected final String enter;

	@Getter
	protected final String exit;

	@Getter
	protected final Charset charset;

	@Getter
	protected final Long startTime;

	protected final List<String> history = new ArrayList<>();

	protected Command(String init, String enter, String exit, Charset charset) throws IOException {
		if (!StringUtils.hasText(init)) {
			throw new IllegalArgumentException("Empty init");
		}
		this.init = init;
		StringTokenizer st = new StringTokenizer(init);
		String[] array = new String[st.countTokens()];
		for (int i = 0; st.hasMoreTokens(); i++) {
			array[i] = st.nextToken();
		}

		this.stdOut = FileUtils.createTemp(".out", TEMP_DIR);
		this.stdErr = FileUtils.createTemp(".err", TEMP_DIR);

		// 重定向标准输出和标准错误到文件, 避免写入到缓冲区然后占满导致 waitFor 死锁
		ProcessBuilder builder = new ProcessBuilder(array).redirectError(stdErr).redirectOutput(stdOut);
		this.process = builder.start();
		this.stdIn = process.getOutputStream();
		this.enter = enter;
		this.exit = exit;
		this.charset = charset;
		this.startTime = System.currentTimeMillis();
	}

	/**
	 * 获取命令操作实例. 此实例默认使用系统字符集, 如果发现部分带非英文字符和特殊符号命令执行异常, 建议使用
	 * {@link Command#of(String, Charset)} 自定义对应的字符集
	 *
	 * @param init 初始命令
	 */
	public static Command of(String init) throws IOException {
		return of(init, SystemUtils.charset());
	}

	/**
	 * 推荐使用此实例
	 */
	public static Command of(String init, Charset charset) throws IOException {
		return of(init, ENTER, EXIT, charset);
	}

	public static Command of(String init, String enter, String exit, Charset charset) throws IOException {
		return new Command(init, enter, exit, charset);
	}

	public List<String> history() {
		return Collections.unmodifiableList(history);
	}

	public Command write(String str) throws IOException {
		byte[] bytes = str.getBytes(charset);
		stdIn.write(bytes);
		stdIn.flush();
		if (!enter.equals(str)) {
			history.add(str);
		}
		return this;
	}

	/**
	 * 换到下一行
	 */
	public Command enter() throws IOException {
		return write(enter);
	}

	/**
	 * 写入通道退出指令
	 */
	public Command exit() throws IOException {
		write(exit);
		return enter();
	}

	/**
	 * 写入并执行一行指令
	 *
	 * @param str 单行指令
	 */
	public Command exec(String str) throws IOException {
		write(str);
		return enter();
	}

	/**
	 * 获取执行结果, 并退出
	 * <p>
	 * 注意: 如果套娃了多个通道, 则需要手动退出套娃的通道
	 * </p>
	 * <p>
	 * 例如: eg: exec("ssh ssh.lingting.live").exec("ssh ssh.lingting.live").exec("ssh
	 * ssh.lingting.live")
	 * </p>
	 * <p>
	 * 需要: eg: exit().exit().exit()
	 * </p>
	 */
	public CommandResult waitFor() throws InterruptedException {
		int i = process.waitFor();
		return new CommandResult(this, i);
	}

	/**
	 * 等待命令执行完成
	 * <h3>如果 process 是通过 {@link Runtime#exec}方法构建的, 那么{@link Process#waitFor}方法可能会导致线程卡死,
	 * 具体原因如下</h3>
	 * <p>
	 * 终端缓冲区大小有限, 在缓冲区被写满之后, 会子线程会挂起,等待缓冲区内容被读, 然后才继续写. 如果此时主线程也在waitFor()等待子线程结束, 就卡死了
	 * </p>
	 * <p>
	 * 即便是先读取返回结果在调用此方法也可能会导致卡死. 比如: 先读取标准输出流, 还没读完, 缓冲区被错误输出流写满了.
	 * </p>
	 *
	 * @param millis 等待时间, 单位: 毫秒
	 * @return live.lingting.tools.system.CommandResult
	 */
	public CommandResult waitFor(long millis) throws InterruptedException, TimeoutException {
		// 超时
		if (!process.waitFor(millis, TimeUnit.MILLISECONDS)) {
			throw new TimeoutException();
		}
		int i = process.exitValue();
		return new CommandResult(this, i);
	}

	public void destroy() {
		process.destroy();
	}

	public void destroyForcibly() {
		process.destroyForcibly();
	}

	public void clean() {
		FileUtils.delete(stdOut);
		FileUtils.delete(stdErr);
	}

	/**
	 * 清空历史记录
	 *
	 * @return 返回被清除的数据
	 */
	public List<String> cleanHistory() {
		List<String> back = new ArrayList<>(history);
		history.clear();
		return back;
	}

}
