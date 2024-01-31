package live.lingting.framework.security.convert;

import live.lingting.framework.security.domain.AuthorizationVO;
import live.lingting.framework.security.domain.SecurityScope;

/**
 * @author lingting 2024-01-30 19:24
 */
public interface SecurityConvert {

	default SecurityScope scopeExpand(SecurityScope scope) {
		return scope;
	}

	default AuthorizationVO voExpand(AuthorizationVO vo) {
		return vo;
	}

	default AuthorizationVO scopeToVo(SecurityScope scope) {
		AuthorizationVO rawVO = SecurityMapstruct.INSTANCE.toVo(scope);
		return voExpand(rawVO);
	}

	default SecurityScope voToScope(AuthorizationVO vo) {
		SecurityScope rawScope = SecurityMapstruct.INSTANCE.ofVo(vo);
		return scopeExpand(rawScope);
	}

}
