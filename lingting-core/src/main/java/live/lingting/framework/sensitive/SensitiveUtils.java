package live.lingting.framework.sensitive;

import live.lingting.framework.Sequence;
import live.lingting.framework.sensitive.serializer.SensitiveDefaultProvider;
import live.lingting.framework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * @author lingting 2023-04-27 15:42
 */
public final class SensitiveUtils {

	public static final String MIDDLE = "******";

	private SensitiveUtils() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}

	/**
	 * 脱敏字符串序列化
	 *
	 * @param raw          原始字符串
	 * @param prefixLength 结果前缀长度
	 * @param suffixLength 结果后缀长度
	 */
	public static String serialize(String raw, String middle, int prefixLength, int suffixLength) {
		if (!StringUtils.hasText(raw)) {
			return "";
		}

		// 如果关闭脱敏
		if (!SensitiveHolder.allowSensitive()) {
			return raw;
		}

		StringBuilder builder = new StringBuilder();

		// 开头
		builder.append(raw, 0, prefixLength);

		// 中间
		if (raw.length() > prefixLength) {
			builder.append(middle);
		}

		// 有没有结尾
		if (raw.length() > prefixLength + suffixLength) {
			builder.append(raw, raw.length() - suffixLength, raw.length());
		}

		return builder.toString();
	}

	public static String serialize(String raw, int prefixLength, int suffixLength) {
		return serialize(raw, MIDDLE, prefixLength, suffixLength);
	}

	public static String serialize(String raw, int prefixLength, int suffixLength, Sensitive sensitive) {
		if (sensitive != null) {
			if (sensitive.prefixLength() > -1) {
				prefixLength = sensitive.prefixLength();
			}
			if (sensitive.suffixLength() > -1) {
				suffixLength = sensitive.suffixLength();
			}

			if (StringUtils.hasText(sensitive.middle())) {
				return serialize(raw, sensitive.middle(), prefixLength, suffixLength);
			}
		}
		return serialize(raw, prefixLength, suffixLength);
	}

	public static SensitiveSerializer findSerializer(Sensitive sensitive) {
		List<SensitiveProvider> providers = providers();
		for (SensitiveProvider provider : providers) {
			SensitiveSerializer serializer = provider.find(sensitive);
			if (serializer != null) {
				return serializer;
			}
		}
		return null;
	}

	public static List<SensitiveProvider> providers() {
		List<SensitiveProvider> providers = new ArrayList<>();
		providers.add(SensitiveDefaultProvider.INSTANCE);

		try {
			ServiceLoader<SensitiveProvider> loader = ServiceLoader.load(SensitiveProvider.class);

			loader.stream().filter(Objects::nonNull).forEach(provider -> {
				try {
					SensitiveProvider p = provider.get();
					if (p != null) {
						providers.add(p);
					}
				}
				catch (ServiceConfigurationError error) {
					//
				}
			});
		}
		catch (ServiceConfigurationError e) {
			//
		}

		Sequence.asc(providers);
		return providers;
	}

}
