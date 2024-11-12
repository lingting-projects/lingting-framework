package live.lingting.framework.sensitive;

import live.lingting.framework.sensitive.serializer.SensitiveDefaultSerializer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lingting 2023-04-27 15:15
 */
@Inherited
@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Sensitive {

	Class<? extends SensitiveSerializer> value() default SensitiveDefaultSerializer.class;

	String middle() default SensitiveUtils.MIDDLE;

	int prefixLength() default -1;

	int suffixLength() default -1;

}
