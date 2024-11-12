
package live.lingting.polaris.grpc.server;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public interface DelayRegister {

	/**
	 * 允许注册
	 * @return 是否可以放通注册动作
	 */
	boolean allowRegis();

}
