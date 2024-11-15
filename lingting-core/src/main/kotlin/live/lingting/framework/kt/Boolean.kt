package live.lingting.framework.kt

/**
 * @author lingting 2024/11/15 15:49
 */
fun <T : Boolean?> T.ifTrue(action: () -> Unit) {
    if (this == true) {
        action()
    }
}

fun <T : Boolean?> T.ifFalse(action: () -> Unit) {
    if (this == false) {
        action()
    }
}

fun <T : Boolean?, R : Any?> T.ifTrue(action: () -> R): R? = if (this == true) action() else null

fun <T : Boolean?, R : Any?> T.ifFalse(action: () -> R): R? = if (this == false) action() else null
