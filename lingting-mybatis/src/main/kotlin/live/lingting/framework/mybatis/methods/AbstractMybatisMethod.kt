package live.lingting.framework.mybatis.methods

import com.baomidou.mybatisplus.core.injector.AbstractMethod
import live.lingting.framework.util.ClassUtils
import java.sql.Connection
import java.util.concurrent.ConcurrentHashMap
import javax.sql.DataSource

/**
 * @author lingting 2024/11/25 14:58
 */
abstract class AbstractMybatisMethod(name: String) : AbstractMethod(name) {

    companion object {

        val DB_TYPE_CACHE = ConcurrentHashMap<DataSource, String>()

        fun resolveDatabaseTypeByUrl(jdbcUrl: String): String? {
            return when {
                jdbcUrl.startsWith("jdbc:mysql:") -> "mysql"
                jdbcUrl.startsWith("jdbc:sqlite:") -> "sqlite"
                jdbcUrl.startsWith("jdbc:postgresql:") -> "postgresql"
                jdbcUrl.startsWith("jdbc:oracle:") -> "oracle"
                jdbcUrl.startsWith("jdbc:h2:") -> "h2"
                else -> null
            }
        }

        fun resolveDatabaseTypeByDriver(driverClassName: String): String? {
            return when (driverClassName.lowercase()) {
                "com.mysql.cj.jdbc.driver", "com.mysql.jdbc.driver" -> "mysql"
                "org.sqlite.JDBC" -> "sqlite"
                "org.postgresql.Driver" -> "postgresql"
                "oracle.jdbc.driver.OracleDriver" -> "oracle"
                "org.h2.Driver" -> "h2"
                else -> null
            }
        }

        fun resolveByReflection(dataSource: DataSource): String? {
            var source = dataSource
            // spring 的 DelegatingDataSource 识别
            var cf = ClassUtils.classField(source.javaClass, "targetDataSource")
            if (cf != null && cf.canGet(source)) {
                val any = cf.get(source)
                if (any is DataSource) {
                    source = any
                }
            }

            // 提供了url返回的识别
            cf = ClassUtils.classField(source.javaClass, "url")
            if (cf == null) {
                return null
            }
            try {
                if (cf.visibleGet().canGet(source)) {
                    return cf.get(source)?.toString()
                }
                return null
            } catch (_: Exception) {
                return null
            }
        }

        fun resolveByConnection(dataSource: DataSource): String? {
            var connection: Connection? = null
            try {
                connection = dataSource.connection
                val data = connection.metaData
                var type = resolveDatabaseTypeByUrl(data.url)
                if (!type.isNullOrBlank()) {
                    return type
                }
                type = resolveDatabaseTypeByDriver(data.driverName)

                if (type != null) {
                    return type
                }
                throw UnsupportedOperationException("Unknown db type!")
            } catch (_: Exception) {
                return null
            } finally {
                connection?.close()
            }
        }
    }

    fun dbType() = DB_TYPE_CACHE.computeIfAbsent(configuration.environment.dataSource) { dataSource ->
        var type = resolveByReflection(dataSource)

        if (type == null) {
            type = resolveByConnection(dataSource)
        }

        if (type == null) {
            throw UnsupportedOperationException("Unknown db type!")
        }
        type
    }


}
