package live.lingting.framework.dingtalk

import com.fasterxml.jackson.annotation.JsonProperty
import live.lingting.framework.dingtalk.message.DingTalkActionCardMessage
import live.lingting.framework.jackson.JacksonUtils

/**
 * @author lingting 2020/6/12 19:35
 */
class DingTalkParams {
    @JsonProperty("msgtype")
    var type: String = ""

    var at: At = At()

    var actionCard: ActionCard? = null

    var link: Link? = null

    var markdown: Markdown? = null

    var text: Text? = null

    fun atAll() {
        at.atAll = true
    }

    fun atPhones(phones: Collection<String>) {
        at.atMobiles = phones.toSet()
    }

    fun json(): String {
        return JacksonUtils.toJson(this)
    }

    data class Text(val content: String?)

    data class Markdown(val title: String?, val text: String?)

    class Link {
        var text: String? = null

        var title: String? = null

        var picUrl: String? = null

        var messageUrl: String? = null

    }

    class ActionCard {
        var title: String? = null

        var text: String? = null

        var btnOrientation: String? = null

        var singleTitle: String? = null

        @JsonProperty("singleURL")
        var singleUrl: String? = null

        @JsonProperty("btns")
        var buttons: List<DingTalkActionCardMessage.Button>? = null
    }

    class At {
        @JsonProperty("isAtAll")
        var atAll: Boolean = false

        var atMobiles: Set<String> = emptySet()

    }
}
