package live.lingting.framework.huawei.properties;

import live.lingting.framework.time.DatePattern;
import lombok.Getter;
import lombok.Setter;

import java.time.ZoneOffset;
import java.util.Map;

/**
 * @author lingting 2024-09-12 21:31
 */
@Getter
@Setter
public class HuaweiIamProperties {

	private String host = "iam.myhuaweicloud.com";

	private Map<String, Object> domain;

	private String username;

	private String password;

	private ZoneOffset zone = DatePattern.DEFAULT_ZONE_OFFSET;

}
