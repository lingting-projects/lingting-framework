package live.lingting.framework.util

import java.util.*

/**
 * @author lingting
 */
class CollectionUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        @SafeVarargs
        fun <T> toList(vararg ts: T): List<T> {
            val list = ArrayList<T>()
            Collections.addAll(list, *ts)
            return list
        }

        @SafeVarargs
        fun <T> toSet(vararg ts: T): Set<T> {
            val set = HashSet<T>()
            Collections.addAll(set, *ts)
            return set
        }

        fun isEmpty(collection: Collection<*>?): Boolean {
            return collection == null || collection.isEmpty()
        }

        fun isEmpty(map: Map<*, *>?): Boolean {
            return map == null || map.isEmpty()
        }

        /**
         * 是否是否可以存放多个数据
         */
        fun isMulti(obj: Any?): Boolean {
            if (obj == null) {
                return false
            }
            return obj is Iterable<*> || obj is Iterator<*> || obj.javaClass.isArray()
        }

        /**
         * 复数对应转为 List
         */
        fun multiToList(obj: Any?): List<Any> {
            if (obj == null) {
                return ArrayList()
            }

            if (!isMulti(obj)) {
                return toList(obj)
            } else if (obj is List<*>) {
                return obj as List<Any>
            } else if (obj is Collection<*>) {
                return ArrayList(obj)
            }

            val list: MutableList<Any> = ArrayList()

            if (obj.javaClass.isArray()) {
                Collections.addAll(list, *obj as Array<Any?>)
            } else if (obj is Iterator<*>) {
                obj.forEachRemaining { e: E -> list.add(e) }
            } else if (obj is Iterable<*>) {
                obj.forEach { e: E -> list.add(e) }
            }

            return list
        }

        /**
         * 提取集合中指定数量的元素,
         *
         * @param number 提取元素数量, 不足则有多少提取多少
         */
        fun <D> extract(collection: Collection<D>, number: Int): List<D> {
            return extract(collection.iterator(), number)
        }

        fun <D> extract(iterator: Iterator<D>, number: Int): List<D> {
            val list: MutableList<D> = ArrayList(number)
            while (iterator.hasNext()) {
                list.add(iterator.next())
                if (list.size == number) {
                    break
                }
            }
            return list
        }

        /**
         * 分割为多个小list, 每个list最多拥有 size个元素
         *
         * @param collection 原始数据
         * @param size       单个list最多元素数量
         * @return java.util.List<java.util.List></java.util.List> < D>>
         */
        fun <D> split(collection: Collection<D>, size: Int): List<List<D>> {
            return split(collection.iterator(), size)
        }

        fun <D> split(iterator: Iterator<D>, size: Int): List<List<D>> {
            val list: MutableList<List<D>> = ArrayList()

            var items: MutableList<D> = ArrayList(size)

            while (iterator.hasNext()) {
                val next = iterator.next()
                items.add(next)

                if (items.size == size) {
                    list.add(items)
                    items = ArrayList(size)
                }
            }

            if (!isEmpty(items)) {
                list.add(items)
            }

            return list
        }

        fun <K, V> toMap(keys: Collection<K>?, values: Collection<V>): Map<K, V> {
            val map: MutableMap<K, V> = HashMap()

            if (keys == null || keys.isEmpty()) {
                return map
            }

            val keyIterator = keys.iterator()
            val valueIterator = values.iterator()

            while (keyIterator.hasNext() && valueIterator.hasNext()) {
                val key: K? = keyIterator.next()
                val value: V? = valueIterator.next()
                if (key != null && value != null) {
                    map.put(key, value)
                }
            }

            return map
        }
    }
}
