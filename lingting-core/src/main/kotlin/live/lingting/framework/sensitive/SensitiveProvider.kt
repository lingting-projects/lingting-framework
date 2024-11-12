package live.lingting.framework.sensitive;

import live.lingting.framework.Sequence;

/**
 * @author lingting 2024-01-26 18:16
 */
public interface SensitiveProvider extends Sequence {

	SensitiveSerializer find(Sensitive sensitive);

	/**
	 * 升序排序用
	 */
	@Override
	default int getSequence() {
		return 0;
	}

}
