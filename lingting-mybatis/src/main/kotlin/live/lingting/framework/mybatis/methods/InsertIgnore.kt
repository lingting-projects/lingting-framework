package live.lingting.framework.mybatis.methods

/**
 * @author lingting 2020/5/27 11:47
 */
class InsertIgnore : AbstractInsert("insertIgnore") {
    override fun getSql(): String {
        return "<script>insert ignore into %s %s value %s</script>"
    }
}
