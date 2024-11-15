package live.lingting.framework.mybatis.extend

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler
import org.apache.ibatis.reflection.MetaObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

import java.util.function.Supplier

class ExtendMetaObjectHandle : MetaObjectHandler {
    override fun insertFill(metaObject: MetaObject) {
        // 逻辑删除标识
        strictInsertFill(metaObject, "deleted", Long::class.java, 0L)
        // 创建时间
        strictInsertFill(metaObject, "createTime", LocalDateTime::class.java, LocalDateTime.now())

        // 新增时, 将创建数据同步到修改数据
        updateFill(metaObject)
    }

    override fun updateFill(metaObject: MetaObject) {
        // 修改时间
        strictUpdateFill(metaObject, "updateTime", LocalDateTime::class.java, LocalDateTime.now())
    }

    override fun strictFillStrategy(metaObject: MetaObject, fieldName: String, fieldVal: Supplier<*>): MetaObjectHandler {
        // 重修填充策略, 即便原字段有值 依旧进行填充.
        val obj = fieldVal.get()
        // 如果新的值为null, 则不进行填充
        if (Objects.nonNull(obj)) {
            metaObject.setValue(fieldName, obj)
        }
        return this
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ExtendMetaObjectHandle::class.java)
    }
}
