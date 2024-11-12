package live.lingting.framework.ali

/**
 * @author lingting 2024-09-18 14:58
 */
class AliActions private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        /**
         * 列举请求者拥有的所有Bucket。
         */
        const val OSS_BUCKET_LIST: String = "oss:ListBuckets"

        /**
         * 创建Bucket。
         */
        const val OSS_BUCKET_PUT: String = "oss:PutBucket"

        /**
         * 列举Bucket中所有Object的信息。
         */
        const val OSS_BUCKET_LISTS: String = "oss:ListObjects"

        /**
         * 查看Bucket相关信息。
         */
        const val OSS_BUCKET_GET_INFO: String = "oss:GetBucketInfo"

        /**
         * 查看Bucket位置信息。
         */
        const val OSS_BUCKET_GET_LOCATION: String = "oss:GetBucketLocation"

        /**
         * 设置指定Bucket的版本控制状态。
         */
        const val OSS_BUCKET_PUT_VERSIONING: String = "oss:PutBucketVersioning"

        /**
         * 获取指定Bucket的版本控制状态。
         */
        const val OSS_BUCKET_GET_VERSIONING: String = "oss:GetBucketVersioning"

        /**
         * 列出Bucket中包括删除标记（Delete Marker）在内的所有Object的版本信息。
         */
        const val OSS_BUCKET_LIST_VERSIONS: String = "oss:ListObjectVersions"

        /**
         * 设置或修改Bucket ACL。
         */
        const val OSS_BUCKET_PUT_ACL: String = "oss:PutBucketAcl"

        /**
         * 获取Bucket ACL。
         */
        const val OSS_BUCKET_GET_ACL: String = "oss:GetBucketAcl"

        /**
         * 删除某个Bucket。
         */
        const val OSS_BUCKET_DELETE: String = "oss:DeleteBucket"

        /**
         * 新建合规保留策略。
         */
        const val OSS_BUCKET_INITIATE_WORM: String = "oss:InitiateBucketWorm"

        /**
         * 删除未锁定的合规保留策略。
         */
        const val OSS_BUCKET_ABORT_WORM: String = "oss:AbortBucketWorm"

        /**
         * 锁定合规保留策略。
         */
        const val OSS_BUCKET_COMPLETE_WORM: String = "oss:CompleteBucketWorm"

        /**
         * 延长已锁定的合规保留策略对应Bucket中Object的保留天数。
         */
        const val OSS_BUCKET_EXTEND_WORM: String = "oss:ExtendBucketWorm"

        /**
         * 获取合规保留策略信息。
         */
        const val OSS_BUCKET_GET_WORM: String = "oss:GetBucketWorm"

        /**
         * 开启Bucket日志转存功能。
         */
        const val OSS_BUCKET_PUT_LOGGING: String = "oss:PutBucketLogging"

        /**
         * 查看Bucket日志转存配置。
         */
        const val OSS_BUCKET_GET_LOGGING: String = "oss:GetBucketLogging"

        /**
         * 关闭Bucket日志转存功能。
         */
        const val OSS_BUCKET_DELETE_LOGGING: String = "oss:DeleteBucketLogging"

        /**
         * 设置Bucket为静态网站托管模式并设置其跳转规则（RoutingRule）。
         */
        const val OSS_BUCKET_PUT_WEBSITE: String = "oss:PutBucketWebsite"

        /**
         * 查看Bucket的静态网站托管状态以及跳转规则。
         */
        const val OSS_BUCKET_GET_WEBSITE: String = "oss:GetBucketWebsite"

        /**
         * 关闭Bucket的静态网站托管模式以及跳转规则。
         */
        const val OSS_BUCKET_DELETE_WEBSITE: String = "oss:DeleteBucketWebsite"

        /**
         * 设置Bucket的防盗链。
         */
        const val OSS_BUCKET_PUT_REFERER: String = "oss:PutBucketReferer"

        /**
         * 查看Bucket的防盗链（Referer）相关配置。
         */
        const val OSS_BUCKET_GET_REFERER: String = "oss:GetBucketReferer"

        /**
         * 设置Bucket的生命周期规则。
         */
        const val OSS_BUCKET_PUT_LIFECYCLE: String = "oss:PutBucketLifecycle"

        /**
         * 查看Bucket的生命周期规则。
         */
        const val OSS_BUCKET_GET_LIFECYCLE: String = "oss:GetBucketLifecycle"

        /**
         * 删除Bucket的生命周期规则。
         */
        const val OSS_BUCKET_DELETE_LIFECYCLE: String = "oss:DeleteBucketLifecycle"

        /**
         * 设置Bucket传输加速。
         */
        const val OSS_BUCKET_PUT_TRANSFER_ACCELERATION: String = "oss:PutBucketTransferAcceleration"

        /**
         * 查看Bucket的传输加速配置。
         */
        const val OSS_BUCKET_GET_TRANSFER_ACCELERATION: String = "oss:GetBucketTransferAcceleration"

        /**
         * 列举所有执行中的Multipart Upload事件，即已经初始化但还未完成（Complete）或者还未中止（Abort）的Multipart Upload事件。
         */
        const val OSS_BUCKET_LIST_MULTIPART_UPLOADS: String = "oss:ListMultipartUploads"

        /**
         * 设置指定Bucket的跨域资源共享CORS（Cross-Origin Resource Sharing）规则。
         */
        const val OSS_BUCKET_PUT_CORS: String = "oss:PutBucketCors"

        /**
         * 获取指定Bucket当前的跨域资源共享CORS规则。
         */
        const val OSS_BUCKET_GET_CORS: String = "oss:GetBucketCors"

        /**
         * 关闭指定Bucket对应的跨域资源共享CORS功能并清空所有规则。
         */
        const val OSS_BUCKET_DELETE_CORS: String = "oss:DeleteBucketCors"

        /**
         * 设置指定Bucket的授权策略。
         */
        const val OSS_BUCKET_PUT_POLICY: String = "oss:PutBucketPolicy"

        /**
         * 获取指定Bucket的授权策略。
         */
        const val OSS_BUCKET_GET_POLICY: String = "oss:GetBucketPolicy"

        /**
         * 删除指定Bucket的授权策略。
         */
        const val OSS_BUCKET_DELETE_POLICY: String = "oss:DeleteBucketPolicy"

        /**
         * 添加或修改指定Bucket的标签。
         */
        const val OSS_BUCKET_PUT_TAGS: String = "oss:PutBucketTagging"

        /**
         * 获取Bucket的标签。
         */
        const val OSS_BUCKET_GET_TAGS: String = "oss:GetBucketTagging"

        /**
         * 删除Bucket的标签。
         */
        const val OSS_BUCKET_DELETE_TAGS: String = "oss:DeleteBucketTagging"

        /**
         * 配置Bucket的加密规则。
         */
        const val OSS_BUCKET_PUT_ENCRYPTION: String = "oss:PutBucketEncryption"

        /**
         * 获取Bucket的加密规则。
         */
        const val OSS_BUCKET_GET_ENCRYPTION: String = "oss:GetBucketEncryption"

        /**
         * 删除Bucket的加密规则。
         */
        const val OSS_BUCKET_DELETE_ENCRYPTION: String = "oss:DeleteBucketEncryption"

        /**
         * 设置请求者付费模式。
         */
        const val OSS_BUCKET_PUT_REQUEST_PAYMENT: String = "oss:PutBucketRequestPayment"

        /**
         * 获取请求者付费模式配置信息。
         */
        const val OSS_BUCKET_GET_REQUEST_PAYMENT: String = "oss:GetBucketRequestPayment"

        /**
         * 设置Bucket的数据复制规则。
         */
        const val OSS_BUCKET_PUT_REPLICATION: String = "oss:PutBucketReplication"

        /**
         * 为已有的跨区域复制规则开启或关闭数据复制时间控制（RTC）功能。
         */
        const val OSS_BUCKET_PUT_R_T_C: String = "oss:PutBucketRTC"

        /**
         * 获取Bucket已设置的数据复制规则。
         */
        const val OSS_BUCKET_GET_REPLICATION: String = "oss:GetBucketReplication"

        /**
         * 停止Bucket的数据复制并删除Bucket的复制配置。
         */
        const val OSS_BUCKET_DELETE_REPLICATION: String = "oss:DeleteBucketReplication"

        /**
         * 获取可复制到的目标Bucket的所在地域。
         */
        const val OSS_BUCKET_GET_REPLICATION_LOCATION: String = "oss:GetBucketReplicationLocation"

        /**
         * 获取Bucket的数据复制进度。
         */
        const val OSS_BUCKET_GET_REPLICATION_PROGRESS: String = "oss:GetBucketReplicationProgress"

        /**
         * 配置Bucket的清单（Inventory）规则。
         */
        const val OSS_BUCKET_PUT_INVENTORY: String = "oss:PutBucketInventory"

        /**
         * 查看Bucket中指定的清单任务。
         */
        const val OSS_BUCKET_GET_INVENTORY: String = "oss:GetBucketInventory"

        /**
         * 批量获取Bucket中所有清单任务。
         */
        const val OSS_BUCKET_LIST_INVENTORY: String = "oss:GetBucketInventory"

        /**
         * 删除Bucket中指定的清单任务。
         */
        const val OSS_BUCKET_DELETE_INVENTORY: String = "oss:DeleteBucketInventory"

        /**
         * 配置Bucket的访问跟踪状态。
         */
        const val OSS_BUCKET_PUT_ACCESS_MONITOR: String = "oss:PutBucketAccessMonitor"

        /**
         * 获取Bucket的访问跟踪状态。
         */
        const val OSS_BUCKET_GET_ACCESS_MONITOR: String = "oss:GetBucketAccessMonitor"

        /**
         * 开启Bucket的元数据管理功能。
         */
        const val OSS_BUCKET_OPEN_META_QUERY: String = "oss:OpenMetaQuery"

        /**
         * 获取Bucket的元数据索引库信息。
         */
        const val OSS_BUCKET_GET_META_QUERY_STATUS: String = "oss:GetMetaQueryStatus"

        /**
         * 查询满足指定条件的Object，并按照指定字段和排序方式列出Object信息。
         */
        const val OSS_BUCKET_DO_META_QUERY: String = "oss:DoMetaQuery"

        /**
         * 关闭Bucket的元数据管理功能.
         */
        const val OSS_BUCKET_CLOSE_META_QUERY: String = "oss:CloseMetaQuery"

        /**
         * 创建高防OSS实例。
         */
        const val OSS_BUCKET_INIT_USER_ANTI_D_DOS_INFO: String = "oss:InitUserAntiDDosInfo"

        /**
         * 更改高防OSS实例状态。
         */
        const val OSS_BUCKET_UPDATE_USER_ANTI_D_DOS_INFO: String = "oss:UpdateUserAntiDDosInfo"

        /**
         * 查询指定账号下的高防OSS实例信息。
         */
        const val OSS_BUCKET_GET_USER_ANTI_D_DOS_INFO: String = "oss:GetUserAntiDDosInfo"

        /**
         * 初始化Bucket防护。
         */
        const val OSS_BUCKET_INIT_ANTI_D_DOS_INFO: String = "oss:InitBucketAntiDDosInfo"

        /**
         * 更新Bucket防护状态。
         */
        const val OSS_BUCKET_UPDATE_ANTI_D_DOS_INFO: String = "oss:UpdateBucketAntiDDosInfo"

        /**
         * 获取Bucket防护信息列表。
         */
        const val OSS_BUCKET_LIST_ANTI_D_DOS_INFO: String = "oss:ListBucketAntiDDosInfo"

        /**
         * 设置Bucket所属资源组。
         */
        const val OSS_BUCKET_PUT_RESOURCE_GROUP: String = "oss:PutBucketResourceGroup"

        /**
         * 查询Bucket所属资源组ID。
         */
        const val OSS_BUCKET_GET_RESOURCE_GROUP: String = "oss:GetBucketResourceGroup"

        /**
         * 创建域名所有权验证所需的CnameToken。
         */
        const val OSS_BUCKET_CREATE_CNAME_TOKEN: String = "oss:CreateCnameToken"

        /**
         * 获取已创建的CnameToken。
         */
        const val OSS_BUCKET_GET_CNAME_TOKEN: String = "oss:GetCnameToken"

        /**
         * 为Bucket绑定自定义域名。
         */
        const val OSS_BUCKET_PUT_CNAME: String = "oss:PutCname"

        /**
         * 获取Bucket下绑定的所有的自定义域名（Cname）列表。
         */
        const val OSS_BUCKET_LIST_CNAME: String = "oss:ListCname"

        /**
         * 删除Bucket已绑定的Cname。
         */
        const val OSS_BUCKET_DELETE_CNAME: String = "oss:DeleteCname"

        /**
         * 设置图片样式。
         */
        const val OSS_BUCKET_PUT_STYLE: String = "oss:PutStyle"

        /**
         * 获取图片样式。
         */
        const val OSS_BUCKET_GET_STYLE: String = "oss:GetStyle"

        /**
         * 列举图片样式。
         */
        const val OSS_BUCKET_LIST_STYLE: String = "oss:ListStyle"

        /**
         * 删除图片样式。
         */
        const val OSS_BUCKET_DELETE_STYLE: String = "oss:DeleteStyle"

        /**
         * 为Bucket开启或关闭归档直读。
         */
        const val OSS_BUCKET_PUT_ARCHIVE_DIRECT_READ: String = "oss:PutBucketArchiveDirectRead"

        /**
         * 查看Bucket是否开启归档直读。
         */
        const val OSS_BUCKET_GET_ARCHIVE_DIRECT_READ: String = "oss:GetBucketArchiveDirectRead"

        /**
         * 创建接入点。
         */
        const val OSS_BUCKET_CREATE_ACCESS_POINT: String = "oss:CreateAccessPoint"

        /**
         * 获取单个接入点信息。
         */
        const val OSS_BUCKET_GET_ACCESS_POINT: String = "oss:GetAccessPoint"

        /**
         * 删除接入点。
         */
        const val OSS_BUCKET_DELETE_ACCESS_POINT: String = "oss:DeleteAccessPoint"

        /**
         * 获取用户级别及Bucket级别的接入点信息。
         */
        const val OSS_BUCKET_LIST_ACCESS_POINTS: String = "oss:ListAccessPoints"

        /**
         * 配置接入点策略。
         */
        const val OSS_BUCKET_PUT_ACCESS_POINT_POLICY: String = "oss:PutAccessPointPolicy"

        /**
         * 获取接入点策略信息。
         */
        const val OSS_BUCKET_GET_ACCESS_POINT_POLICY: String = "oss:GetAccessPointPolicy"

        /**
         * 删除接入点策略。
         */
        const val OSS_BUCKET_DELETE_ACCESS_POINT_POLICY: String = "oss:DeleteAccessPointPolicy"

        /**
         * 为Bucket开启或关闭TLS版本设置。
         */
        const val OSS_BUCKET_PUT_HTTPS_CONFIG: String = "oss:PutBucketHttpsConfig"

        /**
         * 查看Bucket的TLS版本设置。
         */
        const val OSS_BUCKET_GET_HTTPS_CONFIG: String = "oss:GetBucketHttpsConfig"

        /**
         * 复制过程涉及的列举权限。即允许OSS先列举源Bucket的历史数据，再逐一对历史数据进行复制。
         */
        const val OSS_BUCKET_REPLICATE_LIST: String = "oss:ReplicateList"

        /**
         * 创建对象FC接入点。
         */
        const val OSS_BUCKET_CREATE_ACCESS_POINT_FOR_PROCESS: String = "oss:CreateAccessPointForObjectProcess"

        /**
         * 获取对象FC接入点基础信息。
         */
        const val OSS_BUCKET_GET_ACCESS_POINT_FOR_PROCESS: String = "oss:GetAccessPointForObjectProcess"

        /**
         * 删除对象FC接入点。
         */
        const val OSS_BUCKET_DELETE_ACCESS_POINT_FOR_PROCESS: String = "oss:DeleteAccessPointForObjectProcess"

        /**
         * 获取用户级别的对象FC接入点信息。
         */
        const val OSS_BUCKET_LIST_ACCESS_POINTS_FOR_PROCESS: String = "oss:ListAccessPointsForObjectProcess"

        /**
         * 修改对象FC接入点配置。
         */
        const val OSS_BUCKET_PUT_ACCESS_POINT_CONFIG_FOR_PROCESS: String = "oss:PutAccessPointConfigForObjectProcess"

        /**
         * 获取对象FC接入点配置信息。
         */
        const val OSS_BUCKET_GET_ACCESS_POINT_CONFIG_FOR_PROCESS: String = "oss:GetAccessPointConfigForObjectProcess"

        /**
         * 为对象FC接入点配置权限策略。
         */
        const val OSS_BUCKET_PUT_ACCESS_POINT_POLICY_FOR_PROCESS: String = "oss:PutAccessPointPolicyForObjectProcess"

        /**
         * 获取对象FC接入点的权限策略配置。
         */
        const val OSS_BUCKET_GET_ACCESS_POINT_POLICY_FOR_PROCESS: String = "oss:GetAccessPointPolicyForObjectProcess"

        /**
         * 删除对象FC接入点的权限策略。
         */
        const val OSS_BUCKET_DELETE_ACCESS_POINT_POLICY_FOR_PROCESS: String = "oss:DeleteAccessPointPolicyForObjectProcess"

        /**
         * 自定义返回数据和响应标头。
         */
        const val OSS_BUCKET_WRITE_GET_RESPONSE: String = "oss:WriteGetObjectResponse"

        /**
         * 创建存储冗余转换任务。
         */
        const val OSS_BUCKET_CREATE_DATA_REDUNDANCY_TRANSITION: String = "oss:CreateBucketDataRedundancyTransition"

        /**
         * 获取存储冗余转换任务。
         */
        const val OSS_BUCKET_GET_DATA_REDUNDANCY_TRANSITION: String = "oss:GetBucketDataRedundancyTransition"

        /**
         * 删除存储冗余转换任务。
         */
        const val OSS_BUCKET_DELETE_DATA_REDUNDANCY_TRANSITION: String = "oss:DeleteBucketDataRedundancyTransition"

        /**
         * 列举某个Bucket下所有的存储冗余转换任务。
         */
        const val OSS_BUCKET_LIST_DATA_REDUNDANCY_TRANSITION: String = "oss:ListBucketDataRedundancyTransition"

        /**
         * 为某个Bucket开启阻止公共访问。
         */
        const val OSS_BUCKET_PUT_PUBLIC_ACCESS_BLOCK: String = "oss:PutBucketPublicAccessBlock"

        /**
         * 获取某个Bucket的阻止公共访问配置信息。
         */
        const val OSS_BUCKET_GET_PUBLIC_ACCESS_BLOCK: String = "oss:GetBucketPublicAccessBlock"

        /**
         * 删除某个Bucket的阻止公共访问配置信息。
         */
        const val OSS_BUCKET_DELETE_PUBLIC_ACCESS_BLOCK: String = "oss:DeleteBucketPublicAccessBlock"

        /**
         * 为某个接入点开启阻止公共访问。
         */
        const val OSS_BUCKET_PUT_ACCESS_POINT_PUBLIC_ACCESS_BLOCK: String = "oss:PutAccessPointPublicAccessBlock"

        /**
         * 获取某个接入点的阻止公共访问配置信息。
         */
        const val OSS_BUCKET_GET_ACCESS_POINT_PUBLIC_ACCESS_BLOCK: String = "oss:GetAccessPointPublicAccessBlock"

        /**
         * 删除某个接入点的阻止公共访问配置信息。
         */
        const val OSS_BUCKET_DELETE_ACCESS_POINT_PUBLIC_ACCESS_BLOCK: String = "oss:DeleteAccessPointPublicAccessBlock"

        /**
         * 查看当前Bucket Policy是否允许公共访问。
         */
        const val OSS_BUCKET_GET_POLICY_STATUS: String = "oss:GetBucketPolicyStatus"

        /**
         * 上传文件（Object）。
         */
        const val OSS_OBJECT_PUT: String = "oss:PutObject"

        /**
         * 通过HTML表单上传的方式将Object上传到指定Bucket。
         */
        const val OSS_OBJECT_POST: String = "oss:PutObject"

        /**
         * 以追加写的方式上传Object。
         */
        const val OSS_OBJECT_APPEND: String = "oss:PutObject"

        /**
         * 在使用Multipart Upload模式传输数据前，通知OSS初始化一个分片上传（Multipart Upload）事件。
         */
        const val OSS_OBJECT_INITIATE_MULTIPART_UPLOAD: String = "oss:PutObject"

        /**
         * 根据指定的Object名和uploadId来分块（Part）上传数据
         */
        const val OSS_OBJECT_UPLOAD_PART: String = "oss:PutObject"

        /**
         * 在将所有数据Part都上传完成后，需调用此接口来完成整个Object的分片上传。
         */
        const val OSS_OBJECT_COMPLETE_MULTIPART_UPLOAD: String = "oss:PutObject"

        /**
         * 取消MultipartUpload事件并删除对应的Part数据。
         */
        const val OSS_OBJECT_ABORT_MULTIPART_UPLOAD: String = "oss:AbortMultipartUpload"

        /**
         * 为OSS的目标文件（TargetObject）创建软链接（Symlink）。
         */
        const val OSS_OBJECT_PUT_SYMLINK: String = "oss:PutObject"

        /**
         * 获取某个Object。
         */
        const val OSS_OBJECT_GET: String = "oss:GetObject"

        /**
         * 获取某个Object的元数据。
         */
        const val OSS_OBJECT_HEAD: String = "oss:GetObject"

        /**
         * 获取Object的元数据信息，包括该Object的ETag、Size、LastModified信息。
         */
        const val OSS_OBJECT_GET_META: String = "oss:GetObject"

        /**
         * 对目标文件执行SQL语句，返回执行结果。
         */
        const val OSS_OBJECT_SELECT: String = "oss:GetObject"

        /**
         * 获取目标文件的软链接。
         */
        const val OSS_OBJECT_GET_SYMLINK: String = "oss:GetObject"

        /**
         * 删除某个Object。
         */
        const val OSS_OBJECT_DELETE: String = "oss:DeleteObject"

        /**
         * 拷贝同一地域下相同或不同Bucket之间的Object。
         */
        const val OSS_OBJECT_COPY: String = "oss:GetObject=oss:PutObject"

        /**
         * 在UploadPart请求的基础上增加一个请求头x-oss-copy-source来调用UploadPartCopy接口，实现从一个已存在的Object中拷贝数据来上传一个Part。
         */
        const val OSS_OBJECT_UPLOAD_PART_COPY: String = "oss:GetObject=oss:PutObject"

        /**
         * 列举指定Upload ID所属的所有已经上传成功的Part。
         */
        const val OSS_OBJECT_LIST_PARTS: String = "oss:ListParts"

        /**
         * 修改Bucket下某个Object的ACL。
         */
        const val OSS_OBJECT_PUT_ACL: String = "oss:PutObjectAcl"

        /**
         * 获取Bucket下某个Object的ACL。
         */
        const val OSS_OBJECT_GET_ACL: String = "oss:GetObjectAcl"

        /**
         * 解冻归档存储、冷归档存储或者深度冷归档存储类型的Object。
         */
        const val OSS_OBJECT_RESTORE: String = "oss:RestoreObject"

        /**
         * 设置或更新Object的标签（Tagging）信息。
         */
        const val OSS_OBJECT_PUT_TAGGING: String = "oss:PutObjectTagging"

        /**
         * 获取Object的标签信息。
         */
        const val OSS_OBJECT_GET_TAGGING: String = "oss:GetObjectTagging"

        /**
         * 删除指定Object的标签信息。
         */
        const val OSS_OBJECT_DELETE_TAGGING: String = "oss:DeleteObjectTagging"

        /**
         * 下载指定版本Object。
         */
        const val OSS_OBJECT_GET_VERSION: String = "oss:GetObjectVersion"

        /**
         * 修改Bucket下指定版本Object的ACL。
         */
        const val OSS_OBJECT_PUT_VERSION_ACL: String = "oss:PutObjectVersionAcl"

        /**
         * 获取Bucket下指定版本Object的ACL。
         */
        const val OSS_OBJECT_GET_VERSION_ACL: String = "oss:GetObjectVersionAcl"

        /**
         * 解冻指定版本的归档存储、冷归档存储或者深度冷归档存储类型的Object。
         */
        const val OSS_OBJECT_RESTORE_VERSION: String = "oss:RestoreObjectVersion"

        /**
         * 删除指定版本Object。
         */
        const val OSS_OBJECT_DELETE_VERSION: String = "oss:DeleteObjectVersion"

        /**
         * 设置或更新指定版本Object的标签（Tagging）信息。
         */
        const val OSS_OBJECT_PUT_VERSION_TAGGING: String = "oss:PutObjectVersionTagging"

        /**
         * 获取指定版本Object的标签信息。
         */
        const val OSS_OBJECT_GET_VERSION_TAGGING: String = "oss:GetObjectVersionTagging"

        /**
         * 删除指定版本Object的标签信息。
         */
        const val OSS_OBJECT_DELETE_VERSION_TAGGING: String = "oss:DeleteObjectVersionTagging"

        /**
         * 通过RTMP协议上传音视频数据前，必须先调用该接口创建一个LiveChannel。
         */
        const val OSS_OBJECT_PUT_LIVE_CHANNEL: String = "oss:PutLiveChannel"

        /**
         * 列举指定的LiveChannel。
         */
        const val OSS_OBJECT_LIST_LIVE_CHANNEL: String = "oss:ListLiveChannel"

        /**
         * 删除指定的LiveChannel。
         */
        const val OSS_OBJECT_DELETE_LIVE_CHANNEL: String = "oss:DeleteLiveChannel"

        /**
         * 在启用（enabled）和禁用（disabled）两种状态之间进行切换。
         */
        const val OSS_OBJECT_PUT_LIVE_CHANNEL_STATUS: String = "oss:PutLiveChannelStatus"

        /**
         * 获取指定LiveChannel的配置信息。
         */
        const val OSS_OBJECT_GET_LIVE_CHANNEL_INFO: String = "oss:GetLiveChannel"

        /**
         * 获取指定LiveChannel的推流状态信息。
         */
        const val OSS_OBJECT_GET_LIVE_CHANNEL_STAT: String = "oss:GetLiveChannelStat"

        /**
         * 获取指定LiveChannel的推流记录。
         */
        const val OSS_OBJECT_GET_LIVE_CHANNEL_HISTORY: String = "oss:GetLiveChannelHistory"

        /**
         * 为指定的LiveChannel生成一个点播用的播放列表。
         */
        const val OSS_OBJECT_POST_VOD_PLAYLIST: String = "oss:PostVodPlaylist"

        /**
         * 查看指定LiveChannel在指定时间段内推流生成的播放列表。
         */
        const val OSS_OBJECT_GET_VOD_PLAYLIST: String = "oss:GetVodPlaylist"

        /**
         * 将音频和视频数据流推送到RTMP。
         */
        const val OSS_OBJECT_PUBLISH_RTMP_STREAM: String = "oss:PublishRtmpStream"

        /**
         * 基于图片AI技术检测图片标签和置信度。
         */
        const val OSS_OBJECT_PROCESS_IMM: String = "oss:ProcessImm"

        /**
         * 保存处理后的图片至指定Bucket。
         */
        const val OSS_OBJECT_IMG_SAVE_AS: String = "oss:PostProcessTask"

        /**
         * 复制过程涉及的读权限。即允许OSS读取源Bucket和目标Bucket中的数据与元数据，包括Object、Part、Multipart Upload等。
         */
        const val OSS_OBJECT_REPLICATE_GET: String = "oss:ReplicateGet"

        /**
         * 复制过程涉及的写权限。即允许OSS对目标Bucket复制相关的写入类操作，包括写入Object、Multipart
         * Upload、Part和Symlink，修改元数据信息等。
         */
        const val OSS_OBJECT_REPLICATE_PUT: String = "oss:ReplicatePut"

        /**
         * 复制过程涉及的删除权限。即允许OSS对目标Bucket复制相关的删除操作，包括DeleteObject、AbortMultipartUpload、DeleteMarker等。
         */
        const val OSS_OBJECT_REPLICATE_DELETE: String = "oss:ReplicateDelete"

        val OSS_BUCKET_DEFAULT: List<String?> = java.util.List.of(
            OSS_OBJECT_GET, OSS_OBJECT_PUT, OSS_OBJECT_DELETE,
            OSS_OBJECT_GET_ACL, OSS_OBJECT_PUT_ACL, OSS_OBJECT_GET_META, OSS_OBJECT_GET_TAGGING, OSS_OBJECT_PUT_TAGGING,
            OSS_OBJECT_DELETE_TAGGING, OSS_OBJECT_INITIATE_MULTIPART_UPLOAD, OSS_OBJECT_COMPLETE_MULTIPART_UPLOAD,
            OSS_OBJECT_ABORT_MULTIPART_UPLOAD,

            OSS_BUCKET_LIST, OSS_BUCKET_PUT_ACL, OSS_BUCKET_GET_TAGS, OSS_BUCKET_PUT_TAGS, OSS_BUCKET_DELETE_TAGS,
            OSS_BUCKET_LIST_MULTIPART_UPLOADS
        )

        val OSS_OBJECT_DEFAULT: List<String?> = OSS_BUCKET_DEFAULT

        val OSS_OBJECT_DEFAULT_PUT: List<String> = java.util.List.of(
            OSS_OBJECT_PUT, OSS_OBJECT_PUT_ACL,
            OSS_OBJECT_GET_META, OSS_OBJECT_PUT_TAGGING, OSS_OBJECT_ABORT_MULTIPART_UPLOAD
        )
    }
}
