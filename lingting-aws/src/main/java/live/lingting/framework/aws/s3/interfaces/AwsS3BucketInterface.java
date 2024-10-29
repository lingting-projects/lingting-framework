package live.lingting.framework.aws.s3.interfaces;

import live.lingting.framework.aws.s3.request.AwsS3SimpleRequest;
import live.lingting.framework.aws.s3.response.AwsS3MultipartItem;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author lingting 2024-09-19 21:57
 */
public interface AwsS3BucketInterface {

	AwsS3ObjectInterface use(String key);

	/**
	 * 列举所有未完成的分片上传
	 * @return k: uploadId, v: k
	 */
	List<AwsS3MultipartItem> multipartList();

	List<AwsS3MultipartItem> multipartList(Consumer<AwsS3SimpleRequest> consumer);

}
