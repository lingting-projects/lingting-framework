package live.lingting.framework.reflect.lambda

import live.lingting.framework.util.ClassUtils

/**
 * @author lingting 2025/1/9 10:54
 */
class KotlinLambdaMeta(
    source: Any
) : CapturedLambdaMeta(source, { getDelegate(source) }) {

    companion object {

        fun getDelegate(source: Any): Any {
            val methods = ClassUtils.methods(source.javaClass)
            return methods.first {
                it.name == "getFunctionDelegate"
            }.apply { isAccessible = true }
        }

    }


}
