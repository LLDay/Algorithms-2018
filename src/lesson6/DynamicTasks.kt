@file:Suppress("UNUSED_PARAMETER")

package lesson6

import java.io.File
import kotlin.math.max
import kotlin.math.min


/**
 * Наибольшая общая подпоследовательность.
 * Средняя
 *
 * Дано две строки, например "nematode knowledge" и "empty bottle".
 * Найти их самую длинную общую подпоследовательность -- в примере это "emt ole".
 * Подпоследовательность отличается от подстроки тем, что её символы не обязаны идти подряд
 * (но по-прежнему должны быть расположены в исходной строке в том же порядке).
 * Если общей подпоследовательности нет, вернуть пустую строку.
 * При сравнении подстрок, регистр символов *имеет* значение.
 */

// n = first.size
// m = second.size
// Трудоемкость O(m * n)
// Ресурсоемкость O((m + 1) * (n + 1))
fun <T> lcs(first: Collection<T>, second: Collection<T>): List<T> {
    val table = Array(first.size + 1) { IntArray(second.size + 1) { 0 } }

    // filling table
    for (i in 1..first.size)
        for (j in 1..second.size)
            if (first.elementAt(i - 1) == second.elementAt(j - 1))
                table[i][j] = table[i - 1][j - 1] + 1
            else table[i][j] = max(table[i - 1][j], table[i][j - 1])

    var i = first.size
    var j = second.size

    val returnList = mutableListOf<T>()

    // finding the way
    while (i > 0 && j > 0) {
        val currElement = first.elementAt(i - 1)
        if (currElement == second.elementAt(j - 1)) {
            returnList.add(currElement)
            i--; j--
            continue
        }

        if (table[i - 1][j] == table[i][j])
            i--
        else j--
    }

    return returnList.reversed()
}

fun longestCommonSubSequence(first: String, second: String): String {
    val lcsList = lcs(first.toList(), second.toList())
    return lcsList.joinToString("")
}


/**
 * Наибольшая возрастающая подпоследовательность
 * Средняя
 *
 * Дан список целых чисел, например, [2 8 5 9 12 6].
 * Найти в нём самую длинную возрастающую подпоследовательность.
 * Элементы подпоследовательности не обязаны идти подряд,
 * но должны быть расположены в исходном списке в том же порядке.
 * Если самых длинных возрастающих подпоследовательностей несколько (как в примере),
 * то вернуть ту, в которой числа расположены раньше (приоритет имеют первые числа).
 * В примере ответами являются 2, 8, 9, 12 или 2, 5, 9, 12 -- выбираем первую из них.
 */
fun longestIncreasingSubSequence(list: List<Int>): List<Int> {
    val sortedList = list.sorted()
    return lcs(list, sortedList)
}

/**
 * Самый короткий маршрут на прямоугольном поле.
 * Сложная
 *
 * В файле с именем inputName задано прямоугольное поле:
 *
 * 0 2 3 2 4 1
 * 1 5 3 4 6 2
 * 2 6 2 5 1 3
 * 1 4 3 2 6 2
 * 4 2 3 1 5 0
 *
 * Можно совершать шаги длиной в одну клетку вправо, вниз или по диагонали вправо-вниз.
 * В каждой клетке записано некоторое натуральное число или нуль.
 * Необходимо попасть из верхней левой клетки в правую нижнюю.
 * Вес маршрута вычисляется как сумма чисел со всех посещенных клеток.
 * Необходимо найти маршрут с минимальным весом и вернуть этот минимальный вес.
 *
 * Здесь ответ 2 + 3 + 4 + 1 + 2 = 12
 */

fun getShortestPath(table: Array<IntArray>): Int {
    val rows = table.size
    val columns = table.first().size

    val minTable = Array(rows) { IntArray(columns) { Int.MAX_VALUE } }
    minTable[0][0] = table[0][0]
    fillMinPathTable(table, minTable, rows - 1, columns - 1)

    return minTable.last().last()
}

//n = InputTable.rows
//m = InputTable.columns
//Трудоемкость O(n * m)
//Ресурсоемкость O(n * m)
fun fillMinPathTable(table: Array<IntArray>, minTable: Array<IntArray>, row: Int, column: Int) {
    if (row - 1 >= 0 && minTable[row - 1][column] == Int.MAX_VALUE)
        fillMinPathTable(table, minTable, row - 1, column)

    if (column - 1 >= 0 && minTable[row][column - 1] == Int.MAX_VALUE)
        fillMinPathTable(table, minTable, row, column - 1)

    val currentCell = table[row][column]
    var minElement = Int.MAX_VALUE

    if (row > 0)
        minElement = min(minElement, minTable[row - 1][column] + currentCell)
    if (column > 0)
        minElement = min(minElement, minTable[row][column - 1] + currentCell)
    if (row > 0 && column > 0)
        minElement = min(minElement, minTable[row - 1][column - 1] + currentCell)

    if (minElement == Int.MAX_VALUE)
        minTable[row][column] = currentCell
    else minTable[row][column] = minElement
}

fun shortestPathOnField(inputName: String): Int {
    val buffReader = File(inputName).bufferedReader()
    val lines = buffReader.readLines()
    buffReader.close()

    if (lines.isEmpty())
        return -1

    val rows = lines.size
    val columns = lines[0].length / 2 + 1
    val table = Array(rows) { IntArray(columns) }

    for (i in 0 until rows) {
        for (j in 0 until columns)
            table[i][j] = lines[i][j * 2].toString().toInt()
    }

    return getShortestPath(table)
}

// Задачу "Максимальное независимое множество вершин в графе без циклов"
// смотрите в уроке 5