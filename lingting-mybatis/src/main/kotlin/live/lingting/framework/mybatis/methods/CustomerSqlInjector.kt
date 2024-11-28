package live.lingting.framework.mybatis.methods

import com.baomidou.mybatisplus.core.injector.AbstractMethod
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector
import com.baomidou.mybatisplus.core.metadata.TableInfo
import org.apache.ibatis.session.Configuration

/**
 * @author lingting 2024/11/25 15:02
 */
class CustomerSqlInjector(
    val methods: List<AbstractMybatisMethod>,
) : DefaultSqlInjector() {

    override fun getMethodList(configuration: Configuration, mapperClass: Class<*>, tableInfo: TableInfo): List<AbstractMethod> {
        val list = super.getMethodList(configuration, mapperClass, tableInfo)
        list.addAll(methods)
        return list
    }

}
