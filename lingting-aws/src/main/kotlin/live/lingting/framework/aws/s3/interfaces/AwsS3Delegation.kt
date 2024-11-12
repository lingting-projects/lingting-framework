package live.lingting.framework.aws.s3.interfaces;

import live.lingting.framework.aws.AwsS3Client;

/**
 * @author lingting 2024-09-19 22:06
 */
public interface AwsS3Delegation<C extends AwsS3Client> {

	C delegation();

}
