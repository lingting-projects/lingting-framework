package live.lingting.framework.aws.s3

/**
 * @author lingting 2024-09-18 14:58
 */
object AwsS3Actions {

    /**
     * 列举请求者拥有的所有Bucket。
     */
    const val S3_BUCKET_LIST = "s3:ListBuckets"

    /**
     * 创建Bucket。
     */
    const val S3_BUCKET_PUT = "s3:PutBucket"

    /**
     * 列举Bucket中所有Object的信息。
     */
    const val S3_BUCKET_OBJECT_LIST = "s3:ListObjects"

    /**
     * 查看Bucket相关信息。
     */
    const val S3_BUCKET_GET_INFO = "s3:GetBucketInfo"

    /**
     * 查看Bucket位置信息。
     */
    const val S3_BUCKET_GET_LOCATION = "s3:GetBucketLocation"

    /**
     * 设置指定Bucket的版本控制状态。
     */
    const val S3_BUCKET_PUT_VERSIONING = "s3:PutBucketVersioning"

    /**
     * 获取指定Bucket的版本控制状态。
     */
    const val S3_BUCKET_GET_VERSIONING = "s3:GetBucketVersioning"

    /**
     * 列出Bucket中包括删除标记（Delete Marker）在内的所有Object的版本信息。
     */
    const val S3_BUCKET_LIST_VERSIONS = "s3:ListObjectVersions"

    /**
     * 设置或修改Bucket ACL。
     */
    const val S3_BUCKET_PUT_ACL = "s3:PutBucketAcl"

    /**
     * 获取Bucket ACL。
     */
    const val S3_BUCKET_GET_ACL = "s3:GetBucketAcl"

    /**
     * 删除某个Bucket。
     */
    const val S3_BUCKET_DELETE = "s3:DeleteBucket"

    /**
     * 新建合规保留策略。
     */
    const val S3_BUCKET_INITIATE_WORM = "s3:InitiateBucketWorm"

    /**
     * 删除未锁定的合规保留策略。
     */
    const val S3_BUCKET_ABORT_WORM = "s3:AbortBucketWorm"

    /**
     * 锁定合规保留策略。
     */
    const val S3_BUCKET_COMPLETE_WORM = "s3:CompleteBucketWorm"

    /**
     * 延长已锁定的合规保留策略对应Bucket中Object的保留天数。
     */
    const val S3_BUCKET_EXTEND_WORM = "s3:ExtendBucketWorm"

    /**
     * 获取合规保留策略信息。
     */
    const val S3_BUCKET_GET_WORM = "s3:GetBucketWorm"

    /**
     * 开启Bucket日志转存功能。
     */
    const val S3_BUCKET_PUT_LOGGING = "s3:PutBucketLogging"

    /**
     * 查看Bucket日志转存配置。
     */
    const val S3_BUCKET_GET_LOGGING = "s3:GetBucketLogging"

    /**
     * 关闭Bucket日志转存功能。
     */
    const val S3_BUCKET_DELETE_LOGGING = "s3:DeleteBucketLogging"

    /**
     * 设置Bucket为静态网站托管模式并设置其跳转规则（RoutingRule）。
     */
    const val S3_BUCKET_PUT_WEBSITE = "s3:PutBucketWebsite"

    /**
     * 查看Bucket的静态网站托管状态以及跳转规则。
     */
    const val S3_BUCKET_GET_WEBSITE = "s3:GetBucketWebsite"

    /**
     * 关闭Bucket的静态网站托管模式以及跳转规则。
     */
    const val S3_BUCKET_DELETE_WEBSITE = "s3:DeleteBucketWebsite"

    /**
     * 设置Bucket的防盗链。
     */
    const val S3_BUCKET_PUT_REFERER = "s3:PutBucketReferer"

    /**
     * 查看Bucket的防盗链（Referer）相关配置。
     */
    const val S3_BUCKET_GET_REFERER = "s3:GetBucketReferer"

    /**
     * 设置Bucket的生命周期规则。
     */
    const val S3_BUCKET_PUT_LIFECYCLE = "s3:PutBucketLifecycle"

    /**
     * 查看Bucket的生命周期规则。
     */
    const val S3_BUCKET_GET_LIFECYCLE = "s3:GetBucketLifecycle"

    /**
     * 删除Bucket的生命周期规则。
     */
    const val S3_BUCKET_DELETE_LIFECYCLE = "s3:DeleteBucketLifecycle"

    /**
     * 设置Bucket传输加速。
     */
    const val S3_BUCKET_PUT_TRANSFER_ACCELERATION = "s3:PutBucketTransferAcceleration"

    /**
     * 查看Bucket的传输加速配置。
     */
    const val S3_BUCKET_GET_TRANSFER_ACCELERATION = "s3:GetBucketTransferAcceleration"

    /**
     * 列举所有执行中的Multipart Upload事件，即已经初始化但还未完成（Complete）或者还未中止（Abort）的Multipart Upload事件。
     */
    const val S3_BUCKET_LIST_MULTIPART_UPLOADS = "s3:ListMultipartUploads"

    /**
     * 设置指定Bucket的跨域资源共享CORS（Crs3-Origin Resource Sharing）规则。
     */
    const val S3_BUCKET_PUT_CORS = "s3:PutBucketCors"

    /**
     * 获取指定Bucket当前的跨域资源共享CORS规则。
     */
    const val S3_BUCKET_GET_CORS = "s3:GetBucketCors"

    /**
     * 关闭指定Bucket对应的跨域资源共享CORS功能并清空所有规则。
     */
    const val S3_BUCKET_DELETE_CORS = "s3:DeleteBucketCors"

    /**
     * 设置指定Bucket的授权策略。
     */
    const val S3_BUCKET_PUT_POLICY = "s3:PutBucketPolicy"

    /**
     * 获取指定Bucket的授权策略。
     */
    const val S3_BUCKET_GET_POLICY = "s3:GetBucketPolicy"

    /**
     * 删除指定Bucket的授权策略。
     */
    const val S3_BUCKET_DELETE_POLICY = "s3:DeleteBucketPolicy"

    /**
     * 添加或修改指定Bucket的标签。
     */
    const val S3_BUCKET_PUT_TAGS = "s3:PutBucketTagging"

    /**
     * 获取Bucket的标签。
     */
    const val S3_BUCKET_GET_TAGS = "s3:GetBucketTagging"

    /**
     * 删除Bucket的标签。
     */
    const val S3_BUCKET_DELETE_TAGS = "s3:DeleteBucketTagging"

    /**
     * 配置Bucket的加密规则。
     */
    const val S3_BUCKET_PUT_ENCRYPTION = "s3:PutBucketEncryption"

    /**
     * 获取Bucket的加密规则。
     */
    const val S3_BUCKET_GET_ENCRYPTION = "s3:GetBucketEncryption"

    /**
     * 删除Bucket的加密规则。
     */
    const val S3_BUCKET_DELETE_ENCRYPTION = "s3:DeleteBucketEncryption"

    /**
     * 设置请求者付费模式。
     */
    const val S3_BUCKET_PUT_REQUEST_PAYMENT = "s3:PutBucketRequestPayment"

    /**
     * 获取请求者付费模式配置信息。
     */
    const val S3_BUCKET_GET_REQUEST_PAYMENT = "s3:GetBucketRequestPayment"

    /**
     * 设置Bucket的数据复制规则。
     */
    const val S3_BUCKET_PUT_REPLICATION = "s3:PutBucketReplication"

    /**
     * 为已有的跨区域复制规则开启或关闭数据复制时间控制（RTC）功能。
     */
    const val S3_BUCKET_PUT_R_T_C = "s3:PutBucketRTC"

    /**
     * 获取Bucket已设置的数据复制规则。
     */
    const val S3_BUCKET_GET_REPLICATION = "s3:GetBucketReplication"

    /**
     * 停止Bucket的数据复制并删除Bucket的复制配置。
     */
    const val S3_BUCKET_DELETE_REPLICATION = "s3:DeleteBucketReplication"

    /**
     * 获取可复制到的目标Bucket的所在地域。
     */
    const val S3_BUCKET_GET_REPLICATION_LOCATION = "s3:GetBucketReplicationLocation"

    /**
     * 获取Bucket的数据复制进度。
     */
    const val S3_BUCKET_GET_REPLICATION_PROGRESS = "s3:GetBucketReplicationProgress"

    /**
     * 配置Bucket的清单（Inventory）规则。
     */
    const val S3_BUCKET_PUT_INVENTORY = "s3:PutBucketInventory"

    /**
     * 查看Bucket中指定的清单任务。
     */
    const val S3_BUCKET_GET_INVENTORY = "s3:GetBucketInventory"

    /**
     * 批量获取Bucket中所有清单任务。
     */
    const val S3_BUCKET_LIST_INVENTORY = "s3:GetBucketInventory"

    /**
     * 删除Bucket中指定的清单任务。
     */
    const val S3_BUCKET_DELETE_INVENTORY = "s3:DeleteBucketInventory"

    /**
     * 配置Bucket的访问跟踪状态。
     */
    const val S3_BUCKET_PUT_ACCESS_MONITOR = "s3:PutBucketAccessMonitor"

    /**
     * 获取Bucket的访问跟踪状态。
     */
    const val S3_BUCKET_GET_ACCESS_MONITOR = "s3:GetBucketAccessMonitor"

    /**
     * 开启Bucket的元数据管理功能。
     */
    const val S3_BUCKET_OPEN_META_QUERY = "s3:OpenMetaQuery"

    /**
     * 获取Bucket的元数据索引库信息。
     */
    const val S3_BUCKET_GET_META_QUERY_STATUS = "s3:GetMetaQueryStatus"

    /**
     * 查询满足指定条件的Object，并按照指定字段和排序方式列出Object信息。
     */
    const val S3_BUCKET_DO_META_QUERY = "s3:DoMetaQuery"

    /**
     * 关闭Bucket的元数据管理功能.
     */
    const val S3_BUCKET_CLOSE_META_QUERY = "s3:CloseMetaQuery"

    /**
     * 创建高防S3实例。
     */
    const val S3_BUCKET_INIT_USER_ANTI_D_DOS_INFO = "s3:InitUserAntiDDosInfo"

    /**
     * 更改高防S3实例状态。
     */
    const val S3_BUCKET_UPDATE_USER_ANTI_D_DOS_INFO = "s3:UpdateUserAntiDDosInfo"

    /**
     * 查询指定账号下的高防S3实例信息。
     */
    const val S3_BUCKET_GET_USER_ANTI_D_DOS_INFO = "s3:GetUserAntiDDosInfo"

    /**
     * 初始化Bucket防护。
     */
    const val S3_BUCKET_INIT_ANTI_D_DOS_INFO = "s3:InitBucketAntiDDosInfo"

    /**
     * 更新Bucket防护状态。
     */
    const val S3_BUCKET_UPDATE_ANTI_D_DOS_INFO = "s3:UpdateBucketAntiDDosInfo"

    /**
     * 获取Bucket防护信息列表。
     */
    const val S3_BUCKET_LIST_ANTI_D_DOS_INFO = "s3:ListBucketAntiDDosInfo"

    /**
     * 设置Bucket所属资源组。
     */
    const val S3_BUCKET_PUT_RESOURCE_GROUP = "s3:PutBucketResourceGroup"

    /**
     * 查询Bucket所属资源组ID。
     */
    const val S3_BUCKET_GET_RESOURCE_GROUP = "s3:GetBucketResourceGroup"

    /**
     * 创建域名所有权验证所需的CnameToken。
     */
    const val S3_BUCKET_CREATE_CNAME_TOKEN = "s3:CreateCnameToken"

    /**
     * 获取已创建的CnameToken。
     */
    const val S3_BUCKET_GET_CNAME_TOKEN = "s3:GetCnameToken"

    /**
     * 为Bucket绑定自定义域名。
     */
    const val S3_BUCKET_PUT_CNAME = "s3:PutCname"

    /**
     * 获取Bucket下绑定的所有的自定义域名（Cname）列表。
     */
    const val S3_BUCKET_LIST_CNAME = "s3:ListCname"

    /**
     * 删除Bucket已绑定的Cname。
     */
    const val S3_BUCKET_DELETE_CNAME = "s3:DeleteCname"

    /**
     * 设置图片样式。
     */
    const val S3_BUCKET_PUT_STYLE = "s3:PutStyle"

    /**
     * 获取图片样式。
     */
    const val S3_BUCKET_GET_STYLE = "s3:GetStyle"

    /**
     * 列举图片样式。
     */
    const val S3_BUCKET_LIST_STYLE = "s3:ListStyle"

    /**
     * 删除图片样式。
     */
    const val S3_BUCKET_DELETE_STYLE = "s3:DeleteStyle"

    /**
     * 为Bucket开启或关闭归档直读。
     */
    const val S3_BUCKET_PUT_ARCHIVE_DIRECT_READ = "s3:PutBucketArchiveDirectRead"

    /**
     * 查看Bucket是否开启归档直读。
     */
    const val S3_BUCKET_GET_ARCHIVE_DIRECT_READ = "s3:GetBucketArchiveDirectRead"

    /**
     * 创建接入点。
     */
    const val S3_BUCKET_CREATE_ACCESS_POINT = "s3:CreateAccessPoint"

    /**
     * 获取单个接入点信息。
     */
    const val S3_BUCKET_GET_ACCESS_POINT = "s3:GetAccessPoint"

    /**
     * 删除接入点。
     */
    const val S3_BUCKET_DELETE_ACCESS_POINT = "s3:DeleteAccessPoint"

    /**
     * 获取用户级别及Bucket级别的接入点信息。
     */
    const val S3_BUCKET_LIST_ACCESS_POINTS = "s3:ListAccessPoints"

    /**
     * 配置接入点策略。
     */
    const val S3_BUCKET_PUT_ACCESS_POINT_POLICY = "s3:PutAccessPointPolicy"

    /**
     * 获取接入点策略信息。
     */
    const val S3_BUCKET_GET_ACCESS_POINT_POLICY = "s3:GetAccessPointPolicy"

    /**
     * 删除接入点策略。
     */
    const val S3_BUCKET_DELETE_ACCESS_POINT_POLICY = "s3:DeleteAccessPointPolicy"

    /**
     * 为Bucket开启或关闭TLS版本设置。
     */
    const val S3_BUCKET_PUT_HTTPS_CONFIG = "s3:PutBucketHttpsConfig"

    /**
     * 查看Bucket的TLS版本设置。
     */
    const val S3_BUCKET_GET_HTTPS_CONFIG = "s3:GetBucketHttpsConfig"

    /**
     * 复制过程涉及的列举权限。即允许S3先列举源Bucket的历史数据，再逐一对历史数据进行复制。
     */
    const val S3_BUCKET_REPLICATE_LIST = "s3:ReplicateList"

    /**
     * 创建对象FC接入点。
     */
    const val S3_BUCKET_CREATE_ACCESS_POINT_FOR_PROCESS = "s3:CreateAccessPointForObjectProcess"

    /**
     * 获取对象FC接入点基础信息。
     */
    const val S3_BUCKET_GET_ACCESS_POINT_FOR_PROCESS = "s3:GetAccessPointForObjectProcess"

    /**
     * 删除对象FC接入点。
     */
    const val S3_BUCKET_DELETE_ACCESS_POINT_FOR_PROCESS = "s3:DeleteAccessPointForObjectProcess"

    /**
     * 获取用户级别的对象FC接入点信息。
     */
    const val S3_BUCKET_LIST_ACCESS_POINTS_FOR_PROCESS = "s3:ListAccessPointsForObjectProcess"

    /**
     * 修改对象FC接入点配置。
     */
    const val S3_BUCKET_PUT_ACCESS_POINT_CONFIG_FOR_PROCESS = "s3:PutAccessPointConfigForObjectProcess"

    /**
     * 获取对象FC接入点配置信息。
     */
    const val S3_BUCKET_GET_ACCESS_POINT_CONFIG_FOR_PROCESS = "s3:GetAccessPointConfigForObjectProcess"

    /**
     * 为对象FC接入点配置权限策略。
     */
    const val S3_BUCKET_PUT_ACCESS_POINT_POLICY_FOR_PROCESS = "s3:PutAccessPointPolicyForObjectProcess"

    /**
     * 获取对象FC接入点的权限策略配置。
     */
    const val S3_BUCKET_GET_ACCESS_POINT_POLICY_FOR_PROCESS = "s3:GetAccessPointPolicyForObjectProcess"

    /**
     * 删除对象FC接入点的权限策略。
     */
    const val S3_BUCKET_DELETE_ACCESS_POINT_POLICY_FOR_PROCESS = "s3:DeleteAccessPointPolicyForObjectProcess"

    /**
     * 自定义返回数据和响应标头。
     */
    const val S3_BUCKET_WRITE_GET_RESPONSE = "s3:WriteGetObjectResponse"

    /**
     * 创建存储冗余转换任务。
     */
    const val S3_BUCKET_CREATE_DATA_REDUNDANCY_TRANSITION = "s3:CreateBucketDataRedundancyTransition"

    /**
     * 获取存储冗余转换任务。
     */
    const val S3_BUCKET_GET_DATA_REDUNDANCY_TRANSITION = "s3:GetBucketDataRedundancyTransition"

    /**
     * 删除存储冗余转换任务。
     */
    const val S3_BUCKET_DELETE_DATA_REDUNDANCY_TRANSITION = "s3:DeleteBucketDataRedundancyTransition"

    /**
     * 列举某个Bucket下所有的存储冗余转换任务。
     */
    const val S3_BUCKET_LIST_DATA_REDUNDANCY_TRANSITION = "s3:ListBucketDataRedundancyTransition"

    /**
     * 为某个Bucket开启阻止公共访问。
     */
    const val S3_BUCKET_PUT_PUBLIC_ACCESS_BLOCK = "s3:PutBucketPublicAccessBlock"

    /**
     * 获取某个Bucket的阻止公共访问配置信息。
     */
    const val S3_BUCKET_GET_PUBLIC_ACCESS_BLOCK = "s3:GetBucketPublicAccessBlock"

    /**
     * 删除某个Bucket的阻止公共访问配置信息。
     */
    const val S3_BUCKET_DELETE_PUBLIC_ACCESS_BLOCK = "s3:DeleteBucketPublicAccessBlock"

    /**
     * 为某个接入点开启阻止公共访问。
     */
    const val S3_BUCKET_PUT_ACCESS_POINT_PUBLIC_ACCESS_BLOCK = "s3:PutAccessPointPublicAccessBlock"

    /**
     * 获取某个接入点的阻止公共访问配置信息。
     */
    const val S3_BUCKET_GET_ACCESS_POINT_PUBLIC_ACCESS_BLOCK = "s3:GetAccessPointPublicAccessBlock"

    /**
     * 删除某个接入点的阻止公共访问配置信息。
     */
    const val S3_BUCKET_DELETE_ACCESS_POINT_PUBLIC_ACCESS_BLOCK = "s3:DeleteAccessPointPublicAccessBlock"

    /**
     * 查看当前Bucket Policy是否允许公共访问。
     */
    const val S3_BUCKET_GET_POLICY_STATUS = "s3:GetBucketPolicyStatus"

    /**
     * 上传文件（Object）。
     */
    const val S3_OBJECT_PUT = "s3:PutObject"

    /**
     * 通过HTML表单上传的方式将Object上传到指定Bucket。
     */
    const val S3_OBJECT_POST = S3_OBJECT_PUT

    /**
     * 以追加写的方式上传Object。
     */
    const val S3_OBJECT_APPEND = S3_OBJECT_PUT

    /**
     * 在使用Multipart Upload模式传输数据前，通知S3初始化一个分片上传（Multipart Upload）事件。
     */
    const val S3_OBJECT_INITIATE_MULTIPART_UPLOAD = S3_OBJECT_PUT

    /**
     * 根据指定的Object名和uploadId来分块（Part）上传数据
     */
    const val S3_OBJECT_UPLOAD_PART = S3_OBJECT_PUT

    /**
     * 在将所有数据Part都上传完成后，需调用此接口来完成整个Object的分片上传。
     */
    const val S3_OBJECT_COMPLETE_MULTIPART_UPLOAD = S3_OBJECT_PUT

    /**
     * 取消MultipartUpload事件并删除对应的Part数据。
     */
    const val S3_OBJECT_ABORT_MULTIPART_UPLOAD = "s3:AbortMultipartUpload"

    /**
     * 为S3的目标文件（TargetObject）创建软链接（Symlink）。
     */
    const val S3_OBJECT_PUT_SYMLINK = S3_OBJECT_PUT

    /**
     * 获取某个Object。
     */
    const val S3_OBJECT_GET = "s3:GetObject"

    /**
     * 获取某个Object的元数据。
     */
    const val S3_OBJECT_HEAD = S3_OBJECT_GET

    /**
     * 获取Object的元数据信息，包括该Object的ETag、Size、LastModified信息。
     */
    const val S3_OBJECT_GET_META = S3_OBJECT_GET

    /**
     * 对目标文件执行SQL语句，返回执行结果。
     */
    const val S3_OBJECT_SELECT = S3_OBJECT_GET

    /**
     * 获取目标文件的软链接。
     */
    const val S3_OBJECT_GET_SYMLINK = S3_OBJECT_GET

    /**
     * 删除某个Object。
     */
    const val S3_OBJECT_DELETE = "s3:DeleteObject"

    /**
     * 拷贝同一地域下相同或不同Bucket之间的Object。
     */
    const val S3_OBJECT_COPY = "s3:GetObject=s3:PutObject"

    /**
     * 在UploadPart请求的基础上增加一个请求头x-s3-copy-source来调用UploadPartCopy接口，实现从一个已存在的Object中拷贝数据来上传一个Part。
     */
    const val S3_OBJECT_UPLOAD_PART_COPY = "s3:GetObject=s3:PutObject"

    /**
     * 列举指定Upload ID所属的所有已经上传成功的Part。
     */
    const val S3_OBJECT_LIST_PARTS = "s3:ListParts"

    /**
     * 修改Bucket下某个Object的ACL。
     */
    const val S3_OBJECT_PUT_ACL = "s3:PutObjectAcl"

    /**
     * 获取Bucket下某个Object的ACL。
     */
    const val S3_OBJECT_GET_ACL = "s3:GetObjectAcl"

    /**
     * 解冻归档存储、冷归档存储或者深度冷归档存储类型的Object。
     */
    const val S3_OBJECT_RESTORE = "s3:RestoreObject"

    /**
     * 设置或更新Object的标签（Tagging）信息。
     */
    const val S3_OBJECT_PUT_TAGGING = "s3:PutObjectTagging"

    /**
     * 获取Object的标签信息。
     */
    const val S3_OBJECT_GET_TAGGING = "s3:GetObjectTagging"

    /**
     * 删除指定Object的标签信息。
     */
    const val S3_OBJECT_DELETE_TAGGING = "s3:DeleteObjectTagging"

    /**
     * 下载指定版本Object。
     */
    const val S3_OBJECT_GET_VERSION = "s3:GetObjectVersion"

    /**
     * 修改Bucket下指定版本Object的ACL。
     */
    const val S3_OBJECT_PUT_VERSION_ACL = "s3:PutObjectVersionAcl"

    /**
     * 获取Bucket下指定版本Object的ACL。
     */
    const val S3_OBJECT_GET_VERSION_ACL = "s3:GetObjectVersionAcl"

    /**
     * 解冻指定版本的归档存储、冷归档存储或者深度冷归档存储类型的Object。
     */
    const val S3_OBJECT_RESTORE_VERSION = "s3:RestoreObjectVersion"

    /**
     * 删除指定版本Object。
     */
    const val S3_OBJECT_DELETE_VERSION = "s3:DeleteObjectVersion"

    /**
     * 设置或更新指定版本Object的标签（Tagging）信息。
     */
    const val S3_OBJECT_PUT_VERSION_TAGGING = "s3:PutObjectVersionTagging"

    /**
     * 获取指定版本Object的标签信息。
     */
    const val S3_OBJECT_GET_VERSION_TAGGING = "s3:GetObjectVersionTagging"

    /**
     * 删除指定版本Object的标签信息。
     */
    const val S3_OBJECT_DELETE_VERSION_TAGGING = "s3:DeleteObjectVersionTagging"

    /**
     * 通过RTMP协议上传音视频数据前，必须先调用该接口创建一个LiveChannel。
     */
    const val S3_OBJECT_PUT_LIVE_CHANNEL = "s3:PutLiveChannel"

    /**
     * 列举指定的LiveChannel。
     */
    const val S3_OBJECT_LIST_LIVE_CHANNEL = "s3:ListLiveChannel"

    /**
     * 删除指定的LiveChannel。
     */
    const val S3_OBJECT_DELETE_LIVE_CHANNEL = "s3:DeleteLiveChannel"

    /**
     * 在启用（enabled）和禁用（disabled）两种状态之间进行切换。
     */
    const val S3_OBJECT_PUT_LIVE_CHANNEL_STATUS = "s3:PutLiveChannelStatus"

    /**
     * 获取指定LiveChannel的配置信息。
     */
    const val S3_OBJECT_GET_LIVE_CHANNEL_INFO = "s3:GetLiveChannel"

    /**
     * 获取指定LiveChannel的推流状态信息。
     */
    const val S3_OBJECT_GET_LIVE_CHANNEL_STAT = "s3:GetLiveChannelStat"

    /**
     * 获取指定LiveChannel的推流记录。
     */
    const val S3_OBJECT_GET_LIVE_CHANNEL_HISTORY = "s3:GetLiveChannelHistory"

    /**
     * 为指定的LiveChannel生成一个点播用的播放列表。
     */
    const val S3_OBJECT_POST_VOD_PLAYLIST = "s3:PostVodPlaylist"

    /**
     * 查看指定LiveChannel在指定时间段内推流生成的播放列表。
     */
    const val S3_OBJECT_GET_VOD_PLAYLIST = "s3:GetVodPlaylist"

    /**
     * 将音频和视频数据流推送到RTMP。
     */
    const val S3_OBJECT_PUBLISH_RTMP_STREAM = "s3:PublishRtmpStream"

    /**
     * 基于图片AI技术检测图片标签和置信度。
     */
    const val S3_OBJECT_PROCESS_IMM = "s3:ProcessImm"

    /**
     * 保存处理后的图片至指定Bucket。
     */
    const val S3_OBJECT_IMG_SAVE_AS = "s3:PostProcessTask"

    /**
     * 复制过程涉及的读权限。即允许S3读取源Bucket和目标Bucket中的数据与元数据，包括Object、Part、Multipart Upload等。
     */
    const val S3_OBJECT_REPLICATE_GET = "s3:ReplicateGet"

    /**
     * 复制过程涉及的写权限。即允许S3对目标Bucket复制相关的写入类操作，包括写入Object、Multipart
     * Upload、Part和Symlink，修改元数据信息等。
     */
    const val S3_OBJECT_REPLICATE_PUT = "s3:ReplicatePut"

    /**
     * 复制过程涉及的删除权限。即允许S3对目标Bucket复制相关的删除操作，包括DeleteObject、AbortMultipartUpload、DeleteMarker等。
     */
    const val S3_OBJECT_REPLICATE_DELETE = "s3:ReplicateDelete"

    @JvmField
    val S3_BUCKET_DEFAULT = setOf(
        S3_BUCKET_OBJECT_LIST, S3_OBJECT_GET, S3_OBJECT_PUT, S3_OBJECT_DELETE,
        S3_OBJECT_GET_ACL, S3_OBJECT_PUT_ACL, S3_OBJECT_GET_META, S3_OBJECT_GET_TAGGING, S3_OBJECT_PUT_TAGGING,
        S3_OBJECT_DELETE_TAGGING, S3_OBJECT_INITIATE_MULTIPART_UPLOAD, S3_OBJECT_COMPLETE_MULTIPART_UPLOAD,
        S3_OBJECT_ABORT_MULTIPART_UPLOAD,

        S3_BUCKET_LIST, S3_BUCKET_PUT_ACL, S3_BUCKET_GET_TAGS, S3_BUCKET_PUT_TAGS, S3_BUCKET_DELETE_TAGS,
        S3_BUCKET_LIST_MULTIPART_UPLOADS
    )

    @JvmField
    val S3_OBJECT_DEFAULT = S3_BUCKET_DEFAULT

    @JvmField
    val S3_OBJECT_DEFAULT_PUT = setOf(
        S3_OBJECT_PUT, S3_OBJECT_PUT_ACL,
        S3_OBJECT_GET_META, S3_OBJECT_PUT_TAGGING, S3_OBJECT_ABORT_MULTIPART_UPLOAD
    )

}
