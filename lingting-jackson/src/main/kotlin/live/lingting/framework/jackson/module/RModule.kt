package live.lingting.framework.jackson.module;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import live.lingting.framework.api.R;

import java.io.IOException;

/**
 * @author lingting 2023-09-27 11:25
 */
public class RModule extends SimpleModule {

	public static final String FIELD_CODE = "code";

	public static final String FIELD_DATA = "data";

	public static final String FIELD_MESSAGE = "message";

	public RModule() {
		init();
	}

	protected void init() {
		setDeserializers(new RJacksonDeserializers());
	}

	public static class RJacksonDeserializers extends SimpleDeserializers {

		@Override
		public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config,
														BeanDescription beanDesc) throws JsonMappingException {
			Class<?> rawClass = type.getRawClass();
			if (R.class.isAssignableFrom(rawClass)) {
				return new RDeserializer(beanDesc);
			}
			return super.findBeanDeserializer(type, config, beanDesc);
		}

	}

	public static class RDeserializer extends JsonDeserializer<R<?>> {

		private final BeanDescription beanDesc;

		public RDeserializer(BeanDescription beanDesc) {
			this.beanDesc = beanDesc;
		}

		@Override
		public R<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
			TreeNode root = p.getCodec().readTree(p);
			int code = getCode(root);
			String message = getMessage(root);
			Object data = getData(root, getDefinition(FIELD_DATA), ctxt);
			return R.of(code, data, message);
		}

		int getCode(TreeNode root) {
			TreeNode node = root.get(FIELD_CODE);
			if (isNull(node) || !node.isValueNode() || !(node instanceof IntNode)) {
				return -1;
			}
			return ((IntNode) node).asInt();
		}

		String getMessage(TreeNode root) {
			TreeNode node = root.get(FIELD_MESSAGE);
			if (isNull(node) || !node.isValueNode() || !(node instanceof TextNode)) {
				return null;
			}
			return ((TextNode) node).asText();
		}

		Object getData(TreeNode root, BeanPropertyDefinition definition, DeserializationContext ctxt)
			throws IOException {
			TreeNode node = root.get(FIELD_DATA);
			if (isNull(node)) {
				return null;
			}
			JavaType javaType = definition.getPrimaryType();

			JsonDeserializer<Object> deserializer = ctxt.findRootValueDeserializer(javaType);

			try (JsonParser parser = node.traverse()) {
				parser.nextToken();
				return deserializer.deserialize(parser, ctxt);
			}
		}

		BeanPropertyDefinition getDefinition(String field) {
			return beanDesc.findProperties()
				.stream()
				.filter(definition -> definition.getName().equals(field))
				.findFirst()
				.orElse(null);
		}

		boolean isNull(TreeNode node) {
			return node == null || node instanceof NullNode;
		}

	}

}
