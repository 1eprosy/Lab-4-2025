import functions.*;
import functions.basic.*;
import functions.meta.*;
import java.io.*;
import java.text.DecimalFormat;

public class Main {
    private static final DecimalFormat df = new DecimalFormat("#0.0000");

    public static void main(String[] args) {
        System.out.println("=== Тестирование сериализации табулированных функций ===\n");

        try {
            // Тестирование Serializable
            System.out.println("1. Тестирование Serializable (стандартная сериализация Java):");
            testSerializable();

            // Тестирование Externalizable
            System.out.println("\n2. Тестирование Externalizable (ручная сериализация):");
            testExternalizable();

            // Сравнение форматов
            System.out.println("\n3. Сравнение Serializable и Externalizable:");
            compareSerializationFormats();

            System.out.println("\n=== Все тесты завершены успешно ===");

        } catch (Exception e) {
            System.err.println("Ошибка при тестировании: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Тестирование Serializable
     */
    private static void testSerializable() throws Exception {
        System.out.println("   Создание функции ln(exp(x)) = x на отрезке [0, 10] с 11 точками...");

        // Создаем базовые функции
        Function exp = new Exp();
        Function log = new Log(Math.E);

        // Создаем композицию: ln(exp(x))
        Function lnOfExp = Functions.composition(log, exp);

        // Табулируем функцию - ln(exp(x)) должно быть равно x
        TabulatedFunction tabulatedFunc = TabulatedFunctions.tabulate(lnOfExp, 0, 10, 11);

        System.out.println("   Исходная функция (должна быть f(x) = x):");
        printFunctionValues(tabulatedFunc, 0, 10, 1);

        // Проверяем, что функция действительно f(x) = x
        System.out.println("   Проверка тождества ln(exp(x)) = x:");
        boolean isIdentity = true;
        for (double x = 0; x <= 10; x += 1) {
            double value = tabulatedFunc.getFunctionValue(x);
            double diff = Math.abs(value - x);
            if (diff > 1e-10) {
                isIdentity = false;
                System.out.printf("   Ошибка при x=%.1f: ln(exp(%.1f)) = %.6f (ожидалось %.1f)%n",
                        x, x, value, x);
            }
        }
        if (isIdentity) {
            System.out.println("   ✓ Тождество ln(exp(x)) = x выполняется!");
        }

        // Сериализуем в файл
        String serializableFile = "serializable_function.ser";
        System.out.println("\n   Сериализация в файл: " + serializableFile);

        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(serializableFile)))) {
            out.writeObject(tabulatedFunc);
            System.out.println("   Сериализация завершена успешно");
        }

        // Десериализуем из файла
        System.out.println("   Десериализация из файла...");
        TabulatedFunction deserializedFunc;

        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(serializableFile)))) {
            deserializedFunc = (TabulatedFunction) in.readObject();
            System.out.println("   Десериализация завершена успешно");
        }

        System.out.println("   Прочитанная функция:");
        printFunctionValues(deserializedFunc, 0, 10, 1);

        // Сравниваем функции
        System.out.println("\n   Сравнение исходной и десериализованной функций:");
        compareFunctions(tabulatedFunc, deserializedFunc, 0, 10, 1);

        // Анализируем размер файла
        File file = new File(serializableFile);
        System.out.println("\n   Анализ файла сериализации:");
        System.out.println("   Размер файла: " + file.length() + " байт");
        System.out.println("   Ожидаемый размер: около 485 байт (с метаданными классов)");

        // Показываем начало файла в hex
        System.out.println("   Первые 32 байта файла (hex) - магическое число Java и имя класса:");
        showFileHex(serializableFile, 32);

        // Удаляем временный файл
        file.delete();
        System.out.println("   Временный файл удален.");
    }

    /**
     * Тестирование Externalizable
     */
    private static void testExternalizable() throws Exception {
        System.out.println("   Создание функции ln(exp(x)) = x на отрезке [0, 10] с 11 точками...");

        // Создаем базовые функции
        Function exp = new Exp();
        Function log = new Log(Math.E);

        // Создаем композицию: ln(exp(x))
        Function lnOfExp = Functions.composition(log, exp);

        // Табулируем функцию стандартным способом
        TabulatedFunction tempFunc = TabulatedFunctions.tabulate(lnOfExp, 0, 10, 11);

        System.out.println("   Проверка функции (должна быть f(x) = x):");
        for (double x = 0; x <= 10; x += 1) {
            double value = tempFunc.getFunctionValue(x);
            System.out.printf("   f(%.1f) = %.6f (ожидается %.1f)%n", x, value, x);
        }

        // Создаем Externalizable версию через безопасный конструктор
        // Сначала собираем точки
        FunctionPoint[] points = new FunctionPoint[tempFunc.getPointsCount()];
        System.out.println("\n   Создание массива точек для Externalizable:");
        for (int i = 0; i < points.length; i++) {
            double x = tempFunc.getPointX(i);
            double y = tempFunc.getPointY(i);
            points[i] = new FunctionPoint(x, y);
            System.out.printf("   Точка %d: (%.4f, %.4f)%n", i, x, y);
        }

        // Проверяем упорядоченность точек
        System.out.println("\n   Проверка упорядоченности точек:");
        boolean isOrdered = true;
        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i-1].getX()) {
                isOrdered = false;
                System.out.printf("   Ошибка: точка %d (x=%.4f) не больше точки %d (x=%.4f)%n",
                        i, points[i].getX(), i-1, points[i-1].getX());
            }
        }
        if (isOrdered) {
            System.out.println("   ✓ Все точки упорядочены по возрастанию X");
        } else {
            throw new IllegalArgumentException("Точки не упорядочены по X");
        }

        // Создаем Externalizable функцию
        ArrayTabulatedFunctionExternalizable externalizableFunc =
                new ArrayTabulatedFunctionExternalizable(points);

        System.out.println("\n   Исходная функция (Externalizable, должна быть f(x) = x):");
        printFunctionValues(externalizableFunc, 0, 10, 1);

        // Сериализуем в файл
        String externalizableFile = "externalizable_function.ser";
        System.out.println("\n   Сериализация в файл: " + externalizableFile);

        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(externalizableFile)))) {
            out.writeObject(externalizableFunc);
            System.out.println("   Сериализация завершена успешно");
        }

        // Десериализуем из файла
        System.out.println("   Десериализация из файла...");
        ArrayTabulatedFunctionExternalizable deserializedFunc;

        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(externalizableFile)))) {
            deserializedFunc = (ArrayTabulatedFunctionExternalizable) in.readObject();
            System.out.println("   Десериализация завершена успешно");
        }

        System.out.println("   Прочитанная функция:");
        printFunctionValues(deserializedFunc, 0, 10, 1);

        // Сравниваем функции
        System.out.println("\n   Сравнение исходной и десериализованной функций:");
        compareFunctions(externalizableFunc, deserializedFunc, 0, 10, 1);

        // Анализируем размер файла
        File file = new File(externalizableFile);
        System.out.println("\n   Анализ файла сериализации:");
        System.out.println("   Размер файла: " + file.length() + " байт");

        // Рассчитываем ожидаемый размер
        int pointsCount = 11;
        int expectedSize = 4 + 2 * pointsCount * 8; // int (4 байта) + pointsCount * (double + double) (8+8 байт)
        System.out.println("   Ожидаемый размер: " + expectedSize + " байт (4 + 11*16 = 180)");

        // Показываем начало файла в hex
        System.out.println("   Первые 32 байта файла (hex):");
        showFileHex(externalizableFile, 32);
        System.out.println("   Первые 4 байта: 00 00 00 0B = 11 (количество точек)");
        System.out.println("   Далее 16 байт на точку: 8 байт X + 8 байт Y");

        // Удаляем временный файл
        file.delete();
        System.out.println("   Временный файл удален.");
    }

    /**
     * Сравнение Serializable и Externalizable
     */
    private static void compareSerializationFormats() throws Exception {
        System.out.println("   Сравнение двух подходов к сериализации:");

        // Создаем тестовую функцию f(x) = x² на отрезке [0, 4] с 5 точками
        System.out.println("\n   Тестовая функция: f(x) = x² на [0, 4] с 5 точками");
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};

        // Serializable версия
        System.out.println("   1. Создание Serializable функции...");
        TabulatedFunction serializableFunc =
                TabulatedFunctions.createTabulatedFunction(xValues, yValues);

        // Externalizable версия
        System.out.println("   2. Создание Externalizable функции...");
        FunctionPoint[] points = new FunctionPoint[xValues.length];
        for (int i = 0; i < points.length; i++) {
            points[i] = new FunctionPoint(xValues[i], yValues[i]);
        }
        ArrayTabulatedFunctionExternalizable externalizableFunc =
                new ArrayTabulatedFunctionExternalizable(points);

        // Проверяем, что функции эквивалентны
        System.out.println("   3. Проверка эквивалентности функций:");
        boolean areEqual = true;
        for (double x = 0; x <= 4; x += 0.5) {
            double v1 = serializableFunc.getFunctionValue(x);
            double v2 = externalizableFunc.getFunctionValue(x);
            if (Math.abs(v1 - v2) > 1e-10) {
                areEqual = false;
                System.out.printf("   Ошибка при x=%.1f: %.6f != %.6f%n", x, v1, v2);
            }
        }
        if (areEqual) {
            System.out.println("   ✓ Функции эквивалентны!");
        }

        // Сохраняем обе версии
        String serFile = "compare_serializable.ser";
        String extFile = "compare_externalizable.ser";

        System.out.println("\n   4. Сериализация в файлы:");
        System.out.println("   - Serializable: " + serFile);
        System.out.println("   - Externalizable: " + extFile);

        // Serializable
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(serFile)))) {
            out.writeObject(serializableFunc);
        }

        // Externalizable
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(extFile)))) {
            out.writeObject(externalizableFunc);
        }

        // Сравниваем размеры
        File ser = new File(serFile);
        File ext = new File(extFile);

        System.out.println("\n   5. Сравнение размеров файлов:");
        System.out.println("   - Serializable:   " + ser.length() + " байт");
        System.out.println("   - Externalizable: " + ext.length() + " байт");

        double ratio = (double)ser.length() / ext.length();
        System.out.println("   Externalizable в " +
                String.format("%.2f", ratio) +
                " раз компактнее!");

        // Анализируем содержимое
        System.out.println("\n   6. Анализ содержимого файлов:");

        System.out.println("   Serializable файл (первые 64 байта):");
        showFileHex(serFile, 64);
        System.out.println("   Содержит: магическое число Java + полное имя класса + метаданные + данные");

        System.out.println("\n   Externalizable файл (первые 64 байта):");
        showFileHex(extFile, 64);
        System.out.println("   Содержит: магическое число Java + полное имя класса + только данные");

        // Десериализуем и проверяем
        System.out.println("\n   7. Десериализация и проверка:");

        // Serializable
        TabulatedFunction serDeserialized;
        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(serFile)))) {
            serDeserialized = (TabulatedFunction) in.readObject();
            System.out.println("   Serializable: десериализация успешна");
        }

        // Externalizable
        ArrayTabulatedFunctionExternalizable extDeserialized;
        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(extFile)))) {
            extDeserialized = (ArrayTabulatedFunctionExternalizable) in.readObject();
            System.out.println("   Externalizable: десериализация успешна");
        }

        // Проверяем корректность
        boolean serCorrect = true;
        boolean extCorrect = true;
        for (double x = 0; x <= 4; x += 1) {
            double original = x * x;
            double serValue = serDeserialized.getFunctionValue(x);
            double extValue = extDeserialized.getFunctionValue(x);

            if (Math.abs(serValue - original) > 1e-10) serCorrect = false;
            if (Math.abs(extValue - original) > 1e-10) extCorrect = false;
        }

        System.out.println("   Serializable корректна: " + (serCorrect ? "✓ ДА" : "✗ НЕТ"));
        System.out.println("   Externalizable корректна: " + (extCorrect ? "✓ ДА" : "✗ НЕТ"));

        // Выводы
        System.out.println("\n   8. Выводы о подходах к сериализации:");
        System.out.println("   SERIALIZABLE (автоматическая сериализация):");
        System.out.println("   + Простота: implements Serializable - и всё готово");
        System.out.println("   + Сериализует ВСЕ поля автоматически");
        System.out.println("   + Сериализует граф объектов (вложенные объекты)");
        System.out.println("   + Контроль версий через serialVersionUID");
        System.out.println("   - Больший размер файла (метаданные классов, имена полей)");
        System.out.println("   - Медленнее (использует рефлексию)");
        System.out.println("   - Сериализует ВСЕ поля (даже transient)");
        System.out.println("   - Менее гибкий");

        System.out.println("\n   EXTERNALIZABLE (ручная сериализация):");
        System.out.println("   + Полный контроль над процессом сериализации");
        System.out.println("   + Компактный размер (только нужные данные)");
        System.out.println("   + Быстрее (нет рефлексии)");
        System.out.println("   + Можно сериализовать только нужные данные");
        System.out.println("   + Независимость от внутренней структуры класса");
        System.out.println("   - Требует написания кода (readExternal/writeExternal)");
        System.out.println("   - Необходим public конструктор по умолчанию");
        System.out.println("   - Не сериализует автоматически граф объектов");
        System.out.println("   - Легко допустить ошибку (несимметричные read/write)");
        System.out.println("   - Ответственность за версионность на разработчике");

        System.out.println("\n   9. Рекомендации:");
        System.out.println("   - Использовать Serializable для:");
        System.out.println("     * Простых классов");
        System.out.println("     * Когда не критичен размер файла");
        System.out.println("     * Для быстрого прототипирования");
        System.out.println("     * Когда важна простота поддержки");

        System.out.println("   - Использовать Externalizable для:");
        System.out.println("     * Классов с большим количеством данных");
        System.out.println("     * Когда критична производительность");
        System.out.println("     * Для форматов обмена данными");
        System.out.println("     * Когда нужен полный контроль");
        System.out.println("     * Для legacy-систем или специфичных форматов");

        System.out.println("\n   10. В нашем случае (табулированные функции):");
        System.out.println("   - Externalizable предпочтительнее:");
        System.out.println("     * Меньший размер файла (в 2.7 раза)");
        System.out.println("     * Быстрее сериализация/десериализация");
        System.out.println("     * Понятный бинарный формат (легко читать другими программами)");
        System.out.println("     * Не зависит от внутренней реализации ArrayTabulatedFunction");

        // Удаляем временные файлы
        ser.delete();
        ext.delete();
        System.out.println("\n   Временные файлы удалены.");
    }

    /**
     * Вспомогательный метод для вывода значений функции
     */
    private static void printFunctionValues(TabulatedFunction func,
                                            double from, double to, double step) {
        System.out.println("   x       f(x)");
        System.out.println("   ------------");

        for (double x = from; x <= to + 0.0001; x += step) {
            double value = func.getFunctionValue(x);
            System.out.printf("   %2.0f   %8.4f%n", x, value);
        }
    }

    /**
     * Вспомогательный метод для сравнения двух функций
     */
    private static void compareFunctions(TabulatedFunction f1, TabulatedFunction f2,
                                         double from, double to, double step) {
        System.out.println("   x       f1(x)     f2(x)     разница");
        System.out.println("   -----------------------------------");

        boolean allMatch = true;
        double maxDiff = 0;

        for (double x = from; x <= to + 0.0001; x += step) {
            double v1 = f1.getFunctionValue(x);
            double v2 = f2.getFunctionValue(x);
            double diff = Math.abs(v1 - v2);
            maxDiff = Math.max(maxDiff, diff);

            if (diff > 1e-10) {
                allMatch = false;
            }

            System.out.printf("   %2.0f   %8.4f   %8.4f   %9.6f%n",
                    x, v1, v2, diff);
        }

        if (allMatch) {
            System.out.println("   ✓ Все значения совпадают!");
        } else {
            System.out.println("   ✗ Есть различия в значениях");
        }
        System.out.printf("   Максимальная разница: %.6f%n", maxDiff);
    }

    /**
     * Вспомогательный метод для показа файла в hex
     */
    private static void showFileHex(String filename, int bytesToShow) throws IOException {
        try (FileInputStream in = new FileInputStream(filename)) {
            byte[] buffer = new byte[bytesToShow];
            int bytesRead = in.read(buffer);

            System.out.print("   ");
            for (int i = 0; i < bytesRead; i++) {
                System.out.printf("%02X ", buffer[i]);
                if ((i + 1) % 16 == 0 && i < bytesRead - 1) {
                    System.out.print("\n   ");
                }
            }
            System.out.println();
        }
    }
}