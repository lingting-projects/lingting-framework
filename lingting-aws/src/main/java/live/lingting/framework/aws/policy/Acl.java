package live.lingting.framework.aws.policy;

/**
 * @author lingting 2024-09-12 20:55
 */
public enum Acl {

	PRIVATE("private"),

	PUBLIC_READ("public-read"),

	PUBLIC_READ_WRITE("public-read-write"),

	;

	private final String value;

	private Acl(String value) {
		this.value = value;
	}

	public String getValue() {return this.value;}
}
