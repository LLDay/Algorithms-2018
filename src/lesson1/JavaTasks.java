package lesson1;

import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.*;

@SuppressWarnings("unused")
public class JavaTasks {
    /**
     * Сортировка времён
     *
     * Простая
     * (Модифицированная задача с сайта acmp.ru)
     *
     * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС,
     * каждый на отдельной строке. Пример:
     *
     * 13:15:19
     * 07:26:57
     * 10:00:03
     * 19:56:14
     * 13:15:19
     * 00:40:31
     *
     * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
     * сохраняя формат ЧЧ:ММ:СС. Одинаковые моменты времени выводить друг за другом. Пример:
     *
     * 00:40:31
     * 07:26:57
     * 10:00:03
     * 13:15:19
     * 13:15:19
     * 19:56:14
     *
     * В случае обнаружения неверного формата файла бросить любое исключение.
     */

    static public class Time implements  Comparable<Time> {
        public Time(String timeStr) {
            if (!timeStr.matches("^\\d{2}:\\d{2}:\\d{2}$"))
                throw new IllegalArgumentException("Wrong time format");

            str = timeStr;
            String[] h_m_s_str = timeStr.split(":");
            int h = Integer.parseInt(h_m_s_str[0]);
            int m = Integer.parseInt(h_m_s_str[1]);
            int s = Integer.parseInt(h_m_s_str[2]);

            if (h > 24 || m > 60 || s > 60)
                throw new IllegalArgumentException("Wrong time format");

            sec = h * 3600 + m * 60 + s;
        }

        @Override
        public String toString() {
            return str;
        }

        @Override
        public int compareTo(@NotNull Time o) {
            return this.sec - o.sec;
        }

        private int sec;
        private String str;
    }

    static public void sortTimes(String inputName, String outputName) {
        File inputFile = new File(inputName);
        File outputFile = new File(outputName);

        List<Time> timeList = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(inputFile);
            while (scanner.hasNext())
                timeList.add(new Time(scanner.next()));
            scanner.close();

            Collections.sort(timeList);

            Writer writer = new FileWriter(outputFile);
            for (Time el : timeList)
                writer.write(el.toString() + '\n');
            writer.close();
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Сортировка адресов
     *
     * Средняя
     *
     * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
     * где они прописаны. Пример:
     *
     * Петров Иван - Железнодорожная 3
     * Сидоров Петр - Садовая 5
     * Иванов Алексей - Железнодорожная 7
     * Сидорова Мария - Садовая 5
     * Иванов Михаил - Железнодорожная 7
     *
     * Людей в городе может быть до миллиона.
     *
     * Вывести записи в выходной файл outputName,
     * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
     * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
     *
     * Железнодорожная 3 - Петров Иван
     * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
     * Садовая 5 - Сидоров Петр, Сидорова Мария
     *
     * В случае обнаружения неверного формата файла бросить любое исключение.
     */

    static public void sortAddresses(String inputName, String outputName) {
        File inputFile = new File(inputName);
        File outputFile = new File(outputName);

        Map<String, Map<Integer, Set<String>>> treeMultiMap = new TreeMap<>();

        try {
            Scanner scanner = new Scanner(inputFile);
            while (scanner.hasNext()) {
                String next = scanner.nextLine();
                String[] name_add = next.split(" - ");

                if (name_add.length != 2)
                    throw new IllegalArgumentException("Wrong format: " + next);

                String[] add_num = name_add[1].trim().split(" ");
                if (add_num.length != 2)
                    throw new IllegalArgumentException("Wrong format: " + next);

                String name = name_add[0].trim();
                String address = add_num[0];
                int number = Integer.parseInt(add_num[1]);

                Map<Integer, Set<String>> numb_names_map = treeMultiMap.get(address);

                if (numb_names_map != null) {
                    Set<String> names = numb_names_map.get(number);

                    if (names != null)
                        names.add(name);
                    else {
                        Set<String> firstSet = new TreeSet<>();
                        firstSet.add(name);
                        numb_names_map.put(number, firstSet);
                    }
                } else {
                    Set<String> firstSet = new TreeSet<>();
                    firstSet.add(name);
                    Map<Integer, Set<String>> firstMap = new HashMap<>();
                    firstMap.put(number, firstSet);
                    treeMultiMap.put(address, firstMap);
                }
            }
            scanner.close();

            Writer writer = new FileWriter(outputFile);

            for (String currentAddress : treeMultiMap.keySet()) {
                Map<Integer, Set<String>> num_name_map = treeMultiMap.get(currentAddress);

                for (Integer currentNumber : num_name_map.keySet()) {
                    Iterator<String> currentNameIter = num_name_map.get(currentNumber).iterator();
        
                    writer.write(currentAddress + " " + currentNumber + " - " + currentNameIter.next());

                    while (currentNameIter.hasNext())
                        writer.write(", " + currentNameIter.next());

                    writer.write('\n');
                }
            }

            writer.close();
        }

        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Сортировка температур
     *
     * Средняя
     * (Модифицированная задача с сайта acmp.ru)
     *
     * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
     * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
     * Например:
     *
     * 24.7
     * -12.6
     * 121.3
     * -98.4
     * 99.5
     * -12.6
     * 11.0
     *
     * Количество строк в файле может достигать ста миллионов.
     * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
     * Повторяющиеся строки сохранить. Например:
     *
     * -98.4
     * -12.6
     * -12.6
     * 11.0
     * 24.7
     * 99.5
     * 121.3
     */
    static public void sortTemperatures(String inputName, String outputName) {
        List<Integer> tempList = new ArrayList<>();
        File inputFile = new File(inputName);
        File outputFile = new File(outputName);

        try {
            Scanner scanner = new Scanner(inputFile);
            while (scanner.hasNext())
                tempList.add(Integer.parseInt(scanner.nextLine()));
            scanner.close();

            Collections.sort(tempList);

            Writer writer = new FileWriter(outputFile);
            for (int el : tempList)
                writer.write(el + '\n');
            writer.close();
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Сортировка последовательности
     *
     * Средняя
     * (Задача взята с сайта acmp.ru)
     *
     * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
     *
     * 1
     * 2
     * 3
     * 2
     * 3
     * 1
     * 2
     *
     * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
     * а если таких чисел несколько, то найти минимальное из них,
     * и после этого переместить все такие числа в конец заданной последовательности.
     * Порядок расположения остальных чисел должен остаться без изменения.
     *
     * 1
     * 3
     * 3
     * 1
     * 2
     * 2
     * 2
     */
    static public void sortSequence(String inputName, String outputName) {
        throw new NotImplementedError();
    }

    /**
     * Соединить два отсортированных массива в один
     *
     * Простая
     *
     * Задан отсортированный массив first и второй массив second,
     * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
     * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
     *
     * first = [4 9 15 20 28]
     * second = [null null null null null 1 3 9 13 18 23]
     *
     * Результат: second = [1 3 4 9 9 13 15 20 23 28]
     */
    static <T extends Comparable<T>> void mergeArrays(T[] first, T[] second) {
        throw new NotImplementedError();
    }
}
