package live.lingting.framework.ali.properties;

/**
 * @author lingting 2024-09-14 11:53
 */
public class AliStsProperties extends AliProperties {

	public AliStsProperties() {
		setPrefix("sts");
	}

	private String roleArn;

	private String roleSessionName;

	public String getRoleArn() {return this.roleArn;}

	public String getRoleSessionName() {return this.roleSessionName;}

	public void setRoleArn(String roleArn) {this.roleArn = roleArn;}

	public void setRoleSessionName(String roleSessionName) {this.roleSessionName = roleSessionName;}
}
