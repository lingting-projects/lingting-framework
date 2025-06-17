package live.lingting.framework.reflect.lambda

import java.lang.invoke.SerializedLambda
import live.lingting.framework.reflect.LambdaMeta
import live.lingting.framework.util.ClassUtils

/**
 * @author lingting 2025/1/9 11:27
 */
class SerializedLambdaMeta(
    val source: Any,
    val lambda: SerializedLambda
) : LambdaMeta() {

    override val wrapperCls: Class<*> = source.javaClass

    override val cls: Class<*> by lazy {
        lambda.instantiatedMethodType.let {
            it.substring(2, it.indexOf(";")).replace("/", ".")
        }.let {
            ClassUtils.loadClass(it, source.javaClass.classLoader)
        }
    }

    override val field: String by lazy {
        val method = lambda.implMethodName
        ClassUtils.toFiledName(method)
    }

}
