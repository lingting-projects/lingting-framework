package live.lingting.framework.huawei;

import java.util.List;

/**
 * @author lingting 2024-09-13 16:09
 */
public final class HuaweiActions {

	public static final String OBS_OBJECT_GET = "obs:object:GetObject";

	public static final String OBS_OBJECT_PUT = "obs:object:PutObject";

	public static final String OBS_OBJECT_DELETE = "obs:object:DeleteObject";

	public static final String OBS_OBJECT_GET_ACL = "obs:object:GetObjectAcl";

	public static final String OBS_OBJECT_PUT_ACL = "obs:object:PutObjectAcl";

	public static final String OBS_OBJECT_MODIFY_META_DATA = "obs:object:ModifyObjectMetaData";

	public static final String OBS_OBJECT_GET_TAGGING = "obs:object:GetObjectTagging";

	public static final String OBS_OBJECT_PUT_TAGGING = "obs:object:PutObjectTagging";

	public static final String OBS_OBJECT_DELETE_TAGGING = "obs:object:DeleteObjectTagging";

	public static final String OBS_OBJECT_LIST_MULTIPART_UPLOAD_PARTS = "obs:object:ListMultipartUploadParts";

	public static final String OBS_OBJECT_ABORT_MULTIPART_UPLOAD = "obs:object:AbortMultipartUpload";

	public static final String OBS_BUCKET_LIST_ALL_MY = "obs:bucket:ListAllMyBuckets";

	public static final String OBS_BUCKET_CREATE = "obs:bucket:CreateBucket";

	public static final String OBS_BUCKET_LIST = "obs:bucket:ListBucket";

	public static final String OBS_BUCKET_LIST_VERSIONS = "obs:bucket:ListBucketVersions";

	public static final String OBS_BUCKET_HEAD = "obs:bucket:HeadBucket";

	public static final String OBS_BUCKET_GET_LOCATION = "obs:bucket:GetBucketLocation";

	public static final String OBS_BUCKET_DELETE = "obs:bucket:DeleteBucket";

	public static final String OBS_BUCKET_PUT_POLICY = "obs:bucket:PutBucketPolicy";

	public static final String OBS_BUCKET_GET_POLICY = "obs:bucket:GetBucketPolicy";

	public static final String OBS_BUCKET_DELETE_POLICY = "obs:bucket:DeleteBucketPolicy";

	public static final String OBS_BUCKET_PUT_ACL = "obs:bucket:PutBucketAcl";

	public static final String OBS_BUCKET_GET_ACL = "obs:bucket:GetBucketAcl";

	public static final String OBS_BUCKET_PUT_LOGGING = "obs:bucket:PutBucketLogging";

	public static final String OBS_BUCKET_GET_LOGGING = "obs:bucket:GetBucketLogging";

	public static final String OBS_BUCKET_PUT_LIFECYCLE_CONFIGURATION = "obs:bucket:PutLifecycleConfiguration";

	public static final String OBS_BUCKET_GET_LIFECYCLE_CONFIGURATION = "obs:bucket:GetLifecycleConfiguration";

	public static final String OBS_BUCKET_PUT_VERSIONING = "obs:bucket:PutBucketVersioning";

	public static final String OBS_BUCKET_GET_VERSIONING = "obs:bucket:GetBucketVersioning";

	public static final String OBS_BUCKET_PUT_STORAGE_POLICY = "obs:bucket:PutBucketStoragePolicy";

	public static final String OBS_BUCKET_GET_STORAGE_POLICY = "obs:bucket:GetBucketStoragePolicy";

	public static final String OBS_BUCKET_PUT_REPLICATION_CONFIGURATION = "obs:bucket:PutReplicationConfiguration";

	public static final String OBS_BUCKET_GET_REPLICATION_CONFIGURATION = "obs:bucket:GetReplicationConfiguration";

	public static final String OBS_BUCKET_DELETE_REPLICATION_CONFIGURATION = "obs:bucket:DeleteReplicationConfiguration";

	public static final String OBS_BUCKET_PUT_TAGGING = "obs:bucket:PutBucketTagging";

	public static final String OBS_BUCKET_GET_TAGGING = "obs:bucket:GetBucketTagging";

	public static final String OBS_BUCKET_DELETE_TAGGING = "obs:bucket:DeleteBucketTagging";

	public static final String OBS_BUCKET_PUT_QUOTA = "obs:bucket:PutBucketQuota";

	public static final String OBS_BUCKET_GET_QUOTA = "obs:bucket:GetBucketQuota";

	public static final String OBS_BUCKET_GET_STORAGE = "obs:bucket:GetBucketStorage";

	public static final String OBS_BUCKET_PUT_INVENTORY_CONFIGURATION = "obs:bucket:PutBucketInventoryConfiguration";

	public static final String OBS_BUCKET_GET_INVENTORY_CONFIGURATION = "obs:bucket:GetBucketInventoryConfiguration";

	public static final String OBS_BUCKET_DELETE_INVENTORY_CONFIGURATION = "obs:bucket:DeleteBucketInventoryConfiguration";

	public static final String OBS_BUCKET_PUT_CUSTOM_DOMAIN_CONFIGURATION = "obs:bucket:PutBucketCustomDomainConfiguration";

	public static final String OBS_BUCKET_GET_CUSTOM_DOMAIN_CONFIGURATION = "obs:bucket:GetBucketCustomDomainConfiguration";

	public static final String OBS_BUCKET_DELETE_CUSTOM_DOMAIN_CONFIGURATION = "obs:bucket:DeleteBucketCustomDomainConfiguration";

	public static final String OBS_BUCKET_PUT_ENCRYPTION_CONFIGURATION = "obs:bucket:PutEncryptionConfiguration";

	public static final String OBS_BUCKET_GET_ENCRYPTION_CONFIGURATION = "obs:bucket:GetEncryptionConfiguration";

	public static final String OBS_BUCKET_PUT_DIRECT_COLD_ACCESS_CONFIGURATION = "obs:bucket:PutDirectColdAccessConfiguration";

	public static final String OBS_BUCKET_GET_DIRECT_COLD_ACCESS_CONFIGURATION = "obs:bucket:GetDirectColdAccessConfiguration";

	public static final String OBS_BUCKET_DELETE_DIRECT_COLD_ACCESS_CONFIGURATION = "obs:bucket:DeleteDirectColdAccessConfiguration";

	public static final String OBS_BUCKET_PUT_WEBSITE = "obs:bucket:PutBucketWebsite";

	public static final String OBS_BUCKET_GET_WEBSITE = "obs:bucket:GetBucketWebsite";

	public static final String OBS_BUCKET_DELETE_WEBSITE = "obs:bucket:DeleteBucketWebsite";

	public static final String OBS_BUCKET_PUT_C_O_R_S = "obs:bucket:PutBucketCORS";

	public static final String OBS_BUCKET_GET_C_O_R_S = "obs:bucket:GetBucketCORS";

	public static final String OBS_BUCKET_PUT_OBJECT_LOCK_CONFIGURATION = "obs:bucket:PutBucketObjectLockConfiguration";

	public static final String OBS_BUCKET_GET_OBJECT_LOCK_CONFIGURATION = "obs:bucket:GetBucketObjectLockConfiguration";

	public static final String OBS_BUCKET_LIST_MULTIPART_UPLOADS = "obs:bucket:ListBucketMultipartUploads";

	public static final List<String> OBS_BUCKET_DEFAULT = List.of(OBS_OBJECT_GET, OBS_OBJECT_PUT, OBS_OBJECT_DELETE,
		OBS_OBJECT_GET_ACL, OBS_OBJECT_PUT_ACL, OBS_OBJECT_MODIFY_META_DATA, OBS_OBJECT_GET_TAGGING,
		OBS_OBJECT_PUT_TAGGING, OBS_OBJECT_DELETE_TAGGING, OBS_OBJECT_LIST_MULTIPART_UPLOAD_PARTS,
		OBS_OBJECT_ABORT_MULTIPART_UPLOAD,

		OBS_BUCKET_LIST, OBS_BUCKET_PUT_ACL, OBS_BUCKET_GET_TAGGING, OBS_BUCKET_PUT_TAGGING,
		OBS_BUCKET_DELETE_TAGGING, OBS_BUCKET_LIST_MULTIPART_UPLOADS);

	public static final List<String> OBS_OBJECT_DEFAULT = OBS_BUCKET_DEFAULT;

	public static final List<String> OBS_OBJECT_DEFAULT_PUT = List.of(OBS_OBJECT_PUT, OBS_OBJECT_PUT_ACL,
		OBS_OBJECT_MODIFY_META_DATA, OBS_OBJECT_PUT_TAGGING, OBS_OBJECT_ABORT_MULTIPART_UPLOAD);

	private HuaweiActions() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}
}
