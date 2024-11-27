package live.lingting.framework.reflect

import java.lang.invoke.MethodHandleProxies
import java.lang.invoke.MethodHandles
import java.lang.invoke.SerializedLambda
import java.lang.reflect.Executable
import java.lang.reflect.Proxy
import live.lingting.framework.util.ClassUtils

/**
 * 从匿名函数解析对应调用的字段
 * @author lingting 2024/11/27 20:38
 */
class LambdaMeta(
    val cls: Class<*>,
    val field: String,
) {

    companion object {

        @JvmField
        val FIELD_METHOD_START = setOf("is", "get", "set")

        @JvmStatic
        fun of(a: Any): LambdaMeta {
            if (a is Proxy) {
                return of(a)
            }

            val cls = a.javaClass
            val loader = cls.classLoader
            val cf = ClassUtils.method(cls, "writeReplace")!!.apply { isAccessible = true }
            val lambda = cf.invoke(a) as SerializedLambda
            return of(lambda, loader)
        }

        @JvmStatic
        fun of(proxy: Proxy): LambdaMeta {
            val target = MethodHandleProxies.wrapperInstanceTarget(proxy)
            val executable = MethodHandles.reflectAs(Executable::class.java, target)
            val cls = executable.declaringClass
            val method = executable.name
            val field = ClassUtils.toFiledName(method)
            return LambdaMeta(cls, field)
        }

        @JvmStatic
        fun of(lambda: SerializedLambda, loader: ClassLoader): LambdaMeta {
            val cls = lambda.instantiatedMethodType.let {
                it.substring(2, it.indexOf(";")).replace("/", ".")
            }.let {
                ClassUtils.loadClass(it, loader)
            }

            val method = lambda.implMethodName
            val field = FIELD_METHOD_START.firstOrNull {
                method.startsWith(it)
            }.let {
                if (it != null) {
                    return@let it
                }

                lambda.getCapturedArg(0).toString().let {
                    it.substring(
                        it.indexOf(cls.simpleName) + cls.simpleName.length + 1,
                        it.indexOf(":")
                    )
                }
            }.let {
                ClassUtils.toFiledName(it)
            }

            return LambdaMeta(cls, field)
        }

    }

}
