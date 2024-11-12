package live.lingting.framework.aws.s3.interfaces;

import live.lingting.framework.aws.AwsS3Bucket;
import live.lingting.framework.aws.s3.request.AwsS3SimpleRequest;
import live.lingting.framework.aws.s3.response.AwsS3MultipartItem;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author lingting 2024-09-19 21:55
 */
public interface AwsS3BucketDelegation extends AwsS3BucketInterface, AwsS3Delegation<AwsS3Bucket> {

	@Override
	default List<AwsS3MultipartItem> multipartList() {
		return delegation().multipartList();
	}

	@Override
	default List<AwsS3MultipartItem> multipartList(Consumer<AwsS3SimpleRequest> consumer) {
		return delegation().multipartList(consumer);
	}

}
