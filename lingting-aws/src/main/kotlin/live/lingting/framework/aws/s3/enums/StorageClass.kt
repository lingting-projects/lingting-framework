package live.lingting.framework.aws.s3.enums

import com.fasterxml.jackson.annotation.JsonCreator
import live.lingting.framework.aws.s3.enums.StorageClass.entries

/**
 * @author lingting 2025/1/15 10:16
 */
enum class StorageClass {

    STANDARD,

    WARM,

    COLD,

    DEEP_ARCHIVE,

    ;

    companion object {

        @JvmStatic
        @JsonCreator
        fun of(value: String?): StorageClass? {
            return entries.firstOrNull {
                if (value.isNullOrBlank()) {
                    false
                } else {
                    it.name == value || it.name.lowercase() == value.lowercase()
                }
            }
        }
    }

}
