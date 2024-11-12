package live.lingting.framework.security.convert

import live.lingting.framework.security.domain.AuthorizationVO
import live.lingting.framework.security.domain.SecurityScope
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

/**
 * @author lingting 2023-03-30 13:55
 */
@Mapper
interface SecurityMapstruct {
    fun toVo(scope: SecurityScope?): AuthorizationVO

    fun ofVo(vo: AuthorizationVO?): SecurityScope

    companion object {
        val INSTANCE: SecurityMapstruct = Mappers.getMapper(SecurityMapstruct::class.java)
    }
}
