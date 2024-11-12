package live.lingting.framework.huawei

/**
 * @author lingting 2024-09-13 16:09
 */
class HuaweiActions private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        const val OBS_OBJECT_GET: String = "obs:object:GetObject"

        const val OBS_OBJECT_PUT: String = "obs:object:PutObject"

        const val OBS_OBJECT_DELETE: String = "obs:object:DeleteObject"

        const val OBS_OBJECT_GET_ACL: String = "obs:object:GetObjectAcl"

        const val OBS_OBJECT_PUT_ACL: String = "obs:object:PutObjectAcl"

        const val OBS_OBJECT_MODIFY_META_DATA: String = "obs:object:ModifyObjectMetaData"

        const val OBS_OBJECT_GET_TAGGING: String = "obs:object:GetObjectTagging"

        const val OBS_OBJECT_PUT_TAGGING: String = "obs:object:PutObjectTagging"

        const val OBS_OBJECT_DELETE_TAGGING: String = "obs:object:DeleteObjectTagging"

        const val OBS_OBJECT_LIST_MULTIPART_UPLOAD_PARTS: String = "obs:object:ListMultipartUploadParts"

        const val OBS_OBJECT_ABORT_MULTIPART_UPLOAD: String = "obs:object:AbortMultipartUpload"

        const val OBS_BUCKET_LIST_ALL_MY: String = "obs:bucket:ListAllMyBuckets"

        const val OBS_BUCKET_CREATE: String = "obs:bucket:CreateBucket"

        const val OBS_BUCKET_LIST: String = "obs:bucket:ListBucket"

        const val OBS_BUCKET_LIST_VERSIONS: String = "obs:bucket:ListBucketVersions"

        const val OBS_BUCKET_HEAD: String = "obs:bucket:HeadBucket"

        const val OBS_BUCKET_GET_LOCATION: String = "obs:bucket:GetBucketLocation"

        const val OBS_BUCKET_DELETE: String = "obs:bucket:DeleteBucket"

        const val OBS_BUCKET_PUT_POLICY: String = "obs:bucket:PutBucketPolicy"

        const val OBS_BUCKET_GET_POLICY: String = "obs:bucket:GetBucketPolicy"

        const val OBS_BUCKET_DELETE_POLICY: String = "obs:bucket:DeleteBucketPolicy"

        const val OBS_BUCKET_PUT_ACL: String = "obs:bucket:PutBucketAcl"

        const val OBS_BUCKET_GET_ACL: String = "obs:bucket:GetBucketAcl"

        const val OBS_BUCKET_PUT_LOGGING: String = "obs:bucket:PutBucketLogging"

        const val OBS_BUCKET_GET_LOGGING: String = "obs:bucket:GetBucketLogging"

        const val OBS_BUCKET_PUT_LIFECYCLE_CONFIGURATION: String = "obs:bucket:PutLifecycleConfiguration"

        const val OBS_BUCKET_GET_LIFECYCLE_CONFIGURATION: String = "obs:bucket:GetLifecycleConfiguration"

        const val OBS_BUCKET_PUT_VERSIONING: String = "obs:bucket:PutBucketVersioning"

        const val OBS_BUCKET_GET_VERSIONING: String = "obs:bucket:GetBucketVersioning"

        const val OBS_BUCKET_PUT_STORAGE_POLICY: String = "obs:bucket:PutBucketStoragePolicy"

        const val OBS_BUCKET_GET_STORAGE_POLICY: String = "obs:bucket:GetBucketStoragePolicy"

        const val OBS_BUCKET_PUT_REPLICATION_CONFIGURATION: String = "obs:bucket:PutReplicationConfiguration"

        const val OBS_BUCKET_GET_REPLICATION_CONFIGURATION: String = "obs:bucket:GetReplicationConfiguration"

        const val OBS_BUCKET_DELETE_REPLICATION_CONFIGURATION: String = "obs:bucket:DeleteReplicationConfiguration"

        const val OBS_BUCKET_PUT_TAGGING: String = "obs:bucket:PutBucketTagging"

        const val OBS_BUCKET_GET_TAGGING: String = "obs:bucket:GetBucketTagging"

        const val OBS_BUCKET_DELETE_TAGGING: String = "obs:bucket:DeleteBucketTagging"

        const val OBS_BUCKET_PUT_QUOTA: String = "obs:bucket:PutBucketQuota"

        const val OBS_BUCKET_GET_QUOTA: String = "obs:bucket:GetBucketQuota"

        const val OBS_BUCKET_GET_STORAGE: String = "obs:bucket:GetBucketStorage"

        const val OBS_BUCKET_PUT_INVENTORY_CONFIGURATION: String = "obs:bucket:PutBucketInventoryConfiguration"

        const val OBS_BUCKET_GET_INVENTORY_CONFIGURATION: String = "obs:bucket:GetBucketInventoryConfiguration"

        const val OBS_BUCKET_DELETE_INVENTORY_CONFIGURATION: String = "obs:bucket:DeleteBucketInventoryConfiguration"

        const val OBS_BUCKET_PUT_CUSTOM_DOMAIN_CONFIGURATION: String = "obs:bucket:PutBucketCustomDomainConfiguration"

        const val OBS_BUCKET_GET_CUSTOM_DOMAIN_CONFIGURATION: String = "obs:bucket:GetBucketCustomDomainConfiguration"

        const val OBS_BUCKET_DELETE_CUSTOM_DOMAIN_CONFIGURATION: String = "obs:bucket:DeleteBucketCustomDomainConfiguration"

        const val OBS_BUCKET_PUT_ENCRYPTION_CONFIGURATION: String = "obs:bucket:PutEncryptionConfiguration"

        const val OBS_BUCKET_GET_ENCRYPTION_CONFIGURATION: String = "obs:bucket:GetEncryptionConfiguration"

        const val OBS_BUCKET_PUT_DIRECT_COLD_ACCESS_CONFIGURATION: String = "obs:bucket:PutDirectColdAccessConfiguration"

        const val OBS_BUCKET_GET_DIRECT_COLD_ACCESS_CONFIGURATION: String = "obs:bucket:GetDirectColdAccessConfiguration"

        const val OBS_BUCKET_DELETE_DIRECT_COLD_ACCESS_CONFIGURATION: String = "obs:bucket:DeleteDirectColdAccessConfiguration"

        const val OBS_BUCKET_PUT_WEBSITE: String = "obs:bucket:PutBucketWebsite"

        const val OBS_BUCKET_GET_WEBSITE: String = "obs:bucket:GetBucketWebsite"

        const val OBS_BUCKET_DELETE_WEBSITE: String = "obs:bucket:DeleteBucketWebsite"

        const val OBS_BUCKET_PUT_C_O_R_S: String = "obs:bucket:PutBucketCORS"

        const val OBS_BUCKET_GET_C_O_R_S: String = "obs:bucket:GetBucketCORS"

        const val OBS_BUCKET_PUT_OBJECT_LOCK_CONFIGURATION: String = "obs:bucket:PutBucketObjectLockConfiguration"

        const val OBS_BUCKET_GET_OBJECT_LOCK_CONFIGURATION: String = "obs:bucket:GetBucketObjectLockConfiguration"

        const val OBS_BUCKET_LIST_MULTIPART_UPLOADS: String = "obs:bucket:ListBucketMultipartUploads"

        val OBS_BUCKET_DEFAULT: List<String?> = java.util.List.of(
            OBS_OBJECT_GET, OBS_OBJECT_PUT, OBS_OBJECT_DELETE,
            OBS_OBJECT_GET_ACL, OBS_OBJECT_PUT_ACL, OBS_OBJECT_MODIFY_META_DATA, OBS_OBJECT_GET_TAGGING,
            OBS_OBJECT_PUT_TAGGING, OBS_OBJECT_DELETE_TAGGING, OBS_OBJECT_LIST_MULTIPART_UPLOAD_PARTS,
            OBS_OBJECT_ABORT_MULTIPART_UPLOAD,

            OBS_BUCKET_LIST, OBS_BUCKET_PUT_ACL, OBS_BUCKET_GET_TAGGING, OBS_BUCKET_PUT_TAGGING,
            OBS_BUCKET_DELETE_TAGGING, OBS_BUCKET_LIST_MULTIPART_UPLOADS
        )

        val OBS_OBJECT_DEFAULT: List<String?> = OBS_BUCKET_DEFAULT

        val OBS_OBJECT_DEFAULT_PUT: List<String> = java.util.List.of(
            OBS_OBJECT_PUT, OBS_OBJECT_PUT_ACL,
            OBS_OBJECT_MODIFY_META_DATA, OBS_OBJECT_PUT_TAGGING, OBS_OBJECT_ABORT_MULTIPART_UPLOAD
        )
    }
}
