package live.lingting.framework.security.convert;

import live.lingting.framework.security.domain.AuthorizationVO;
import live.lingting.framework.security.domain.SecurityScope;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author lingting 2023-03-30 13:55
 */
@Mapper
public interface SecurityMapstruct {

	SecurityMapstruct INSTANCE = Mappers.getMapper(SecurityMapstruct.class);

	AuthorizationVO toVo(SecurityScope scope);

	SecurityScope ofVo(AuthorizationVO vo);

}
