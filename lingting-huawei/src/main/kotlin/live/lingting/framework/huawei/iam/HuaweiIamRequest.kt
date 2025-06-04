package live.lingting.framework.huawei.iam

import live.lingting.framework.huawei.HuaweiRequest

/**
 * @author lingting 2024-09-14 15:06
 */
abstract class HuaweiIamRequest : HuaweiRequest() {

    open fun usingToken(): Boolean {
        return true
    }

}
