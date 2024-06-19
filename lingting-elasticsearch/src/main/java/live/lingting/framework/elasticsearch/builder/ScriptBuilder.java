package live.lingting.framework.elasticsearch.builder;

import co.elastic.clients.elasticsearch._types.InlineScript;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.json.JsonData;
import live.lingting.framework.elasticsearch.ElasticsearchFunction;
import live.lingting.framework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static live.lingting.framework.elasticsearch.ElasticsearchUtils.fieldName;

/**
 * @author lingting 2024-03-06 19:27
 */
public class ScriptBuilder<T> {

	public static final String PREFIX_SOURCE = "ctx._source";

	public static final String PREFIX_PARAMS = "params";

	private final StringBuilder sourceBuilder = new StringBuilder();

	private final Map<String, JsonData> params = new HashMap<>();

	private String lang = "painless";

	// region params
	public <R> ScriptBuilder<T> put(ElasticsearchFunction<T, R> func, R value) {
		String field = fieldName(func);
		return put(field, value);
	}

	public <R> ScriptBuilder<T> put(String name, R value) {
		JsonData data = JsonData.of(value);
		params.put(name, data);
		return this;
	}

	// endregion

	// region script basic
	public static <T> ScriptBuilder<T> builder() {
		return new ScriptBuilder<>();
	}

	public static String fillEnd(String source) {
		if (!source.endsWith(";")) {
			return "%s;".formatted(source);
		}
		return source;
	}

	public static String genSourceField(String field) {
		return "%s.%s".formatted(PREFIX_SOURCE, field);
	}

	public static String genParamsField(String field) {
		return "%s.%s".formatted(PREFIX_PARAMS, field);
	}

	public static String genSymbol(String field, String symbol) {
		return genSymbol(field, symbol, genParamsField(field));
	}

	public static String genSymbol(String field, String symbol, String value) {
		return "%s %s %s".formatted(genSourceField(field), symbol, value);
	}

	public static String genIf(String condition, String script) {
		return "if(%s){%s}".formatted(condition, fillEnd(script));
	}

	public static String genSetNull(String field) {
		return genSymbol(field, "=", "null");
	}

	public static String genSetParams(String field) {
		return genSymbol(field, "=", genParamsField(field));
	}

	public static String genSetIfAbsent(String field) {
		String sourceField = genSourceField(field);
		String condition = "%s==null || %s==''".formatted(sourceField, sourceField);
		String script = genSetParams(field);
		return genIf(condition, script);
	}

	public ScriptBuilder<T> append(String script) {
		if (!StringUtils.hasText(script)) {
			return this;
		}

		if (!sourceBuilder.isEmpty()) {
			sourceBuilder.append("\n");
		}
		sourceBuilder.append(fillEnd(script));
		return this;
	}

	public ScriptBuilder<T> append(String script, String field, Object value) {
		if (value != null) {
			params.put(field, JsonData.of(value));
		}
		return append(script);
	}

	public <R> ScriptBuilder<T> set(ElasticsearchFunction<T, R> func, R value) {
		String field = fieldName(func);
		return set(field, value);
	}

	public ScriptBuilder<T> set(String field, Object value) {
		String script = value == null ? genSetNull(field) : genSetParams(field);
		return append(script, field, value);
	}

	public ScriptBuilder<T> setIfAbsent(String field, Object value) {
		if (value != null) {
			String script = genSetIfAbsent(field);
			return append(script, field, value);
		}
		return this;
	}

	public ScriptBuilder<T> lang(String lang) {
		this.lang = lang;
		return this;
	}

	public ScriptBuilder<T> painless() {
		return lang("painless");
	}

	// endregion

	// region script number

	public ScriptBuilder<T> increasing(String field) {
		return increasing(field, 1);
	}

	public ScriptBuilder<T> increasing(String field, Number value) {
		return append(genSymbol(field, "+="), field, value);
	}

	public ScriptBuilder<T> decrease(String field) {
		return decrease(field, 1);
	}

	public ScriptBuilder<T> decrease(String field, Number value) {
		return append(genSymbol(field, "-="), field, value);
	}

	// endregion

	// region build

	public InlineScript buildInline() {
		return InlineScript.of(i -> i.source(sourceBuilder.toString()).lang(lang).params(new HashMap<>(params)));
	}

	public Script build() {
		InlineScript inline = buildInline();
		return Script.of(s -> s.inline(inline));
	}
	// endregion

}
