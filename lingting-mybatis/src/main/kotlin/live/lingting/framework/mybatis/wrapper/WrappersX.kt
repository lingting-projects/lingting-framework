package live.lingting.framework.mybatis.wrapper

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper

/**
 * @author Hccake 2021/1/14
 * @version 1.0
 */
class WrappersX private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        /**
         * 获取 LambdaQueryWrapperX&lt;T&gt;
         *
         * @param <T> 实体类泛型
         * @return LambdaQueryWrapperX&lt;T&gt;
        </T> */
        fun <T> lambdaQueryX(): LambdaQueryWrapperX<T> {
            return LambdaQueryWrapperX()
        }

        /**
         * 获取 LambdaQueryWrapperX&lt;T&gt;
         *
         * @param entity 实体类
         * @param <T>    实体类泛型
         * @return LambdaQueryWrapperX&lt;T&gt;
        </T> */
        fun <T> lambdaQueryX(entity: T): LambdaQueryWrapperX<T> {
            return LambdaQueryWrapperX(entity)
        }

        /**
         * 获取 LambdaQueryWrapperX&lt;T&gt;
         *
         * @param entityClass 实体类class
         * @param <T>         实体类泛型
         * @return LambdaQueryWrapperX&lt;T&gt;
         * @since 3.3.1
        </T> */
        fun <T> lambdaQueryX(entityClass: Class<T>?): LambdaQueryWrapperX<T> {
            return LambdaQueryWrapperX(entityClass)
        }

        /**
         * 获取 LambdaAliasQueryWrapper&lt;T&gt;
         *
         * @param entity 实体类
         * @param <T>    实体类泛型
         * @return LambdaAliasQueryWrapper&lt;T&gt;
        </T> */
        fun <T> lambdaAliasQueryX(entity: T): LambdaAliasQueryWrapperX<T> {
            return LambdaAliasQueryWrapperX(entity)
        }

        /**
         * 获取 LambdaAliasQueryWrapper&lt;T&gt;
         *
         * @param entityClass 实体类class
         * @param <T>         实体类泛型
         * @return LambdaAliasQueryWrapper&lt;T&gt;
         * @since 3.3.1
        </T> */
        fun <T> lambdaAliasQueryX(entityClass: Class<T>?): LambdaAliasQueryWrapperX<T> {
            return LambdaAliasQueryWrapperX(entityClass)
        }

        /**
         * 获取 LambdaUpdateWrapper&lt;T&gt; 复制 com.baomidou.mybatisplus.core.toolkit.Wrappers
         *
         * @param <T> 实体类泛型
         * @return LambdaUpdateWrapper&lt;T&gt;
        </T> */
        fun <T> lambdaUpdate(): LambdaUpdateWrapper<T> {
            return LambdaUpdateWrapper()
        }

        /**
         * 获取 LambdaUpdateWrapper&lt;T&gt; 复制 com.baomidou.mybatisplus.core.toolkit.Wrappers
         *
         * @param entity 实体类
         * @param <T>    实体类泛型
         * @return LambdaUpdateWrapper&lt;T&gt;
        </T> */
        fun <T> lambdaUpdate(entity: T): LambdaUpdateWrapper<T> {
            return LambdaUpdateWrapper(entity)
        }

        /**
         * 获取 LambdaUpdateWrapper&lt;T&gt; 复制 com.baomidou.mybatisplus.core.toolkit.Wrappers
         *
         * @param entityClass 实体类class
         * @param <T>         实体类泛型
         * @return LambdaUpdateWrapper&lt;T&gt;
         * @since 3.3.1
        </T> */
        fun <T> lambdaUpdate(entityClass: Class<T>?): LambdaUpdateWrapper<T> {
            return LambdaUpdateWrapper(entityClass)
        }
    }
}
