package live.lingting.framework.ali.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lingting 2024-09-14 11:53
 */
@Getter
@Setter
public class AliStsProperties extends AliProperties {

	public AliStsProperties() {
		setPrefix("sts");
	}

	private String roleArn;

	private String roleSessionName;

}
