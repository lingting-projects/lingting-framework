package live.lingting.framework.dingtalk

import com.fasterxml.jackson.annotation.JsonProperty
import live.lingting.framework.dingtalk.message.DingTalkActionCardMessage
import live.lingting.framework.jackson.JacksonUtils

/**
 * @author lingting 2020/6/12 19:35
 */
class DingTalkParams {
    @JsonProperty("msgtype")
    var type: String? = null
        private set

    var at: At? = null
        private set

    var actionCard: ActionCard? = null
        private set

    var link: Link? = null
        private set

    var markdown: Markdown? = null
        private set

    var text: Text? = null
        private set

    fun json(): String {
        return JacksonUtils.toJson(this)
    }

    @JsonProperty("msgtype")
    fun setType(type: String?): DingTalkParams {
        this.type = type
        return this
    }

    fun setAt(at: At?): DingTalkParams {
        this.at = at
        return this
    }

    fun setActionCard(actionCard: ActionCard?): DingTalkParams {
        this.actionCard = actionCard
        return this
    }

    fun setLink(link: Link?): DingTalkParams {
        this.link = link
        return this
    }

    fun setMarkdown(markdown: Markdown?): DingTalkParams {
        this.markdown = markdown
        return this
    }

    fun setText(text: Text?): DingTalkParams {
        this.text = text
        return this
    }


    class Text {
        var content: String? = null
            private set

        fun setContent(content: String?): Text {
            this.content = content
            return this
        }
    }


    class Markdown {
        var title: String? = null
            private set

        var text: String? = null
            private set

        fun setTitle(title: String?): Markdown {
            this.title = title
            return this
        }

        fun setText(text: String?): Markdown {
            this.text = text
            return this
        }
    }


    class Link {
        var text: String? = null
            private set

        var title: String? = null
            private set

        var picUrl: String? = null
            private set

        var messageUrl: String? = null
            private set

        fun setText(text: String?): Link {
            this.text = text
            return this
        }

        fun setTitle(title: String?): Link {
            this.title = title
            return this
        }

        fun setPicUrl(picUrl: String?): Link {
            this.picUrl = picUrl
            return this
        }

        fun setMessageUrl(messageUrl: String?): Link {
            this.messageUrl = messageUrl
            return this
        }
    }


    class ActionCard {
        var title: String? = null
            private set

        var text: String? = null
            private set

        var btnOrientation: String? = null
            private set

        var singleTitle: String? = null
            private set

        @JsonProperty("singleURL")
        var singleUrl: String? = null
            private set

        @JsonProperty("btns")
        var buttons: List<DingTalkActionCardMessage.Button>? = null
            private set

        fun setTitle(title: String?): ActionCard {
            this.title = title
            return this
        }

        fun setText(text: String?): ActionCard {
            this.text = text
            return this
        }

        fun setBtnOrientation(btnOrientation: String?): ActionCard {
            this.btnOrientation = btnOrientation
            return this
        }

        fun setSingleTitle(singleTitle: String?): ActionCard {
            this.singleTitle = singleTitle
            return this
        }

        @JsonProperty("singleURL")
        fun setSingleUrl(singleUrl: String?): ActionCard {
            this.singleUrl = singleUrl
            return this
        }

        @JsonProperty("btns")
        fun setButtons(buttons: List<DingTalkActionCardMessage.Button>?): ActionCard {
            this.buttons = buttons
            return this
        }
    }


    class At {
        @JsonProperty("isAtAll")
        var isAtAll: Boolean = false
            private set

        var atMobiles: Set<String>? = null
            private set

        @JsonProperty("isAtAll")
        fun setAtAll(atAll: Boolean): At {
            this.isAtAll = atAll
            return this
        }

        fun setAtMobiles(atMobiles: Set<String>?): At {
            this.atMobiles = atMobiles
            return this
        }
    }
}
