package lesson3

import java.util.*
import kotlin.NoSuchElementException

// Attention: comparable supported but comparator is not
class KtBinaryTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private var root: Node<T>? = null

    override var size = 0
        private set

    private class Node<T>(val value: T) {

        var left: Node<T>? = null

        var right: Node<T>? = null

        override fun toString(): String {
            return value.toString()
        }
    }

    //n = size
    //Трудоемкость O(n)
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

    private fun disconnect(node: Node<T>) {
        val nodeHead = findHead(node.value)

        if (nodeHead != null) {
            if (nodeHead.value < node.value)
                nodeHead.right = null
            else
                nodeHead.left = null
        }
    }

    //n = size
    //Трудоемкость O(n)
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
    //Трудоемкость O(n)
    //Ресурсоемкость O(1)
    override fun remove(element: T): Boolean {
        if (root == null && !contains(element))
            return false

        if (size == 1) {
            root = null
            size = 0
            return true
        }

        var removeable = root
        val removeHead = findHead(element)

        if (removeHead != null) {
            if (removeHead.value > element)
                removeable = removeHead.left
            else removeable = removeHead.right
        }

        var replaceable = removeable!!

        //right nearest
        if (replaceable.right != null) {
            replaceable = replaceable.right!!

            while (replaceable.left != null)
                replaceable = replaceable.left!!
        }
        //left nearest
        else if (replaceable.left != null) {
            replaceable = replaceable.left!!

            while (replaceable.right != null)
                replaceable = replaceable.right!!
        }

        disconnect(replaceable)

        removeable.right?.let { replaceable.right = it }
        removeable.left?.let { replaceable.left = it }

        if (removeable === root)
            root = replaceable

        else if (replaceable.value != element) {
            if (removeHead!!.value < element)
                removeHead.right = replaceable
            else removeHead.left = replaceable
        }

        size--
        return true
    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    //n = size
    //Трудоемкость O(n)
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

        private val cachedDeque = ArrayDeque<T>()

        private lateinit var currentElement: T

        init {
            cachedDeque.addAll(toSet().toSortedSet())
        }

        /**
         * Поиск следующего элемента
         * Средняя
         */
        override fun hasNext(): Boolean = !cachedDeque.isEmpty()

        override fun next(): T{
            currentElement = cachedDeque.pop()
            return currentElement
        }

        /**
         * Удаление следующего элемента
         * Сложная
         */
        override fun remove() {
            remove(currentElement)
        }
    }

    override fun iterator(): MutableIterator<T> = BinaryTreeIterator()

    override fun comparator(): Comparator<in T>? = null

    public fun toSet(): Set<T> {
        if (size == 0)
            return setOf()

        return helperSubSet(first(), last(), includeLast = true)
    }

    //n = size
    //Трудоемкость O(n)
    //Ресурсоемкость O(n)
    private fun setWithoutHead(head: Node<T>): Set<T> {
        var leftNode = head
        var rightNode = head

        while (leftNode.left != null)
            leftNode = leftNode.left!!

        while (rightNode.right != null)
            rightNode = rightNode.right!!

        if (leftNode.value == rightNode.value)
            return setOf()

        val set = helperSubSet(leftNode.value, rightNode.value, includeLast = true).toMutableSet()
        set.remove(head.value)

        return set
    }

    //n = size
    //Трудоемкость O(n)
    //Ресурсоемкость O(n)
    public fun helperSubSet(fromElement: T, toElement: T, includeLast: Boolean = false): Set<T> {
        val set = mutableSetOf<T>()
        val queue = ArrayDeque<Node<T>>()

        if (root == null) return set

        queue.push(root)

        while (!queue.isEmpty()) {
            val currentNode = queue.pop()
            val currentBelongs = currentNode.value in fromElement..toElement
                    && (includeLast || !currentNode.value.equals(toElement))

            if (currentBelongs) {
                set.add(currentNode.value)

                if (currentNode.left != null)
                    queue.push(currentNode.left)

                if (currentNode.right != null)
                    queue.push(currentNode.right)
            }

            else {
                if (currentNode.value < fromElement && currentNode.right != null)
                    queue.push(currentNode.right)

                else if (currentNode.value >= toElement && currentNode.left != null)
                    queue.push(currentNode.left)
            }
        }

        return set
    }

    /**
     * Для этой задачи нет тестов (есть только заготовка subSetTest), но её тоже можно решить и их написать
     * Очень сложная
     */
    // based on helperSubSet
    override fun subSet(fromElement: T, toElement: T) =
            SubSortedSet(this, fromElement, toElement)

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     */
    // based on helperSubSet
    override fun headSet(toElement: T) =
            SubSortedSet(this, null, toElement)

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     */
    // based on helperSubSet
    override fun tailSet(fromElement: T) =
            SubSortedSet(this, fromElement, null, last = true)


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
