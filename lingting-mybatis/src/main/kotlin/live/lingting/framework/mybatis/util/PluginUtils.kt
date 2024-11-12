/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package live.lingting.framework.mybatis.util

import org.apache.ibatis.executor.Executor
import org.apache.ibatis.executor.parameter.ParameterHandler
import org.apache.ibatis.executor.statement.StatementHandler
import org.apache.ibatis.mapping.BoundSql
import org.apache.ibatis.mapping.MappedStatement
import org.apache.ibatis.mapping.ParameterMapping
import org.apache.ibatis.reflection.MetaObject
import org.apache.ibatis.reflection.SystemMetaObject
import org.apache.ibatis.session.Configuration
import java.lang.reflect.Proxy
import java.util.*

/**
 * 插件工具类
 *
 * @author TaoYu , hubin
 * @since 2017-06-20
 */
class PluginUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    /**
     * [org.apache.ibatis.executor.statement.BaseStatementHandler]
     */
    class MPStatementHandler internal constructor(private val statementHandler: MetaObject) {
        fun parameterHandler(): ParameterHandler {
            return get("parameterHandler")
        }

        fun mappedStatement(): MappedStatement {
            return get("mappedStatement")
        }

        fun executor(): Executor {
            return get("executor")
        }

        fun mPBoundSql(): MPBoundSql {
            return MPBoundSql(boundSql())
        }

        fun boundSql(): BoundSql {
            return get("boundSql")
        }

        fun configuration(): Configuration {
            return get("configuration")
        }

        private fun <T> get(property: String): T {
            return statementHandler.getValue(property) as T
        }
    }

    /**
     * [BoundSql]
     */
    class MPBoundSql internal constructor(private val delegate: BoundSql) {
        private val boundSql: MetaObject

        init {
            this.delegate = SystemMetaObject.forObject(delegate)
        }

        fun sql(): String {
            return delegate.sql
        }

        fun sql(sql: String?) {
            delegate.setValue("sql", sql)
        }

        fun parameterMappings(): List<ParameterMapping> {
            val parameterMappings = delegate.parameterMappings
            return ArrayList(parameterMappings)
        }

        fun parameterMappings(parameterMappings: List<ParameterMapping>) {
            delegate.setValue("parameterMappings", Collections.unmodifiableList(parameterMappings))
        }

        fun parameterObject(): Any {
            return get("parameterObject")
        }

        fun additionalParameters(): Map<String, Any> {
            return get("additionalParameters")
        }

        private fun <T> get(property: String): T {
            return delegate.getValue(property) as T
        }
    }

    companion object {
        const val DELEGATE_BOUNDSQL_SQL: String = "delegate.boundSql.sql"

        /**
         * 获得真正的处理对象,可能多层代理.
         */
        fun <T> realTarget(target: Any): T {
            if (Proxy.isProxyClass(target.javaClass)) {
                val metaObject = SystemMetaObject.forObject(target)
                return realTarget(metaObject.getValue("h.target"))
            }
            return target as T
        }

        /**
         * 给 BoundSql 设置 additionalParameters
         *
         * @param boundSql             BoundSql
         * @param additionalParameters additionalParameters
         */
        fun setAdditionalParameter(boundSql: BoundSql, additionalParameters: Map<String?, Any?>) {
            additionalParameters.forEach { (name: String?, value: Any?) -> boundSql.setAdditionalParameter(name, value) }
        }

        fun mpBoundSql(boundSql: BoundSql): MPBoundSql {
            return MPBoundSql(boundSql)
        }

        fun mpStatementHandler(statementHandler: StatementHandler): MPStatementHandler {
            var statementHandler = statementHandler
            statementHandler = realTarget(statementHandler)
            val `object` = SystemMetaObject.forObject(statementHandler)
            return MPStatementHandler(SystemMetaObject.forObject(`object`.getValue("delegate")))
        }
    }
}
