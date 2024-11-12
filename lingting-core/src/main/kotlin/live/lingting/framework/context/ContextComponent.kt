package live.lingting.framework.context

/**
 * 上下文组件, 在接入对应的上下文时(如: spring 的 bean) 便于在 开始和结束时执行对应的方法
 *
 *
 * 默认不会自动调用 start 和 stop 方法, 需要手动接入
 *
 *
 *
 * 可以依赖 tools-spring 或者参考 tools-spring 中的代码自己实现自动调用
 *
 *
 *
 * 一般用于线程类实例达成接入到对应的上下文环境时自动开启和结束线程
 *
 *
 * @author lingting 2022/10/15 17:55
 */
interface ContextComponent {
    /**
     * 上下文准备好之后调用, 内部做一些线程的初始化以及线程启动
     */
    fun onApplicationStart()

    /**
     * 上下文销毁前调用, 内部可以做使用wait等待数据处理完毕
     */
    fun onApplicationStopBefore() {
        //
    }

    /**
     * 在上下文销毁时调用, 内部做线程停止和数据缓存相关
     */
    fun onApplicationStop()
}
