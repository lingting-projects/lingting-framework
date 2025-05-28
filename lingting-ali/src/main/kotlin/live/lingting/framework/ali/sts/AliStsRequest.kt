package live.lingting.framework.ali.sts

import live.lingting.framework.ali.AliRequest

/**
 * @author lingting 2025/5/28 11:27
 */
abstract class AliStsRequest : AliRequest() {

    override fun version(): String = "2015-04-01"

}
