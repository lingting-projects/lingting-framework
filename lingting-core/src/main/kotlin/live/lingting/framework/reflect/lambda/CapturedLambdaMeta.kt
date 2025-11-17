package live.lingting.framework.reflect.lambda

import live.lingting.framework.reflect.LambdaMeta
import live.lingting.framework.util.ClassUtils
import java.util.function.Supplier

/**
 * @author lingting 2025/1/9 10:59
 */
open class CapturedLambdaMeta(open val source: Any, getDelegate: Supplier<Any>) : LambdaMeta() {

    val delegate: Any by lazy { getDelegate.get() }

    override val wrapperCls: Class<*> = source.javaClass

    override val cls: Class<*> by lazy {
        val fields = ClassUtils.classFields(delegate.javaClass)
        fields.first { it.name == "owner" }.visibleGet().get(delegate).let {
            if (it is kotlin.reflect.KClass<*>) {
                it.java
            } else {
                it as Class<*>
            }
        }
    }

    override val field: String by lazy {
        val fields = ClassUtils.classFields(delegate.javaClass)
        fields.first { it.name == "name" }.visibleGet().get(delegate) as String
    }


}
