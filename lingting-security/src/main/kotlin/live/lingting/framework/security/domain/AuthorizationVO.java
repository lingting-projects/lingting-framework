package live.lingting.framework.security.domain;

import java.util.Set;

/**
 * @author lingting 2023-03-30 13:54
 */
public class AuthorizationVO {

	private String token;

	private String tenantId;

	private String userId;

	private String username;

	private String avatar;

	private String nickname;

	/**
	 * 是否启用
	 */
	private Boolean enabled;

	private Set<String> roles;

	private Set<String> permissions;

	private SecurityScopeAttributes attributes;

	public String getToken() {return this.token;}

	public String getTenantId() {return this.tenantId;}

	public String getUserId() {return this.userId;}

	public String getUsername() {return this.username;}

	public String getAvatar() {return this.avatar;}

	public String getNickname() {return this.nickname;}

	public Boolean getEnabled() {return this.enabled;}

	public Set<String> getRoles() {return this.roles;}

	public Set<String> getPermissions() {return this.permissions;}

	public SecurityScopeAttributes getAttributes() {return this.attributes;}

	public void setToken(String token) {this.token = token;}

	public void setTenantId(String tenantId) {this.tenantId = tenantId;}

	public void setUserId(String userId) {this.userId = userId;}

	public void setUsername(String username) {this.username = username;}

	public void setAvatar(String avatar) {this.avatar = avatar;}

	public void setNickname(String nickname) {this.nickname = nickname;}

	public void setEnabled(Boolean enabled) {this.enabled = enabled;}

	public void setRoles(Set<String> roles) {this.roles = roles;}

	public void setPermissions(Set<String> permissions) {this.permissions = permissions;}

	public void setAttributes(SecurityScopeAttributes attributes) {this.attributes = attributes;}
}
