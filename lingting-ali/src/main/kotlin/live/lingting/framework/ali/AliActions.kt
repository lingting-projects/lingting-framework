package live.lingting.framework.ali;

import java.util.List;

/**
 * @author lingting 2024-09-18 14:58
 */
public final class AliActions {

	/**
	 * 列举请求者拥有的所有Bucket。
	 */
	public static final String OSS_BUCKET_LIST = "oss:ListBuckets";

	/**
	 * 创建Bucket。
	 */
	public static final String OSS_BUCKET_PUT = "oss:PutBucket";

	/**
	 * 列举Bucket中所有Object的信息。
	 */
	public static final String OSS_BUCKET_LISTS = "oss:ListObjects";

	/**
	 * 查看Bucket相关信息。
	 */
	public static final String OSS_BUCKET_GET_INFO = "oss:GetBucketInfo";

	/**
	 * 查看Bucket位置信息。
	 */
	public static final String OSS_BUCKET_GET_LOCATION = "oss:GetBucketLocation";

	/**
	 * 设置指定Bucket的版本控制状态。
	 */
	public static final String OSS_BUCKET_PUT_VERSIONING = "oss:PutBucketVersioning";

	/**
	 * 获取指定Bucket的版本控制状态。
	 */
	public static final String OSS_BUCKET_GET_VERSIONING = "oss:GetBucketVersioning";

	/**
	 * 列出Bucket中包括删除标记（Delete Marker）在内的所有Object的版本信息。
	 */
	public static final String OSS_BUCKET_LIST_VERSIONS = "oss:ListObjectVersions";

	/**
	 * 设置或修改Bucket ACL。
	 */
	public static final String OSS_BUCKET_PUT_ACL = "oss:PutBucketAcl";

	/**
	 * 获取Bucket ACL。
	 */
	public static final String OSS_BUCKET_GET_ACL = "oss:GetBucketAcl";

	/**
	 * 删除某个Bucket。
	 */
	public static final String OSS_BUCKET_DELETE = "oss:DeleteBucket";

	/**
	 * 新建合规保留策略。
	 */
	public static final String OSS_BUCKET_INITIATE_WORM = "oss:InitiateBucketWorm";

	/**
	 * 删除未锁定的合规保留策略。
	 */
	public static final String OSS_BUCKET_ABORT_WORM = "oss:AbortBucketWorm";

	/**
	 * 锁定合规保留策略。
	 */
	public static final String OSS_BUCKET_COMPLETE_WORM = "oss:CompleteBucketWorm";

	/**
	 * 延长已锁定的合规保留策略对应Bucket中Object的保留天数。
	 */
	public static final String OSS_BUCKET_EXTEND_WORM = "oss:ExtendBucketWorm";

	/**
	 * 获取合规保留策略信息。
	 */
	public static final String OSS_BUCKET_GET_WORM = "oss:GetBucketWorm";

	/**
	 * 开启Bucket日志转存功能。
	 */
	public static final String OSS_BUCKET_PUT_LOGGING = "oss:PutBucketLogging";

	/**
	 * 查看Bucket日志转存配置。
	 */
	public static final String OSS_BUCKET_GET_LOGGING = "oss:GetBucketLogging";

	/**
	 * 关闭Bucket日志转存功能。
	 */
	public static final String OSS_BUCKET_DELETE_LOGGING = "oss:DeleteBucketLogging";

	/**
	 * 设置Bucket为静态网站托管模式并设置其跳转规则（RoutingRule）。
	 */
	public static final String OSS_BUCKET_PUT_WEBSITE = "oss:PutBucketWebsite";

	/**
	 * 查看Bucket的静态网站托管状态以及跳转规则。
	 */
	public static final String OSS_BUCKET_GET_WEBSITE = "oss:GetBucketWebsite";

	/**
	 * 关闭Bucket的静态网站托管模式以及跳转规则。
	 */
	public static final String OSS_BUCKET_DELETE_WEBSITE = "oss:DeleteBucketWebsite";

	/**
	 * 设置Bucket的防盗链。
	 */
	public static final String OSS_BUCKET_PUT_REFERER = "oss:PutBucketReferer";

	/**
	 * 查看Bucket的防盗链（Referer）相关配置。
	 */
	public static final String OSS_BUCKET_GET_REFERER = "oss:GetBucketReferer";

	/**
	 * 设置Bucket的生命周期规则。
	 */
	public static final String OSS_BUCKET_PUT_LIFECYCLE = "oss:PutBucketLifecycle";

	/**
	 * 查看Bucket的生命周期规则。
	 */
	public static final String OSS_BUCKET_GET_LIFECYCLE = "oss:GetBucketLifecycle";

	/**
	 * 删除Bucket的生命周期规则。
	 */
	public static final String OSS_BUCKET_DELETE_LIFECYCLE = "oss:DeleteBucketLifecycle";

	/**
	 * 设置Bucket传输加速。
	 */
	public static final String OSS_BUCKET_PUT_TRANSFER_ACCELERATION = "oss:PutBucketTransferAcceleration";

	/**
	 * 查看Bucket的传输加速配置。
	 */
	public static final String OSS_BUCKET_GET_TRANSFER_ACCELERATION = "oss:GetBucketTransferAcceleration";

	/**
	 * 列举所有执行中的Multipart Upload事件，即已经初始化但还未完成（Complete）或者还未中止（Abort）的Multipart Upload事件。
	 */
	public static final String OSS_BUCKET_LIST_MULTIPART_UPLOADS = "oss:ListMultipartUploads";

	/**
	 * 设置指定Bucket的跨域资源共享CORS（Cross-Origin Resource Sharing）规则。
	 */
	public static final String OSS_BUCKET_PUT_CORS = "oss:PutBucketCors";

	/**
	 * 获取指定Bucket当前的跨域资源共享CORS规则。
	 */
	public static final String OSS_BUCKET_GET_CORS = "oss:GetBucketCors";

	/**
	 * 关闭指定Bucket对应的跨域资源共享CORS功能并清空所有规则。
	 */
	public static final String OSS_BUCKET_DELETE_CORS = "oss:DeleteBucketCors";

	/**
	 * 设置指定Bucket的授权策略。
	 */
	public static final String OSS_BUCKET_PUT_POLICY = "oss:PutBucketPolicy";

	/**
	 * 获取指定Bucket的授权策略。
	 */
	public static final String OSS_BUCKET_GET_POLICY = "oss:GetBucketPolicy";

	/**
	 * 删除指定Bucket的授权策略。
	 */
	public static final String OSS_BUCKET_DELETE_POLICY = "oss:DeleteBucketPolicy";

	/**
	 * 添加或修改指定Bucket的标签。
	 */
	public static final String OSS_BUCKET_PUT_TAGS = "oss:PutBucketTagging";

	/**
	 * 获取Bucket的标签。
	 */
	public static final String OSS_BUCKET_GET_TAGS = "oss:GetBucketTagging";

	/**
	 * 删除Bucket的标签。
	 */
	public static final String OSS_BUCKET_DELETE_TAGS = "oss:DeleteBucketTagging";

	/**
	 * 配置Bucket的加密规则。
	 */
	public static final String OSS_BUCKET_PUT_ENCRYPTION = "oss:PutBucketEncryption";

	/**
	 * 获取Bucket的加密规则。
	 */
	public static final String OSS_BUCKET_GET_ENCRYPTION = "oss:GetBucketEncryption";

	/**
	 * 删除Bucket的加密规则。
	 */
	public static final String OSS_BUCKET_DELETE_ENCRYPTION = "oss:DeleteBucketEncryption";

	/**
	 * 设置请求者付费模式。
	 */
	public static final String OSS_BUCKET_PUT_REQUEST_PAYMENT = "oss:PutBucketRequestPayment";

	/**
	 * 获取请求者付费模式配置信息。
	 */
	public static final String OSS_BUCKET_GET_REQUEST_PAYMENT = "oss:GetBucketRequestPayment";

	/**
	 * 设置Bucket的数据复制规则。
	 */
	public static final String OSS_BUCKET_PUT_REPLICATION = "oss:PutBucketReplication";

	/**
	 * 为已有的跨区域复制规则开启或关闭数据复制时间控制（RTC）功能。
	 */
	public static final String OSS_BUCKET_PUT_R_T_C = "oss:PutBucketRTC";

	/**
	 * 获取Bucket已设置的数据复制规则。
	 */
	public static final String OSS_BUCKET_GET_REPLICATION = "oss:GetBucketReplication";

	/**
	 * 停止Bucket的数据复制并删除Bucket的复制配置。
	 */
	public static final String OSS_BUCKET_DELETE_REPLICATION = "oss:DeleteBucketReplication";

	/**
	 * 获取可复制到的目标Bucket的所在地域。
	 */
	public static final String OSS_BUCKET_GET_REPLICATION_LOCATION = "oss:GetBucketReplicationLocation";

	/**
	 * 获取Bucket的数据复制进度。
	 */
	public static final String OSS_BUCKET_GET_REPLICATION_PROGRESS = "oss:GetBucketReplicationProgress";

	/**
	 * 配置Bucket的清单（Inventory）规则。
	 */
	public static final String OSS_BUCKET_PUT_INVENTORY = "oss:PutBucketInventory";

	/**
	 * 查看Bucket中指定的清单任务。
	 */
	public static final String OSS_BUCKET_GET_INVENTORY = "oss:GetBucketInventory";

	/**
	 * 批量获取Bucket中所有清单任务。
	 */
	public static final String OSS_BUCKET_LIST_INVENTORY = "oss:GetBucketInventory";

	/**
	 * 删除Bucket中指定的清单任务。
	 */
	public static final String OSS_BUCKET_DELETE_INVENTORY = "oss:DeleteBucketInventory";

	/**
	 * 配置Bucket的访问跟踪状态。
	 */
	public static final String OSS_BUCKET_PUT_ACCESS_MONITOR = "oss:PutBucketAccessMonitor";

	/**
	 * 获取Bucket的访问跟踪状态。
	 */
	public static final String OSS_BUCKET_GET_ACCESS_MONITOR = "oss:GetBucketAccessMonitor";

	/**
	 * 开启Bucket的元数据管理功能。
	 */
	public static final String OSS_BUCKET_OPEN_META_QUERY = "oss:OpenMetaQuery";

	/**
	 * 获取Bucket的元数据索引库信息。
	 */
	public static final String OSS_BUCKET_GET_META_QUERY_STATUS = "oss:GetMetaQueryStatus";

	/**
	 * 查询满足指定条件的Object，并按照指定字段和排序方式列出Object信息。
	 */
	public static final String OSS_BUCKET_DO_META_QUERY = "oss:DoMetaQuery";

	/**
	 * 关闭Bucket的元数据管理功能.
	 */
	public static final String OSS_BUCKET_CLOSE_META_QUERY = "oss:CloseMetaQuery";

	/**
	 * 创建高防OSS实例。
	 */
	public static final String OSS_BUCKET_INIT_USER_ANTI_D_DOS_INFO = "oss:InitUserAntiDDosInfo";

	/**
	 * 更改高防OSS实例状态。
	 */
	public static final String OSS_BUCKET_UPDATE_USER_ANTI_D_DOS_INFO = "oss:UpdateUserAntiDDosInfo";

	/**
	 * 查询指定账号下的高防OSS实例信息。
	 */
	public static final String OSS_BUCKET_GET_USER_ANTI_D_DOS_INFO = "oss:GetUserAntiDDosInfo";

	/**
	 * 初始化Bucket防护。
	 */
	public static final String OSS_BUCKET_INIT_ANTI_D_DOS_INFO = "oss:InitBucketAntiDDosInfo";

	/**
	 * 更新Bucket防护状态。
	 */
	public static final String OSS_BUCKET_UPDATE_ANTI_D_DOS_INFO = "oss:UpdateBucketAntiDDosInfo";

	/**
	 * 获取Bucket防护信息列表。
	 */
	public static final String OSS_BUCKET_LIST_ANTI_D_DOS_INFO = "oss:ListBucketAntiDDosInfo";

	/**
	 * 设置Bucket所属资源组。
	 */
	public static final String OSS_BUCKET_PUT_RESOURCE_GROUP = "oss:PutBucketResourceGroup";

	/**
	 * 查询Bucket所属资源组ID。
	 */
	public static final String OSS_BUCKET_GET_RESOURCE_GROUP = "oss:GetBucketResourceGroup";

	/**
	 * 创建域名所有权验证所需的CnameToken。
	 */
	public static final String OSS_BUCKET_CREATE_CNAME_TOKEN = "oss:CreateCnameToken";

	/**
	 * 获取已创建的CnameToken。
	 */
	public static final String OSS_BUCKET_GET_CNAME_TOKEN = "oss:GetCnameToken";

	/**
	 * 为Bucket绑定自定义域名。
	 */
	public static final String OSS_BUCKET_PUT_CNAME = "oss:PutCname";

	/**
	 * 获取Bucket下绑定的所有的自定义域名（Cname）列表。
	 */
	public static final String OSS_BUCKET_LIST_CNAME = "oss:ListCname";

	/**
	 * 删除Bucket已绑定的Cname。
	 */
	public static final String OSS_BUCKET_DELETE_CNAME = "oss:DeleteCname";

	/**
	 * 设置图片样式。
	 */
	public static final String OSS_BUCKET_PUT_STYLE = "oss:PutStyle";

	/**
	 * 获取图片样式。
	 */
	public static final String OSS_BUCKET_GET_STYLE = "oss:GetStyle";

	/**
	 * 列举图片样式。
	 */
	public static final String OSS_BUCKET_LIST_STYLE = "oss:ListStyle";

	/**
	 * 删除图片样式。
	 */
	public static final String OSS_BUCKET_DELETE_STYLE = "oss:DeleteStyle";

	/**
	 * 为Bucket开启或关闭归档直读。
	 */
	public static final String OSS_BUCKET_PUT_ARCHIVE_DIRECT_READ = "oss:PutBucketArchiveDirectRead";

	/**
	 * 查看Bucket是否开启归档直读。
	 */
	public static final String OSS_BUCKET_GET_ARCHIVE_DIRECT_READ = "oss:GetBucketArchiveDirectRead";

	/**
	 * 创建接入点。
	 */
	public static final String OSS_BUCKET_CREATE_ACCESS_POINT = "oss:CreateAccessPoint";

	/**
	 * 获取单个接入点信息。
	 */
	public static final String OSS_BUCKET_GET_ACCESS_POINT = "oss:GetAccessPoint";

	/**
	 * 删除接入点。
	 */
	public static final String OSS_BUCKET_DELETE_ACCESS_POINT = "oss:DeleteAccessPoint";

	/**
	 * 获取用户级别及Bucket级别的接入点信息。
	 */
	public static final String OSS_BUCKET_LIST_ACCESS_POINTS = "oss:ListAccessPoints";

	/**
	 * 配置接入点策略。
	 */
	public static final String OSS_BUCKET_PUT_ACCESS_POINT_POLICY = "oss:PutAccessPointPolicy";

	/**
	 * 获取接入点策略信息。
	 */
	public static final String OSS_BUCKET_GET_ACCESS_POINT_POLICY = "oss:GetAccessPointPolicy";

	/**
	 * 删除接入点策略。
	 */
	public static final String OSS_BUCKET_DELETE_ACCESS_POINT_POLICY = "oss:DeleteAccessPointPolicy";

	/**
	 * 为Bucket开启或关闭TLS版本设置。
	 */
	public static final String OSS_BUCKET_PUT_HTTPS_CONFIG = "oss:PutBucketHttpsConfig";

	/**
	 * 查看Bucket的TLS版本设置。
	 */
	public static final String OSS_BUCKET_GET_HTTPS_CONFIG = "oss:GetBucketHttpsConfig";

	/**
	 * 复制过程涉及的列举权限。即允许OSS先列举源Bucket的历史数据，再逐一对历史数据进行复制。
	 */
	public static final String OSS_BUCKET_REPLICATE_LIST = "oss:ReplicateList";

	/**
	 * 创建对象FC接入点。
	 */
	public static final String OSS_BUCKET_CREATE_ACCESS_POINT_FOR_PROCESS = "oss:CreateAccessPointForObjectProcess";

	/**
	 * 获取对象FC接入点基础信息。
	 */
	public static final String OSS_BUCKET_GET_ACCESS_POINT_FOR_PROCESS = "oss:GetAccessPointForObjectProcess";

	/**
	 * 删除对象FC接入点。
	 */
	public static final String OSS_BUCKET_DELETE_ACCESS_POINT_FOR_PROCESS = "oss:DeleteAccessPointForObjectProcess";

	/**
	 * 获取用户级别的对象FC接入点信息。
	 */
	public static final String OSS_BUCKET_LIST_ACCESS_POINTS_FOR_PROCESS = "oss:ListAccessPointsForObjectProcess";

	/**
	 * 修改对象FC接入点配置。
	 */
	public static final String OSS_BUCKET_PUT_ACCESS_POINT_CONFIG_FOR_PROCESS = "oss:PutAccessPointConfigForObjectProcess";

	/**
	 * 获取对象FC接入点配置信息。
	 */
	public static final String OSS_BUCKET_GET_ACCESS_POINT_CONFIG_FOR_PROCESS = "oss:GetAccessPointConfigForObjectProcess";

	/**
	 * 为对象FC接入点配置权限策略。
	 */
	public static final String OSS_BUCKET_PUT_ACCESS_POINT_POLICY_FOR_PROCESS = "oss:PutAccessPointPolicyForObjectProcess";

	/**
	 * 获取对象FC接入点的权限策略配置。
	 */
	public static final String OSS_BUCKET_GET_ACCESS_POINT_POLICY_FOR_PROCESS = "oss:GetAccessPointPolicyForObjectProcess";

	/**
	 * 删除对象FC接入点的权限策略。
	 */
	public static final String OSS_BUCKET_DELETE_ACCESS_POINT_POLICY_FOR_PROCESS = "oss:DeleteAccessPointPolicyForObjectProcess";

	/**
	 * 自定义返回数据和响应标头。
	 */
	public static final String OSS_BUCKET_WRITE_GET_RESPONSE = "oss:WriteGetObjectResponse";

	/**
	 * 创建存储冗余转换任务。
	 */
	public static final String OSS_BUCKET_CREATE_DATA_REDUNDANCY_TRANSITION = "oss:CreateBucketDataRedundancyTransition";

	/**
	 * 获取存储冗余转换任务。
	 */
	public static final String OSS_BUCKET_GET_DATA_REDUNDANCY_TRANSITION = "oss:GetBucketDataRedundancyTransition";

	/**
	 * 删除存储冗余转换任务。
	 */
	public static final String OSS_BUCKET_DELETE_DATA_REDUNDANCY_TRANSITION = "oss:DeleteBucketDataRedundancyTransition";

	/**
	 * 列举某个Bucket下所有的存储冗余转换任务。
	 */
	public static final String OSS_BUCKET_LIST_DATA_REDUNDANCY_TRANSITION = "oss:ListBucketDataRedundancyTransition";

	/**
	 * 为某个Bucket开启阻止公共访问。
	 */
	public static final String OSS_BUCKET_PUT_PUBLIC_ACCESS_BLOCK = "oss:PutBucketPublicAccessBlock";

	/**
	 * 获取某个Bucket的阻止公共访问配置信息。
	 */
	public static final String OSS_BUCKET_GET_PUBLIC_ACCESS_BLOCK = "oss:GetBucketPublicAccessBlock";

	/**
	 * 删除某个Bucket的阻止公共访问配置信息。
	 */
	public static final String OSS_BUCKET_DELETE_PUBLIC_ACCESS_BLOCK = "oss:DeleteBucketPublicAccessBlock";

	/**
	 * 为某个接入点开启阻止公共访问。
	 */
	public static final String OSS_BUCKET_PUT_ACCESS_POINT_PUBLIC_ACCESS_BLOCK = "oss:PutAccessPointPublicAccessBlock";

	/**
	 * 获取某个接入点的阻止公共访问配置信息。
	 */
	public static final String OSS_BUCKET_GET_ACCESS_POINT_PUBLIC_ACCESS_BLOCK = "oss:GetAccessPointPublicAccessBlock";

	/**
	 * 删除某个接入点的阻止公共访问配置信息。
	 */
	public static final String OSS_BUCKET_DELETE_ACCESS_POINT_PUBLIC_ACCESS_BLOCK = "oss:DeleteAccessPointPublicAccessBlock";

	/**
	 * 查看当前Bucket Policy是否允许公共访问。
	 */
	public static final String OSS_BUCKET_GET_POLICY_STATUS = "oss:GetBucketPolicyStatus";

	/**
	 * 上传文件（Object）。
	 */
	public static final String OSS_OBJECT_PUT = "oss:PutObject";

	/**
	 * 通过HTML表单上传的方式将Object上传到指定Bucket。
	 */
	public static final String OSS_OBJECT_POST = "oss:PutObject";

	/**
	 * 以追加写的方式上传Object。
	 */
	public static final String OSS_OBJECT_APPEND = "oss:PutObject";

	/**
	 * 在使用Multipart Upload模式传输数据前，通知OSS初始化一个分片上传（Multipart Upload）事件。
	 */
	public static final String OSS_OBJECT_INITIATE_MULTIPART_UPLOAD = "oss:PutObject";

	/**
	 * 根据指定的Object名和uploadId来分块（Part）上传数据
	 */
	public static final String OSS_OBJECT_UPLOAD_PART = "oss:PutObject";

	/**
	 * 在将所有数据Part都上传完成后，需调用此接口来完成整个Object的分片上传。
	 */
	public static final String OSS_OBJECT_COMPLETE_MULTIPART_UPLOAD = "oss:PutObject";

	/**
	 * 取消MultipartUpload事件并删除对应的Part数据。
	 */
	public static final String OSS_OBJECT_ABORT_MULTIPART_UPLOAD = "oss:AbortMultipartUpload";

	/**
	 * 为OSS的目标文件（TargetObject）创建软链接（Symlink）。
	 */
	public static final String OSS_OBJECT_PUT_SYMLINK = "oss:PutObject";

	/**
	 * 获取某个Object。
	 */
	public static final String OSS_OBJECT_GET = "oss:GetObject";

	/**
	 * 获取某个Object的元数据。
	 */
	public static final String OSS_OBJECT_HEAD = "oss:GetObject";

	/**
	 * 获取Object的元数据信息，包括该Object的ETag、Size、LastModified信息。
	 */
	public static final String OSS_OBJECT_GET_META = "oss:GetObject";

	/**
	 * 对目标文件执行SQL语句，返回执行结果。
	 */
	public static final String OSS_OBJECT_SELECT = "oss:GetObject";

	/**
	 * 获取目标文件的软链接。
	 */
	public static final String OSS_OBJECT_GET_SYMLINK = "oss:GetObject";

	/**
	 * 删除某个Object。
	 */
	public static final String OSS_OBJECT_DELETE = "oss:DeleteObject";

	/**
	 * 拷贝同一地域下相同或不同Bucket之间的Object。
	 */
	public static final String OSS_OBJECT_COPY = "oss:GetObject=oss:PutObject";

	/**
	 * 在UploadPart请求的基础上增加一个请求头x-oss-copy-source来调用UploadPartCopy接口，实现从一个已存在的Object中拷贝数据来上传一个Part。
	 */
	public static final String OSS_OBJECT_UPLOAD_PART_COPY = "oss:GetObject=oss:PutObject";

	/**
	 * 列举指定Upload ID所属的所有已经上传成功的Part。
	 */
	public static final String OSS_OBJECT_LIST_PARTS = "oss:ListParts";

	/**
	 * 修改Bucket下某个Object的ACL。
	 */
	public static final String OSS_OBJECT_PUT_ACL = "oss:PutObjectAcl";

	/**
	 * 获取Bucket下某个Object的ACL。
	 */
	public static final String OSS_OBJECT_GET_ACL = "oss:GetObjectAcl";

	/**
	 * 解冻归档存储、冷归档存储或者深度冷归档存储类型的Object。
	 */
	public static final String OSS_OBJECT_RESTORE = "oss:RestoreObject";

	/**
	 * 设置或更新Object的标签（Tagging）信息。
	 */
	public static final String OSS_OBJECT_PUT_TAGGING = "oss:PutObjectTagging";

	/**
	 * 获取Object的标签信息。
	 */
	public static final String OSS_OBJECT_GET_TAGGING = "oss:GetObjectTagging";

	/**
	 * 删除指定Object的标签信息。
	 */
	public static final String OSS_OBJECT_DELETE_TAGGING = "oss:DeleteObjectTagging";

	/**
	 * 下载指定版本Object。
	 */
	public static final String OSS_OBJECT_GET_VERSION = "oss:GetObjectVersion";

	/**
	 * 修改Bucket下指定版本Object的ACL。
	 */
	public static final String OSS_OBJECT_PUT_VERSION_ACL = "oss:PutObjectVersionAcl";

	/**
	 * 获取Bucket下指定版本Object的ACL。
	 */
	public static final String OSS_OBJECT_GET_VERSION_ACL = "oss:GetObjectVersionAcl";

	/**
	 * 解冻指定版本的归档存储、冷归档存储或者深度冷归档存储类型的Object。
	 */
	public static final String OSS_OBJECT_RESTORE_VERSION = "oss:RestoreObjectVersion";

	/**
	 * 删除指定版本Object。
	 */
	public static final String OSS_OBJECT_DELETE_VERSION = "oss:DeleteObjectVersion";

	/**
	 * 设置或更新指定版本Object的标签（Tagging）信息。
	 */
	public static final String OSS_OBJECT_PUT_VERSION_TAGGING = "oss:PutObjectVersionTagging";

	/**
	 * 获取指定版本Object的标签信息。
	 */
	public static final String OSS_OBJECT_GET_VERSION_TAGGING = "oss:GetObjectVersionTagging";

	/**
	 * 删除指定版本Object的标签信息。
	 */
	public static final String OSS_OBJECT_DELETE_VERSION_TAGGING = "oss:DeleteObjectVersionTagging";

	/**
	 * 通过RTMP协议上传音视频数据前，必须先调用该接口创建一个LiveChannel。
	 */
	public static final String OSS_OBJECT_PUT_LIVE_CHANNEL = "oss:PutLiveChannel";

	/**
	 * 列举指定的LiveChannel。
	 */
	public static final String OSS_OBJECT_LIST_LIVE_CHANNEL = "oss:ListLiveChannel";

	/**
	 * 删除指定的LiveChannel。
	 */
	public static final String OSS_OBJECT_DELETE_LIVE_CHANNEL = "oss:DeleteLiveChannel";

	/**
	 * 在启用（enabled）和禁用（disabled）两种状态之间进行切换。
	 */
	public static final String OSS_OBJECT_PUT_LIVE_CHANNEL_STATUS = "oss:PutLiveChannelStatus";

	/**
	 * 获取指定LiveChannel的配置信息。
	 */
	public static final String OSS_OBJECT_GET_LIVE_CHANNEL_INFO = "oss:GetLiveChannel";

	/**
	 * 获取指定LiveChannel的推流状态信息。
	 */
	public static final String OSS_OBJECT_GET_LIVE_CHANNEL_STAT = "oss:GetLiveChannelStat";

	/**
	 * 获取指定LiveChannel的推流记录。
	 */
	public static final String OSS_OBJECT_GET_LIVE_CHANNEL_HISTORY = "oss:GetLiveChannelHistory";

	/**
	 * 为指定的LiveChannel生成一个点播用的播放列表。
	 */
	public static final String OSS_OBJECT_POST_VOD_PLAYLIST = "oss:PostVodPlaylist";

	/**
	 * 查看指定LiveChannel在指定时间段内推流生成的播放列表。
	 */
	public static final String OSS_OBJECT_GET_VOD_PLAYLIST = "oss:GetVodPlaylist";

	/**
	 * 将音频和视频数据流推送到RTMP。
	 */
	public static final String OSS_OBJECT_PUBLISH_RTMP_STREAM = "oss:PublishRtmpStream";

	/**
	 * 基于图片AI技术检测图片标签和置信度。
	 */
	public static final String OSS_OBJECT_PROCESS_IMM = "oss:ProcessImm";

	/**
	 * 保存处理后的图片至指定Bucket。
	 */
	public static final String OSS_OBJECT_IMG_SAVE_AS = "oss:PostProcessTask";

	/**
	 * 复制过程涉及的读权限。即允许OSS读取源Bucket和目标Bucket中的数据与元数据，包括Object、Part、Multipart Upload等。
	 */
	public static final String OSS_OBJECT_REPLICATE_GET = "oss:ReplicateGet";

	/**
	 * 复制过程涉及的写权限。即允许OSS对目标Bucket复制相关的写入类操作，包括写入Object、Multipart
	 * Upload、Part和Symlink，修改元数据信息等。
	 */
	public static final String OSS_OBJECT_REPLICATE_PUT = "oss:ReplicatePut";

	/**
	 * 复制过程涉及的删除权限。即允许OSS对目标Bucket复制相关的删除操作，包括DeleteObject、AbortMultipartUpload、DeleteMarker等。
	 */
	public static final String OSS_OBJECT_REPLICATE_DELETE = "oss:ReplicateDelete";

	public static final List<String> OSS_BUCKET_DEFAULT = List.of(OSS_OBJECT_GET, OSS_OBJECT_PUT, OSS_OBJECT_DELETE,
		OSS_OBJECT_GET_ACL, OSS_OBJECT_PUT_ACL, OSS_OBJECT_GET_META, OSS_OBJECT_GET_TAGGING, OSS_OBJECT_PUT_TAGGING,
		OSS_OBJECT_DELETE_TAGGING, OSS_OBJECT_INITIATE_MULTIPART_UPLOAD, OSS_OBJECT_COMPLETE_MULTIPART_UPLOAD,
		OSS_OBJECT_ABORT_MULTIPART_UPLOAD,

		OSS_BUCKET_LIST, OSS_BUCKET_PUT_ACL, OSS_BUCKET_GET_TAGS, OSS_BUCKET_PUT_TAGS, OSS_BUCKET_DELETE_TAGS,
		OSS_BUCKET_LIST_MULTIPART_UPLOADS);

	public static final List<String> OSS_OBJECT_DEFAULT = OSS_BUCKET_DEFAULT;

	public static final List<String> OSS_OBJECT_DEFAULT_PUT = List.of(OSS_OBJECT_PUT, OSS_OBJECT_PUT_ACL,
		OSS_OBJECT_GET_META, OSS_OBJECT_PUT_TAGGING, OSS_OBJECT_ABORT_MULTIPART_UPLOAD);

	private AliActions() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}
}
