package live.lingting.framework.aws.policy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author lingting 2024-09-12 20:55
 */
@Getter
@RequiredArgsConstructor
public enum Acl {

	PRIVATE("private"),

	PUBLIC_READ("public-read"),

	PUBLIC_READ_WRITE("public-read-write"),

	;

	private final String value;

}
