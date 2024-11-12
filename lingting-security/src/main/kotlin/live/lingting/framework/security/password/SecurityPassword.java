package live.lingting.framework.security.password;

import live.lingting.framework.util.StringUtils;

/**
 * @author lingting 2023-03-30 15:10
 */
public interface SecurityPassword {

	default boolean valid(String plaintext) {
		return StringUtils.hasText(plaintext);
	}

	/**
	 * 依据前端加密方式, 明文转密文
	 */
	String encodeFront(String plaintext);

	/**
	 * 解析收到的前端密文
	 */
	String decodeFront(String ciphertext);

	/**
	 * 密码明文转数据库存储的密文
	 */
	String encode(String plaintext);

	/**
	 * 明文和密文是否匹配
	 */
	boolean match(String plaintext, String ciphertext);

}
