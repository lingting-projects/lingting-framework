package live.lingting.polaris.grpc.server

/**
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
interface DelayRegister {
    /**
     * 允许注册
     * @return 是否可以放通注册动作
     */
    fun allowRegis(): Boolean
}
