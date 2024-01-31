package live.lingting.framework.security.grpc;

import live.lingting.framework.convert.SecurityGrpcConvert;
import live.lingting.framework.security.domain.AuthorizationVO;
import live.lingting.framework.security.domain.SecurityScope;
import live.lingting.framework.util.BooleanUtils;
import live.lingting.framework.util.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Map;

/**
 * @author lingting 2024-01-30 20:19
 */
public class SecurityGrpcExpandConvert extends SecurityGrpcConvert {

	@Override
	public ExpandSecurityScope scopeExpand(SecurityScope scope) {
		ExpandSecurityScope expand = ExpandMapstruct.INSTANCE.of(scope);
		Map<String, Object> attributes = scope.getAttributes();
		expand.setExpand(isExpand(attributes));
		return expand;
	}

	@Override
	public ExpandAuthorizationVO voExpand(AuthorizationVO vo) {
		ExpandAuthorizationVO expand = ExpandMapstruct.INSTANCE.of(vo);
		Map<String, Object> attributes = vo.getAttributes();
		expand.setExpand(isExpand(attributes));
		return expand;
	}

	boolean isExpand(Map<String, Object> attributes) {
		if (!CollectionUtils.isEmpty(attributes) && attributes.containsKey("expand")) {
			return BooleanUtils.isTrue(attributes.get("expand"));
		}
		return false;
	}

	@Mapper
	public interface ExpandMapstruct {

		ExpandMapstruct INSTANCE = Mappers.getMapper(ExpandMapstruct.class);

		ExpandAuthorizationVO of(AuthorizationVO vo);

		ExpandSecurityScope of(SecurityScope scope);

	}

}
