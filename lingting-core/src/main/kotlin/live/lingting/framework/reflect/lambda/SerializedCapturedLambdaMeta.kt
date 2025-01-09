package live.lingting.framework.reflect.lambda

import java.lang.invoke.SerializedLambda

/**
 * @author lingting 2025/1/9 11:07
 */
class SerializedCapturedLambdaMeta(
    source: Any,
    val lambda: SerializedLambda
) : CapturedLambdaMeta(source, { lambda.getCapturedArg(0) }) {

}
