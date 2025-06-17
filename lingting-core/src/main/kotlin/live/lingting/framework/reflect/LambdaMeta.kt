package live.lingting.framework.reflect

import java.lang.invoke.SerializedLambda
import java.lang.reflect.Proxy
import live.lingting.framework.reflect.lambda.KotlinLambdaMeta
import live.lingting.framework.reflect.lambda.ProxyLambdaMeta
import live.lingting.framework.reflect.lambda.SerializedCapturedLambdaMeta
import live.lingting.framework.reflect.lambda.SerializedLambdaMeta
import live.lingting.framework.util.ClassUtils

/**
 * 从匿名函数解析对应调用的字段
 * @author lingting 2024/11/27 20:38
 */
abstract class LambdaMeta() {

    companion object {

        @JvmStatic
        fun of(source: Any): LambdaMeta {
            if (source is Proxy) {
                return ProxyLambdaMeta(source)
            }

            val cls = source.javaClass

            if (ClassUtils.isSuper(cls, "kotlin.jvm.internal.FunctionAdapter")) {
                return KotlinLambdaMeta(source)
            }

            val methods = ClassUtils.methods(source.javaClass)
            val lambda = methods.first {
                it.name == "writeReplace"
            }.apply { isAccessible = true }
                .invoke(source) as SerializedLambda
            if (lambda.capturedArgCount > 0) {
                return SerializedCapturedLambdaMeta(source, lambda)
            }

            return SerializedLambdaMeta(source, lambda)
        }

    }

    abstract val wrapperCls: Class<*>;

    abstract val cls: Class<*>

    abstract val field: String


}
