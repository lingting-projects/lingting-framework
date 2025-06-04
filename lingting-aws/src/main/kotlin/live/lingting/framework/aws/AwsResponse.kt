package live.lingting.framework.aws

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

/**
 * @author lingting 2025/6/3 17:45
 */
@JacksonXmlRootElement(localName = "AssumeRoleResponse", namespace = "https://sts.amazonaws.com/doc/2011-06-15/")
abstract class AwsResponse {

    @JacksonXmlProperty(localName = "ResponseMetadata")
    var metadata: Metadata = Metadata()

    class Metadata {

        @JacksonXmlProperty(localName = "RequestId")
        var requestId = ""

    }

}
