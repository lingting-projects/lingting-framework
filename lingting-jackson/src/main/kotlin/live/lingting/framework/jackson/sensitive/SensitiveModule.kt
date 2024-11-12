package live.lingting.framework.jackson.sensitive

import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier
import live.lingting.framework.sensitive.Sensitive

/**
 * @author lingting 2024-01-26 18:14
 */
class SensitiveModule : SimpleModule() {
    init {
        init()
    }

    protected fun init() {
        setSerializerModifier(Modifier())
    }

    class Modifier : BeanSerializerModifier() {
        override fun changeProperties(
            config: SerializationConfig, beanDesc: BeanDescription,
            beanProperties: List<BeanPropertyWriter>
        ): List<BeanPropertyWriter> {
            for (property in beanProperties) {
                val sensitive = property.getAnnotation(Sensitive::class.java)
                if (sensitive != null) {
                    property.assignSerializer(SensitiveDefaultSerializer(sensitive))
                }
            }

            return super.changeProperties(config, beanDesc, beanProperties)
        }
    }
}
