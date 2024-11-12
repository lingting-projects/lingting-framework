package live.lingting.framework.mybatis.extend;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Supplier;

public class ExtendMetaObjectHandle implements MetaObjectHandler {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(ExtendMetaObjectHandle.class);

	@Override
	public void insertFill(MetaObject metaObject) {
		// 逻辑删除标识
		strictInsertFill(metaObject, "deleted", Long.class, 0L);
		// 创建时间
		strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());

		// 新增时, 将创建数据同步到修改数据
		updateFill(metaObject);
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		// 修改时间
		strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
	}

	@Override
	public MetaObjectHandler strictFillStrategy(MetaObject metaObject, String fieldName, Supplier<?> fieldVal) {
		// 重修填充策略, 即便原字段有值 依旧进行填充.
		Object obj = fieldVal.get();
		// 如果新的值为null, 则不进行填充
		if (Objects.nonNull(obj)) {
			metaObject.setValue(fieldName, obj);
		}
		return this;
	}

}
