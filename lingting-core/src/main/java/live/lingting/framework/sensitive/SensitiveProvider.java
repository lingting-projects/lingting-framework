package live.lingting.framework.sensitive;

/**
 * @author lingting 2024-01-26 18:16
 */
public interface SensitiveProvider {

	SensitiveSerializer find(Sensitive sensitive);

	/**
	 * 排序, 值越小 优先级越高
	 */
	default int order() {
		return Integer.MAX_VALUE;
	}

}
