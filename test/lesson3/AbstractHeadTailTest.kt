package lesson3

import java.lang.Math.max
import java.lang.Math.min
import java.util.*
import kotlin.test.*

abstract class AbstractHeadTailTest {
    private lateinit var tree: SortedSet<Int>

    private val rand = Random()

    protected fun fillTree(empty: SortedSet<Int>) {
        this.tree = empty
        //В произвольном порядке добавим числа от 1 до 10
        tree.add(5)
        tree.add(1)
        tree.add(2)
        tree.add(7)
        tree.add(9)
        tree.add(10)
        tree.add(8)
        tree.add(4)
        tree.add(3)
        tree.add(6)
    }


    protected fun doHeadSetTest() {
        var set: SortedSet<Int> = tree.headSet(5)

        assertEquals(true, set.contains(1))
        assertEquals(true, set.contains(2))
        assertEquals(true, set.contains(3))
        assertEquals(true, set.contains(4))
        assertEquals(false, set.contains(5))
        assertEquals(false, set.contains(6))
        assertEquals(false, set.contains(7))
        assertEquals(false, set.contains(8))
        assertEquals(false, set.contains(9))
        assertEquals(false, set.contains(10))

        set = tree.headSet(127)
        for (i in 1..10)
            assertEquals(true, set.contains(i))
    }

    protected fun doTailSetTest() {
        var set: SortedSet<Int> = tree.tailSet(5)

        assertEquals(false, set.contains(1))
        assertEquals(false, set.contains(2))
        assertEquals(false, set.contains(3))
        assertEquals(false, set.contains(4))
        assertEquals(true, set.contains(5))
        assertEquals(true, set.contains(6))
        assertEquals(true, set.contains(7))
        assertEquals(true, set.contains(8))
        assertEquals(true, set.contains(9))
        assertEquals(true, set.contains(10))

        set = tree.tailSet(-128)
        for (i in 1..10)
            assertEquals(true, set.contains(i))
    }

    protected fun doHeadSetRelationTest() {
        val set: SortedSet<Int> = tree.headSet(7)
        assertEquals(6, set.size)
        assertEquals(10, tree.size)
        tree.add(0)
        assertTrue(set.contains(0))
        set.remove(4)
        assertFalse(tree.contains(4))
        tree.remove(6)
        assertFalse(set.contains(6))
        tree.add(12)
        assertFalse(set.contains(12))
        assertEquals(5, set.size)
        assertEquals(10, tree.size)
    }

    protected fun doTailSetRelationTest() {
        val set: SortedSet<Int> = tree.tailSet(4)
        assertEquals(7, set.size)
        assertEquals(10, tree.size)
        tree.add(12)
        assertTrue(set.contains(12))
        set.remove(4)
        assertFalse(tree.contains(4))
        tree.remove(6)
        assertFalse(set.contains(6))
        tree.add(0)
        assertFalse(set.contains(0))
        assertEquals(6, set.size)
        assertEquals(10, tree.size)
    }

    protected fun doSubSetTest() {
        val testTree = KtBinaryTree<Int>()

        for (i in 0..1000)
            testTree.add(rand.nextInt())

        val first = testTree.elementAt(rand.nextInt(testTree.size - 1))
        val second = testTree.elementAt(rand.nextInt(testTree.size - 1))

        val min = min(first, second)
        val max = max(first, second)

        val subSet = testTree.subSet(min, max)
        val mutSet = mutableSetOf<Int>()

        mutSet.addAll(testTree.tailSet(max))
        mutSet.addAll(testTree.headSet(min))

        assertEquals(mutSet.size + subSet.size, testTree.size)
        for (item in subSet)
            assertFalse(mutSet.contains(item))

        val treeSet = testTree.toSet()
        assertEquals(treeSet.size, testTree.size)

        for (item in treeSet)
            assertTrue(treeSet.contains(item))
    }

    protected fun doRemoveTest() {
        val testTree = KtBinaryTree<Int>()

        for (i in 0..1000)
            testTree.add(rand.nextInt())

        val currentSize = testTree.size
        val removeSet = mutableSetOf<Int>()

        removeSet.add(testTree.first())
        removeSet.add(testTree.last())

        for (i in 0 until currentSize step 3)
            removeSet.add(testTree.elementAt(i))

        for (item in removeSet)
            testTree.remove(item)

        for (item in removeSet)
            assertEquals(false, testTree.contains(item))

        assertEquals(testTree.size, currentSize - removeSet.size)
        assertEquals(true, testTree.checkInvariant())
    }
}