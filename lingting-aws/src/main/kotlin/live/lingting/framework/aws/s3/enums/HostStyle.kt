package live.lingting.framework.aws.s3.enums

/**
 * @author lingting 2025/1/15 19:54
 */
enum class HostStyle {

    /**
     * https://s3.region.amazonaws.com/s3-bucket
     */
    SECOND,

    /**
     * https://s3-bucket.s3.region.amazonaws.com
     */
    VIRTUAL,

    ;

}
