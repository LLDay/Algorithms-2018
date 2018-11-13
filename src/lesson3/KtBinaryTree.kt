package lesson3

import java.lang.Math.abs
import java.util.*
import kotlin.NoSuchElementException

// Attention: comparable supported but comparator is not
class KtBinaryTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private var root: Node<T>? = null

    override var size = 0
        private set

    private val cache = TreeCache()

    private class Node<T>(val value: T) {

        var left: Node<T>? = null

        var right: Node<T>? = null

        override fun toString(): String {
            return value.toString()
        }
    }

    private inner class TreeCache {

        private var cache = setOf<T>()

        private var actual = false

        private var cacheSize = 0

        fun obsolete() {
            actual = false
        }

        fun isObsolete() = !actual || this.cacheSize != size

        fun update() {
            this.cache = toSet()
            this.actual = true
            this.cacheSize = size
        }

        //n = size
        //Трудоемкость O(n) (cached: O(1))
        //Ресурсоемкость O(n)
        fun getCache(): Set<T> {
            if (isObsolete())
                update()

            return this.cache
        }
    }

    //n = size
    //Трудоемкость O(ln n)
    //Ресурсоемкость O(1)
    override fun add(element: T): Boolean {
        val closest = find(element)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)
        if (comparison == 0) {
            return false
        }
        val newNode = Node(element)
        when {
            closest == null -> root = newNode
            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
            }
            else -> {
                assert(closest.right == null)
                closest.right = newNode
            }
        }
        size++
        return true
    }

    override fun checkInvariant(): Boolean =
            root?.let { checkInvariant(it) } ?: true

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }

    //n = size
    //Трудоемкость O(ln n)
    //Ресурсоемкость O(1)
    private fun findHead(value: T) =
            root?.let { findHead(it, null, value) }

    private fun findHead(currentNode: Node<T>, lastNode: Node<T>?, value: T): Node<T>? {
        if (value == currentNode.value)
            return lastNode

        if (currentNode.value < value)
            return currentNode.right?.let { findHead(it, currentNode, value) }

        return currentNode.left?.let { findHead(it, currentNode, value) }
    }

    /**
     * Удаление элемента в дереве
     * Средняя
     */
    //n = size
    //Трудоемкость O(n*ln(n))
    //Ресурсоемкость O(n)
    override fun remove(element: T): Boolean {
        if (root == null)
            return false

        if (root!!.value == element) {
            val set = toNormalSet(setWithoutHead(root!!))
            root = null

            for (item in set)
                add(item)

            size -= set.size + 1
            return true
        }

        val head = findHead(element) ?: return false
        val set: Set<T>

        if (element > head.value) {
            set = toNormalSet(setWithoutHead(head.right!!))
            head.right = null
        } else {
            set = toNormalSet(setWithoutHead(head.left!!))
            head.left = null
        }

        for (item in set)
            add(item)

        size -= set.size + 1
        cache.obsolete()
        return true
    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    //n = size
    //Трудоемкость O(ln(n))
    //Ресурсоемкость O(1)
    private fun find(value: T): Node<T>? =
            root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.let { find(it, value) } ?: start
            else -> start.right?.let { find(it, value) } ?: start
        }
    }

    inner class BinaryTreeIterator : MutableIterator<T> {

        private var currentIndex = 0

        /**
         * Поиск следующего элемента
         * Средняя
         */
        override fun hasNext(): Boolean = currentIndex < cache.getCache().size

        //n = size
        //Трудоемкость O(n) (cached: O(1))
        //Ресурсоемкость O(n)
        override fun next(): T {
            if (cache.isObsolete())
                throw ConcurrentModificationException()

            val currCache = cache.getCache()
            if (currentIndex >= currCache.size)
                throw NoSuchElementException()

            return currCache.elementAt(currentIndex++)
        }

        /**
         * Удаление следующего элемента
         * Сложная
         */
        //n = size
        //Трудоемкость O(n*ln(n))
        //Ресурсоемкость O(n)
        override fun remove() {
            if (cache.isObsolete())
                throw ConcurrentModificationException()

            remove(cache.getCache().elementAt(--currentIndex))
            cache.update()
        }
    }

    override fun iterator(): MutableIterator<T> = BinaryTreeIterator()

    override fun comparator(): Comparator<in T>? = null

    //n = size
    //Трудоемкость O(n)
    //Ресурсоемкость O(n)
    private fun toSet(): Set<T> {
        val set = mutableSetOf<T>()
        if (root == null)
            return setOf()

        helperToSet(root!!, set)
        return set
    }

    //n = size
    //Трудоемкость O(n)
    //Ресурсоемкость O(n)
    private fun setWithoutHead(head: Node<T>): Set<T> {
        val set = mutableSetOf<T>()

        if (head.right != null)
            set.addAll(toSet(head.right!!))

        if (head.left != null)
            set.addAll(toSet(head.left!!))

        return set
    }

    private fun toSet(head: Node<T>): Set<T> {
        val set = mutableSetOf<T>()
        helperToSet(head, set)
        return set
    }

    //n = set.size
    //Трудоемкость O(n)
    //Ресурсоемкость O(n)
    private fun toNormalSet(set: Set<T>): Set<T> {
        val mutSet = mutableSetOf<T>()
        val list = set.toList()
        val center = list.size / 2

        var adder = 0
        var lastPos = center

        while (abs(adder) < list.size) {
            adder *= -1

            lastPos += adder
            mutSet.add(list[lastPos])

            if (adder >= 0) adder++
            else adder--
        }
        return mutSet
    }

    private fun helperToSet(head: Node<T>, set: MutableSet<T>) {
        if (head.left != null)
            helperToSet(head.left!!, set)

        set.add(head.value)

        if (head.right != null)
            helperToSet(head.right!!, set)
    }

    /**
     * Для этой задачи нет тестов (есть только заготовка subSetTest), но её тоже можно решить и их написать
     * Очень сложная
     */
    //Not relative
    //n = size
    //Трудоемкость O(n ln n)
    //Ресурсоемкость O(n)
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
        val currentSet = cache.getCache()

        val subSet = currentSet.filter { it >= fromElement && it < toElement }
        return subSet.toSortedSet()
    }

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     */
    //Not relative
    //n = size
    //Трудоемкость O(n ln n)
    //Ресурсоемкость O(n)
    override fun headSet(toElement: T): SortedSet<T> {
        val currentSet = cache.getCache()

        val subSet = currentSet.filter { it < toElement }
        return subSet.toSortedSet()
    }

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     */
    //Not relative
    //n = size
    //Трудоемкость O(n ln n)
    //Ресурсоемкость O(n)
    override fun tailSet(fromElement: T): SortedSet<T> {
        val currentSet = cache.getCache()

        val subSet = currentSet.filter { it >= fromElement }
        return subSet.toSortedSet()
    }

    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null)
            current = current.left!!

        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null)
            current = current.right!!

        return current.value
    }
}
