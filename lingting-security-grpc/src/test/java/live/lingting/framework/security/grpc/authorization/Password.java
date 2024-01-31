package live.lingting.framework.security.grpc.authorization;

import live.lingting.framework.security.password.SecurityPassword;

/**
 * @author lingting 2024-01-30 20:35
 */
public class Password implements SecurityPassword {

	@Override
	public String encodeFront(String plaintext) {
		return plaintext;
	}

	@Override
	public String decodeFront(String ciphertext) {
		return ciphertext;
	}

	@Override
	public String encode(String plaintext) {
		return plaintext;
	}

	@Override
	public boolean match(String plaintext, String ciphertext) {
		return true;
	}

}
