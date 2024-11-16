package live.lingting.framework.util

import java.util.Collections


/**
 * @author lingting
 */
object CollectionUtils {
    @SafeVarargs
    @JvmStatic
    fun <T> toList(vararg ts: T): List<T> {
        return ts.toList()
    }

    @SafeVarargs
    @JvmStatic
    fun <T> toSet(vararg ts: T): Set<T> {
        return ts.toSet()
    }

    @JvmStatic
    fun isEmpty(collection: Collection<*>?): Boolean {
        return collection.isNullOrEmpty()
    }

    @JvmStatic
    fun isEmpty(map: Map<*, *>?): Boolean {
        return map.isNullOrEmpty()
    }

    /**
     * 是否是否可以存放多个数据
     */
    @JvmStatic
    fun isMulti(obj: Any?): Boolean {
        if (obj == null) {
            return false
        }
        return obj is Iterable<*> || obj is Iterator<*> || obj.javaClass.isArray
    }

    /**
     * 复数对应转为 List
     */
    @JvmStatic
    fun multiToList(obj: Any?): List<Any?> {
        if (obj == null) {
            return emptyList()
        }

        if (!isMulti(obj)) {
            return toList(obj)
        } else if (obj is List<*>) {
            return obj as List<Any>
        } else if (obj is Collection<*>) {
            return obj.toList() as List<Any>
        }

        val list = ArrayList<Any?>()

        if (obj.javaClass.isArray) {
            Collections.addAll(list, *obj as Array<Any>)
        } else if (obj is Iterator<*>) {
            obj.forEachRemaining { list.add(it) }
        } else if (obj is Iterable<*>) {
            obj.forEach { list.add(it) }
        }

        return list
    }

    /**
     * 提取集合中指定数量的元素,
     *
     * @param number 提取元素数量, 不足则有多少提取多少
     */
    @JvmStatic
    fun <D> extract(collection: Collection<D>, number: Int): List<D> {
        return extract(collection.iterator(), number)
    }

    @JvmStatic
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
    @JvmStatic
    fun <D> split(collection: Collection<D>, size: Int): List<List<D>> {
        return split(collection.iterator(), size)
    }

    @JvmStatic
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

    @JvmStatic
    fun <K, V> toMap(keys: Collection<K>?, values: Collection<V>): Map<K, V> {
        val map: MutableMap<K, V> = HashMap()

        if (keys == null || keys.isEmpty()) {
            return map
        }

        val keyIterator = keys.iterator()
        val valueIterator = values.iterator()

        while (keyIterator.hasNext() && valueIterator.hasNext()) {
            val key = keyIterator.next()
            val value = valueIterator.next()
            if (key != null && value != null) {
                map.put(key, value)
            }
        }
        return map
    }

}
