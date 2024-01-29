package live.lingting.framework.jackson.sensitive;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import live.lingting.framework.sensitive.Sensitive;

import java.util.List;

/**
 * @author lingting 2024-01-26 18:14
 */
public class SensitiveModule extends SimpleModule {

	public SensitiveModule() {
		init();
	}

	protected void init() {
		setSerializerModifier(new Modifier());
	}

	public static class Modifier extends BeanSerializerModifier {

		@Override
		public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
				List<BeanPropertyWriter> beanProperties) {
			for (BeanPropertyWriter property : beanProperties) {
				Sensitive sensitive = property.getAnnotation(Sensitive.class);
				if (sensitive != null) {
					property.assignSerializer(new SensitiveDefaultSerializer(sensitive));
				}
			}

			return super.changeProperties(config, beanDesc, beanProperties);
		}

	}

}
