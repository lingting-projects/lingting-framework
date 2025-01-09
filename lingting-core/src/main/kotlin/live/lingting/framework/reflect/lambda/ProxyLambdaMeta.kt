package live.lingting.framework.reflect.lambda

import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandleProxies
import java.lang.invoke.MethodHandles
import java.lang.reflect.Executable
import java.lang.reflect.Proxy
import live.lingting.framework.reflect.LambdaMeta
import live.lingting.framework.util.ClassUtils

/**
 * @author lingting 2025/1/9 10:33
 */
class ProxyLambdaMeta(val p: Proxy) : LambdaMeta() {

    val target: MethodHandle by lazy { MethodHandleProxies.wrapperInstanceTarget(p) }

    val executable: Executable by lazy { MethodHandles.reflectAs(Executable::class.java, target) }

    override val wrapperCls: Class<*> = p.javaClass

    override val cls: Class<*> by lazy { executable.declaringClass }

    override val field: String by lazy { executable.name.let { ClassUtils.toFiledName(it) } }

}
