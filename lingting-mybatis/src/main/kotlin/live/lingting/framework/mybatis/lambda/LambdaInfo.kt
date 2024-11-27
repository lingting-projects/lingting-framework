package live.lingting.framework.mybatis.lambda

import com.baomidou.mybatisplus.core.toolkit.support.IdeaProxyLambdaMeta
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta
import java.lang.invoke.SerializedLambda
import java.lang.reflect.Proxy
import live.lingting.framework.util.ClassUtils
import org.apache.ibatis.reflection.property.PropertyNamer

/**
 * @author lingting 2024/11/27 20:06
 */
class LambdaInfo(
    val cls: Class<*>,
    val field: String,
) {

    companion object {

        @JvmStatic
        fun of(proxy: Proxy): LambdaInfo {
            val meta = IdeaProxyLambdaMeta(proxy)
            return of(meta)
        }

        @JvmStatic
        fun of(meta: LambdaMeta): LambdaInfo {
            val method = meta.implMethodName
            val field: String = PropertyNamer.methodToProperty(method)
            return LambdaInfo(meta.instantiatedClass, field)
        }

        @JvmStatic
        fun of(lambda: SerializedLambda, loader: ClassLoader): LambdaInfo {
            val cls = lambda.instantiatedMethodType.let {
                it.substring(2, it.indexOf(";")).replace("/", ".")
            }.let {
                ClassUtils.loadClass(it, loader)
            }

            val field = lambda.getCapturedArg(0).toString().let {
                it.substring(
                    it.indexOf(cls.simpleName) + cls.simpleName.length + 1,
                    it.indexOf(":")
                )
            }
            return LambdaInfo(cls, field)
        }

    }

}
