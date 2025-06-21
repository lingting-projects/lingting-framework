package live.lingting.framework.mybatis.methods

/**
 * @author lingting 2020/5/27 11:47
 */
class InsertIgnore : AbstractInsert("insertIgnore") {

    override fun getSql(): String {
        val dbType = dbType()
        val str = when (dbType) {
            "mysql" -> "INSERT IGNORE INTO %s %s VALUES %s"
            "sqlite" -> "INSERT OR IGNORE INTO %s %s VALUES %s"
            "postgresql" -> "INSERT INTO %s %s VALUES %s ON CONFLICT DO NOTHING"
            else -> throw UnsupportedOperationException("Unsupported database type: $dbType")
        }
        return "<script>${str}</script>"
    }

}
