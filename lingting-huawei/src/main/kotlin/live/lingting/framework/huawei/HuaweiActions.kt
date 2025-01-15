package live.lingting.framework.huawei

/**
 * @author lingting 2024-09-13 16:09
 */
object HuaweiActions {
    const val OBS_OBJECT_GET = "obs:object:GetObject"

    const val OBS_OBJECT_PUT = "obs:object:PutObject"

    const val OBS_OBJECT_DELETE = "obs:object:DeleteObject"

    const val OBS_OBJECT_GET_ACL = "obs:object:GetObjectAcl"

    const val OBS_OBJECT_PUT_ACL = "obs:object:PutObjectAcl"

    const val OBS_OBJECT_MODIFY_META_DATA = "obs:object:ModifyObjectMetaData"

    const val OBS_OBJECT_GET_TAGGING = "obs:object:GetObjectTagging"

    const val OBS_OBJECT_PUT_TAGGING = "obs:object:PutObjectTagging"

    const val OBS_OBJECT_DELETE_TAGGING = "obs:object:DeleteObjectTagging"

    const val OBS_OBJECT_LIST_MULTIPART_UPLOAD_PARTS = "obs:object:ListMultipartUploadParts"

    const val OBS_OBJECT_ABORT_MULTIPART_UPLOAD = "obs:object:AbortMultipartUpload"

    const val OBS_BUCKET_LIST_ALL_MY = "obs:bucket:ListAllMyBuckets"

    const val OBS_BUCKET_CREATE = "obs:bucket:CreateBucket"

    const val OBS_BUCKET_LIST = "obs:bucket:ListBucket"

    const val OBS_BUCKET_LIST_VERSIONS = "obs:bucket:ListBucketVersions"

    const val OBS_BUCKET_HEAD = "obs:bucket:HeadBucket"

    const val OBS_BUCKET_GET_LOCATION = "obs:bucket:GetBucketLocation"

    const val OBS_BUCKET_DELETE = "obs:bucket:DeleteBucket"

    const val OBS_BUCKET_PUT_POLICY = "obs:bucket:PutBucketPolicy"

    const val OBS_BUCKET_GET_POLICY = "obs:bucket:GetBucketPolicy"

    const val OBS_BUCKET_DELETE_POLICY = "obs:bucket:DeleteBucketPolicy"

    const val OBS_BUCKET_PUT_ACL = "obs:bucket:PutBucketAcl"

    const val OBS_BUCKET_GET_ACL = "obs:bucket:GetBucketAcl"

    const val OBS_BUCKET_PUT_LOGGING = "obs:bucket:PutBucketLogging"

    const val OBS_BUCKET_GET_LOGGING = "obs:bucket:GetBucketLogging"

    const val OBS_BUCKET_PUT_LIFECYCLE_CONFIGURATION = "obs:bucket:PutLifecycleConfiguration"

    const val OBS_BUCKET_GET_LIFECYCLE_CONFIGURATION = "obs:bucket:GetLifecycleConfiguration"

    const val OBS_BUCKET_PUT_VERSIONING = "obs:bucket:PutBucketVersioning"

    const val OBS_BUCKET_GET_VERSIONING = "obs:bucket:GetBucketVersioning"

    const val OBS_BUCKET_PUT_STORAGE_POLICY = "obs:bucket:PutBucketStoragePolicy"

    const val OBS_BUCKET_GET_STORAGE_POLICY = "obs:bucket:GetBucketStoragePolicy"

    const val OBS_BUCKET_PUT_REPLICATION_CONFIGURATION = "obs:bucket:PutReplicationConfiguration"

    const val OBS_BUCKET_GET_REPLICATION_CONFIGURATION = "obs:bucket:GetReplicationConfiguration"

    const val OBS_BUCKET_DELETE_REPLICATION_CONFIGURATION = "obs:bucket:DeleteReplicationConfiguration"

    const val OBS_BUCKET_PUT_TAGGING = "obs:bucket:PutBucketTagging"

    const val OBS_BUCKET_GET_TAGGING = "obs:bucket:GetBucketTagging"

    const val OBS_BUCKET_DELETE_TAGGING = "obs:bucket:DeleteBucketTagging"

    const val OBS_BUCKET_PUT_QUOTA = "obs:bucket:PutBucketQuota"

    const val OBS_BUCKET_GET_QUOTA = "obs:bucket:GetBucketQuota"

    const val OBS_BUCKET_GET_STORAGE = "obs:bucket:GetBucketStorage"

    const val OBS_BUCKET_PUT_INVENTORY_CONFIGURATION = "obs:bucket:PutBucketInventoryConfiguration"

    const val OBS_BUCKET_GET_INVENTORY_CONFIGURATION = "obs:bucket:GetBucketInventoryConfiguration"

    const val OBS_BUCKET_DELETE_INVENTORY_CONFIGURATION = "obs:bucket:DeleteBucketInventoryConfiguration"

    const val OBS_BUCKET_PUT_CUSTOM_DOMAIN_CONFIGURATION = "obs:bucket:PutBucketCustomDomainConfiguration"

    const val OBS_BUCKET_GET_CUSTOM_DOMAIN_CONFIGURATION = "obs:bucket:GetBucketCustomDomainConfiguration"

    const val OBS_BUCKET_DELETE_CUSTOM_DOMAIN_CONFIGURATION = "obs:bucket:DeleteBucketCustomDomainConfiguration"

    const val OBS_BUCKET_PUT_ENCRYPTION_CONFIGURATION = "obs:bucket:PutEncryptionConfiguration"

    const val OBS_BUCKET_GET_ENCRYPTION_CONFIGURATION = "obs:bucket:GetEncryptionConfiguration"

    const val OBS_BUCKET_PUT_DIRECT_COLD_ACCESS_CONFIGURATION = "obs:bucket:PutDirectColdAccessConfiguration"

    const val OBS_BUCKET_GET_DIRECT_COLD_ACCESS_CONFIGURATION = "obs:bucket:GetDirectColdAccessConfiguration"

    const val OBS_BUCKET_DELETE_DIRECT_COLD_ACCESS_CONFIGURATION = "obs:bucket:DeleteDirectColdAccessConfiguration"

    const val OBS_BUCKET_PUT_WEBSITE = "obs:bucket:PutBucketWebsite"

    const val OBS_BUCKET_GET_WEBSITE = "obs:bucket:GetBucketWebsite"

    const val OBS_BUCKET_DELETE_WEBSITE = "obs:bucket:DeleteBucketWebsite"

    const val OBS_BUCKET_PUT_C_O_R_S = "obs:bucket:PutBucketCORS"

    const val OBS_BUCKET_GET_C_O_R_S = "obs:bucket:GetBucketCORS"

    const val OBS_BUCKET_PUT_OBJECT_LOCK_CONFIGURATION = "obs:bucket:PutBucketObjectLockConfiguration"

    const val OBS_BUCKET_GET_OBJECT_LOCK_CONFIGURATION = "obs:bucket:GetBucketObjectLockConfiguration"

    const val OBS_BUCKET_LIST_MULTIPART_UPLOADS = "obs:bucket:ListBucketMultipartUploads"

    @JvmField
    val OBS_BUCKET_DEFAULT = setOf(
        OBS_OBJECT_GET, OBS_OBJECT_PUT, OBS_OBJECT_DELETE,
        OBS_OBJECT_GET_ACL, OBS_OBJECT_PUT_ACL, OBS_OBJECT_MODIFY_META_DATA, OBS_OBJECT_GET_TAGGING,
        OBS_OBJECT_PUT_TAGGING, OBS_OBJECT_DELETE_TAGGING, OBS_OBJECT_LIST_MULTIPART_UPLOAD_PARTS,
        OBS_OBJECT_ABORT_MULTIPART_UPLOAD,

        OBS_BUCKET_LIST, OBS_BUCKET_PUT_ACL, OBS_BUCKET_GET_TAGGING, OBS_BUCKET_PUT_TAGGING,
        OBS_BUCKET_DELETE_TAGGING, OBS_BUCKET_LIST_MULTIPART_UPLOADS
    )

    @JvmField
    val OBS_OBJECT_DEFAULT = OBS_BUCKET_DEFAULT

    @JvmField
    val OBS_OBJECT_DEFAULT_PUT = setOf(
        OBS_OBJECT_PUT, OBS_OBJECT_PUT_ACL,
        OBS_OBJECT_MODIFY_META_DATA, OBS_OBJECT_PUT_TAGGING, OBS_OBJECT_ABORT_MULTIPART_UPLOAD
    )
}

