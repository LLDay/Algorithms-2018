package lesson3

import java.util.*

class SubSortedSet<T : Comparable<T>>(private val delegate: KtBinaryTree<T>,
                                      private val from: T?,
                                      private val to: T?,
                                      private val last: Boolean = false) : SortedSet<T> {

    override val size: Int
        get() = getSubSet().size

    private fun getSubSet(): Set<T> {
        if (delegate.size == 0)
            return setOf()

        val start = from ?: delegate.first()
        val end = to ?: delegate.last()

        return delegate.helperSubSet(start, end, last)
    }

    override fun add(element: T) = delegate.add(element)

    override fun addAll(elements: Collection<T>) = delegate.addAll(elements)

    override fun clear() = delegate.clear()


    inner class SubSetIterator : MutableIterator<T> {

        private val cachedDeque = ArrayDeque<T>()

        private lateinit var currentElement: T

        init {
            cachedDeque.addAll(getSubSet().toSortedSet())
        }

        override fun hasNext(): Boolean = !cachedDeque.isEmpty()

        override fun next(): T{
            currentElement = cachedDeque.pop()
            return currentElement
        }

        override fun remove() {
            remove(currentElement)
        }
    }


    override fun iterator() = SubSetIterator()

    override fun remove(element: T) = delegate.remove(element)

    override fun removeAll(elements: Collection<T>) = delegate.removeAll(elements)

    override fun retainAll(elements: Collection<T>) = delegate.retainAll(elements)

    override fun isEmpty() = size == 0


    override fun contains(element: T): Boolean = getSubSet().contains(element)

    override fun containsAll(elements: Collection<T>): Boolean {
        val subSet = getSubSet()

        for (item in elements)
            if (subSet.contains(item))
                return false

        return true
    }

    override fun comparator(): Comparator<in T>? = delegate.comparator()

    override fun subSet(fromElement: T, toElement: T) = delegate.subSet(fromElement, toElement)

    override fun headSet(toElement: T): SortedSet<T> = delegate.headSet(toElement)

    override fun tailSet(fromElement: T): SortedSet<T> = delegate.tailSet(fromElement)

    override fun last(): T? = getSubSet().max()

    override fun first(): T? = getSubSet().min()
}

