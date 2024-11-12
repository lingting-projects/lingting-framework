package live.lingting.framework.aws.policy

/**
 * @author lingting 2024-09-12 20:55
 */
enum class Acl(val value: String) {
    PRIVATE("private"),

    PUBLIC_READ("public-read"),

    PUBLIC_READ_WRITE("public-read-write"),
}
