package live.lingting.framework.huawei.properties;

import live.lingting.framework.time.DatePattern;

import java.time.ZoneOffset;
import java.util.Map;

/**
 * @author lingting 2024-09-12 21:31
 */
public class HuaweiIamProperties {

	private String host = "iam.myhuaweicloud.com";

	private Map<String, Object> domain;

	private String username;

	private String password;

	private ZoneOffset zone = DatePattern.DEFAULT_ZONE_OFFSET;

	public String getHost() {return this.host;}

	public Map<String, Object> getDomain() {return this.domain;}

	public String getUsername() {return this.username;}

	public String getPassword() {return this.password;}

	public ZoneOffset getZone() {return this.zone;}

	public void setHost(String host) {this.host = host;}

	public void setDomain(Map<String, Object> domain) {this.domain = domain;}

	public void setUsername(String username) {this.username = username;}

	public void setPassword(String password) {this.password = password;}

	public void setZone(ZoneOffset zone) {this.zone = zone;}
}
