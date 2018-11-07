@file:Suppress("UNUSED_PARAMETER")

package lesson2

import java.io.File
import java.lang.Math.floor
import java.lang.Math.sqrt
import java.util.*
import kotlin.collections.LinkedHashSet

/**
 * Получение наибольшей прибыли (она же -- поиск максимального подмассива)
 * Простая
 *
 * Во входном файле с именем inputName перечислены цены на акции компании в различные (возрастающие) моменты времени
 * (каждая цена идёт с новой строки). Цена -- это целое положительное число. Пример:
 *
 * 201
 * 196
 * 190
 * 198
 * 187
 * 194
 * 193
 * 185
 *
 * Выбрать два момента времени, первый из них для покупки акций, а второй для продажи, с тем, чтобы разница
 * между ценой продажи и ценой покупки была максимально большой. Второй момент должен быть раньше первого.
 * Вернуть пару из двух моментов.
 * Каждый момент обозначается целым числом -- номер строки во входном файле, нумерация с единицы.
 * Например, для приведённого выше файла результат должен быть Pair(3, 4)
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
//Трудоемкость O(n)
//Ресурсоемкость O(n)
fun optimizeBuyAndSell(inputName: String): Pair<Int, Int> {
    val numberList = mutableListOf<Int>()

    try {
        val scanner = Scanner(File(inputName))
        while (scanner.hasNext())
            numberList.add(Integer.parseInt(scanner.next()))
        scanner.close()
    } catch (e: Exception) {
        throw IllegalArgumentException(e.message)
    }

    var startIndex = -1
    var endIndex = numberList.size

    var minIndex = -1
    var minNum = Integer.MAX_VALUE
    var difference = Integer.MIN_VALUE

    for (i in 0 until numberList.lastIndex) {
        if (numberList[i] > numberList[i + 1])
            continue

        if (numberList[i] < minNum) {
            minNum = numberList[i]
            minIndex = i
        }

        val currDiff = numberList[i + 1] - minNum
        if (currDiff > difference) {
            startIndex = minIndex
            endIndex = i + 1
            difference = currDiff
        }
    }

    return Pair(startIndex + 1, endIndex + 1)
}

/**
 * Задача Иосифа Флафия.
 * Простая
 *
 * Образовав круг, стоят menNumber человек, пронумерованных от 1 до menNumber.
 *
 * 1 2 3
 * 8   4
 * 7 6 5
 *
 * Мы считаем от 1 до choiceInterval (например, до 5), начиная с 1-го человека по кругу.
 * Человек, на котором остановился счёт, выбывает.
 *
 * 1 2 3
 * 8   4
 * 7 6 х
 *
 * Далее счёт продолжается со следующего человека, также от 1 до choiceInterval.
 * Выбывшие при счёте пропускаются, и человек, на котором остановился счёт, выбывает.
 *
 * 1 х 3
 * 8   4
 * 7 6 Х
 *
 * Процедура повторяется, пока не останется один человек. Требуется вернуть его номер (в данном случае 3).
 *
 * 1 Х 3
 * х   4
 * 7 6 Х
 *
 * 1 Х 3
 * Х   4
 * х 6 Х
 *
 * х Х 3
 * Х   4
 * Х 6 Х
 *
 * Х Х 3
 * Х   х
 * Х 6 Х
 *
 * Х Х 3
 * Х   Х
 * Х х Х
 */
//Трудоемкость O(n)
//Ресурсоемкость O(1)
fun josephTask(menNumber: Int, choiceInterval: Int): Int {
    var res = 0
    for (i in 1..menNumber)
        res = (res + choiceInterval) % i
    return res + 1
}


/**
 * Наибольшая общая подстрока.
 * Средняя
 *
 * Дано две строки, например ОБСЕРВАТОРИЯ и КОНСЕРВАТОРЫ.
 * Найти их самую длинную общую подстроку -- в примере это СЕРВАТОР.
 * Если общих подстрок нет, вернуть пустую строку.
 * При сравнении подстрок, регистр символов *имеет* значение.
 * Если имеется несколько самых длинных общих подстрок одной длины,
 * вернуть ту из них, которая встречается раньше в строке first.
 */
//Трудоемкость O(n²)
//Ресурсоемкость O(n)
fun longestCommonSubstring(first: String, second: String): String {
    var maxCommonSubstring = ""
    var maxLength = 0
    var firstIndex = 0

    while (firstIndex + maxLength < second.length) {
        val subString = first.substring(firstIndex, firstIndex + maxLength + 1)
        val indexSearch = second.indexOf(subString)

        if (indexSearch != -1) {
            val builder = StringBuilder(subString)

            var index = maxLength + 1
            while (true) {
                val firstAdvancedIndex = index + firstIndex
                val secondAdvancedIndex = index + indexSearch

                if (firstAdvancedIndex > first.lastIndex || secondAdvancedIndex > second.lastIndex)
                    break

                val nextCharSecond = second[secondAdvancedIndex]
                val nextCharFirst = first[firstAdvancedIndex]

                if (nextCharFirst == nextCharSecond) {
                    builder.append(nextCharFirst)
                    index++
                    continue
                }
                break
            }

            val advancedSubString = builder.toString()
            if (advancedSubString.length > maxCommonSubstring.length) {
                maxCommonSubstring = advancedSubString
                maxLength = subString.length
            }
        }

        firstIndex++
    }

    return maxCommonSubstring
}

/**
 * Число простых чисел в интервале
 * Простая
 *
 * Рассчитать количество простых чисел в интервале от 1 до limit (включительно).
 * Если limit <= 1, вернуть результат 0.
 *
 * Справка: простым считается число, которое делится нацело только на 1 и на себя.
 * Единица простым числом не считается.
 */
fun isPrime(number: Int): Boolean {
    val thr = floor(sqrt(number.toDouble())).toInt()

    if (number % 2 == 0)
        return false

    for (i in 3..thr step 2)
        if (number % i == 0)
            return false

    return true
}

//Трудоемкость O(n²)
//Ресурсоемкость O(1)
fun calcPrimesNumber(limit: Int): Int {
    var counter = 0
    if (limit > 1)
        counter++

    for (i in 3..limit step 2)
        if (isPrime(i))
            counter++

    return counter
}

/**
 * Балда
 * Сложная
 *
 * В файле с именем inputName задана матрица из букв в следующем формате
 * (отдельные буквы в ряду разделены пробелами):
 *
 * И Т Ы Н
 * К Р А Н
 * А К В А
 *
 * В аргументе words содержится множество слов для поиска, например,
 * ТРАВА, КРАН, АКВА, НАРТЫ, РАК.
 *
 * Попытаться найти каждое из слов в матрице букв, используя правила игры БАЛДА,
 * и вернуть множество найденных слов. В данном случае:
 * ТРАВА, КРАН, АКВА, НАРТЫ
 *
 * И т Ы Н     И т ы Н
 * К р а Н     К р а н
 * А К в а     А К В А
 *
 * Все слова и буквы -- русские или английские, прописные.
 * В файле буквы разделены пробелами, строки -- переносами строк.
 * Остальные символы ни в файле, ни в словах не допускаются.
 */
data class Pointer(val map: String, val rowLength: Int, val index: Int) {
    private val previewIndexSet = LinkedHashSet<Int>()

    private fun nextPointers(): List<Pointer> {
        val moveList = listOf(1, -1, rowLength, -rowLength)
        val endIndex = map.lastIndex
        val list = mutableListOf<Pointer>()
        
        for (move in moveList) {
            val nextIndex = index + move

            if (nextIndex in 0..endIndex && !previewIndexSet.contains(nextIndex)) {
                val nextPointer = Pointer(map, rowLength, nextIndex)
                nextPointer.previewIndexSet.addAll(this.previewIndexSet)
                nextPointer.previewIndexSet.add(index)
                list.add(nextPointer)
            }
        }
        
        return list
    }

    fun contains(word: String): Boolean {
        if (word == "")
            return true

        if (word.first() != map[index])
            return false

        for (pointer in nextPointers())
            if (pointer.contains(word.substring(1)))
                return true

        return false
    }
}

//Трудоемкость O(n)
//Ресурсоемкость O(n)
fun baldaSearcher(inputName: String, words: Set<String>): Set<String> {
    val reader = File(inputName).reader()
    val lines = reader.readLines()
    reader.close()

    val str = lines.joinToString("")
            .replace("\n", "")
            .replace(" ", "")

    val rowLength = str.length / lines.size
    val pointerList = mutableListOf<Pointer>()
    var addingIndex = 0

    val firstCharSet = HashSet<Char>()
    for (word in words)
        firstCharSet.add(word.first())

    for (ch in str) {
        if (firstCharSet.contains(ch))
            pointerList.add(Pointer(str, rowLength, addingIndex))
        addingIndex++
    }

    val resultSet = mutableSetOf<String>()

    for (word in words) {
        for (pointer in pointerList)
            if (pointer.contains(word))
                resultSet.add(word)
    }

    return resultSet
}