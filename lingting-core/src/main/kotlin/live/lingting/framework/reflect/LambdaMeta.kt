package live.lingting.framework.reflect

import java.lang.invoke.MethodHandleProxies
import java.lang.invoke.MethodHandles
import java.lang.invoke.SerializedLambda
import java.lang.reflect.Executable
import java.lang.reflect.Proxy
import java.util.function.Supplier
import kotlin.reflect.KClass
import live.lingting.framework.util.BooleanUtils.ifTrue
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

        @JvmStatic
        fun of(a: Any): LambdaMeta {
            if (a is Proxy) {
                return of(a)
            }

            val cls = a.javaClass
            val loader = cls.classLoader
            val methods = ClassUtils.methods(cls)
            val writeReplace = methods.firstOrNull {
                it.name == "writeReplace"
            }?.apply { isAccessible = true }

            if (writeReplace != null) {
                val lambda = writeReplace.invoke(a) as SerializedLambda
                return of(lambda, loader)
            }

            val getFunctionDelegate = methods.firstOrNull {
                it.name == "getFunctionDelegate"
            }!!.apply { isAccessible = true }

            val captured = getFunctionDelegate.invoke(a)
            return ofCaptured(captured)
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
            val field = ClassUtils.FIELD_METHOD_START.any {
                method.startsWith(it)
            }.ifTrue(Supplier {
                ClassUtils.toFiledName(method)
            })

            if (field != null) {
                return LambdaMeta(cls, field)
            }

            val captured = lambda.getCapturedArg(0)
            return ofCaptured(captured)
        }

        @JvmStatic
        fun ofCaptured(a: Any): LambdaMeta {
            val fields = ClassUtils.classFields(a.javaClass)
            val owner = fields.first { it.name == "owner" }.visibleGet().get(a).let {
                if (it is KClass<*>) {
                    it.java
                } else {
                    it as Class<*>
                }
            }
            val name = fields.first { it.name == "name" }.visibleGet().get(a) as String
            return LambdaMeta(owner, name)
        }

    }

}
