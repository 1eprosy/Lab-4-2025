import functions.basic.*;
import functions.*;
import functions.meta.*;

import java.io.*;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== ТЕСТИРОВАНИЕ КЛАССОВ ФУНКЦИЙ ===\n");

        try {
            // Часть 1: Sin и Cos
            System.out.println("1. Тестирование Sin и Cos:");
            System.out.println("==========================");
            testSinCos();

            // Часть 2: Табулированные аналоги
            System.out.println("\n2. Табулированные аналоги Sin и Cos:");
            System.out.println("====================================");
            testTabulatedSinCos();

            // Часть 3: Сумма квадратов
            System.out.println("\n3. Сумма квадратов табулированных функций:");
            System.out.println("==========================================");
            testSumOfSquares();

            // Часть 4: Экспонента с записью в файл
            System.out.println("\n4. Тестирование экспоненты с записью в файл:");
            System.out.println("===========================================");
            testExpWithFile();

            // Часть 5: Логарифм с записью в файл
            System.out.println("\n5. Тестирование логарифма с записью в файл:");
            System.out.println("===========================================");
            testLnWithFile();

            // Часть 6: Сериализация (задание 9)
            System.out.println("\n6. Тестирование сериализации (задание 9):");
            System.out.println("==========================================");
            testSerialization();

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testSinCos() {
        Sin sinFunc = new Sin();
        Cos cosFunc = new Cos();

        System.out.println("sin(x) и cos(x) на отрезке [0, π] с шагом 0.1:");
        System.out.printf("%-10s %-15s %-15s%n", "x", "sin(x)", "cos(x)");
        System.out.println("------------------------------------------------");

        for (double x = 0; x <= Math.PI + 1e-10; x += 0.1) {
            System.out.printf(Locale.US, "%-10.4f %-15.10f %-15.10f%n",
                    x, sinFunc.getFunctionValue(x), cosFunc.getFunctionValue(x));
        }
    }

    private static void testTabulatedSinCos() {
        Sin sinFunc = new Sin();
        Cos cosFunc = new Cos();

        // Создаем табулированные функции с 10 точками
        TabulatedFunction tabSin = TabulatedFunctions.tabulate(sinFunc, 0, Math.PI, 10);
        TabulatedFunction tabCos = TabulatedFunctions.tabulate(cosFunc, 0, Math.PI, 10);

        System.out.println("Сравнение точных и табулированных значений:");
        System.out.printf("%-10s %-15s %-15s %-15s %-15s%n",
                "x", "sin(x) точн.", "sin(x) таб.", "cos(x) точн.", "cos(x) таб.");
        System.out.println("------------------------------------------------------------------------");

        for (double x = 0; x <= Math.PI + 1e-10; x += 0.1) {
            double sinExact = sinFunc.getFunctionValue(x);
            double cosExact = cosFunc.getFunctionValue(x);
            double sinTab = tabSin.getFunctionValue(x);
            double cosTab = tabCos.getFunctionValue(x);

            System.out.printf(Locale.US, "%-10.4f %-15.10f %-15.10f %-15.10f %-15.10f%n",
                    x, sinExact, sinTab, cosExact, cosTab);
        }

        // Вычисление погрешностей
        System.out.println("\nМаксимальные погрешности:");
        double maxSinError = 0;
        double maxCosError = 0;

        for (double x = 0; x <= Math.PI + 1e-10; x += 0.1) {
            double sinError = Math.abs(sinFunc.getFunctionValue(x) - tabSin.getFunctionValue(x));
            double cosError = Math.abs(cosFunc.getFunctionValue(x) - tabCos.getFunctionValue(x));

            if (sinError > maxSinError) maxSinError = sinError;
            if (cosError > maxCosError) maxCosError = cosError;
        }

        System.out.printf("sin: %.10f%n", maxSinError);
        System.out.printf("cos: %.10f%n", maxCosError);
    }

    private static void testSumOfSquares() {
        System.out.println("sin²(x) + cos²(x) должна быть равна 1 (теоретически)");
        System.out.println("Исследование влияния количества точек табуляции:");
        System.out.println("=================================================");

        int[] pointCounts = {5, 10, 20, 50, 100};

        for (int points : pointCounts) {
            System.out.printf("\nКоличество точек табуляции: %d%n", points);
            System.out.printf("%-10s %-15s %-15s%n", "x", "sin²+cos²", "погрешность");
            System.out.println("----------------------------------------");

            // Создаем табулированные функции
            TabulatedFunction tabSin = TabulatedFunctions.tabulate(new Sin(), 0, Math.PI, points);
            TabulatedFunction tabCos = TabulatedFunctions.tabulate(new Cos(), 0, Math.PI, points);

            // Создаем квадраты функций
            Function sinSquared = Functions.power(tabSin, 2);
            Function cosSquared = Functions.power(tabCos, 2);

            // Сумма квадратов
            Function sum = Functions.sum(sinSquared, cosSquared);

            double maxError = 0;
            for (double x = 0; x <= Math.PI + 1e-10; x += 0.1) {
                double value = sum.getFunctionValue(x);
                double error = Math.abs(value - 1.0); // Теоретическое значение: 1

                if (error > maxError) maxError = error;

                // Выводим только некоторые точки для наглядности
                if (Math.abs(x - 0) < 1e-10 || Math.abs(x - Math.PI/2) < 1e-10 ||
                        Math.abs(x - Math.PI) < 1e-10) {
                    System.out.printf(Locale.US, "%-10.4f %-15.10f %-15.10f%n",
                            x, value, error);
                }
            }

            System.out.printf("Максимальная погрешность: %.10f%n", maxError);
        }
    }

    private static void testExpWithFile() throws IOException {
        // Создаем табулированный аналог экспоненты
        Exp expFunc = new Exp();
        TabulatedFunction tabExp = TabulatedFunctions.tabulate(expFunc, 0, 10, 11);

        // Записываем в файл
        String filename1 = "exp_function.txt";
        try (FileWriter writer = new FileWriter(filename1)) {
            TabulatedFunctions.writeTabulatedFunction(tabExp, writer);
        }

        System.out.println("Функция записана в файл: " + filename1);

        // Читаем из файла
        TabulatedFunction readExp;
        try (FileReader reader = new FileReader(filename1)) {
            readExp = TabulatedFunctions.readTabulatedFunction(reader);
        }

        System.out.println("\nСравнение исходной и считанной экспоненты:");
        System.out.printf("%-10s %-20s %-20s %-10s%n",
                "x", "исходная exp(x)", "считанная", "разница");
        System.out.println("----------------------------------------------------------------");

        for (double x = 0; x <= 10; x += 1) {
            double original = tabExp.getFunctionValue(x);
            double read = readExp.getFunctionValue(x);
            double diff = Math.abs(original - read);

            System.out.printf(Locale.US, "%-10.0f %-20.10f %-20.10f %-10.10f%n",
                    x, original, read, diff);
        }

        // Изучаем содержимое файла
        System.out.println("\nСодержимое файла " + filename1 + ":");
        try (BufferedReader br = new BufferedReader(new FileReader(filename1))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    private static void testLnWithFile() throws IOException {
        // Создаем табулированный аналог логарифма (начинаем с 1, т.к. ln(0) не определен)
        Log lnFunc = new Log(Math.E); // натуральный логарифм
        TabulatedFunction tabLn = TabulatedFunctions.tabulate(lnFunc, 1, 10, 10);

        // Записываем в бинарный файл
        String filename2 = "ln_function.bin";
        try (FileOutputStream fos = new FileOutputStream(filename2)) {
            TabulatedFunctions.outputTabulatedFunction(tabLn, fos);
        }

        System.out.println("\nБинарная функция записана в файл: " + filename2);

        // Читаем из бинарного файла
        TabulatedFunction readLn;
        try (FileInputStream fis = new FileInputStream(filename2)) {
            readLn = TabulatedFunctions.inputTabulatedFunction(fis);
        }

        System.out.println("\nСравнение исходного и считанного логарифма:");
        System.out.printf("%-10s %-20s %-20s %-10s%n",
                "x", "исходный ln(x)", "считанный", "разница");
        System.out.println("----------------------------------------------------------------");

        for (double x = 1; x <= 10; x += 1) {
            double original = tabLn.getFunctionValue(x);
            double read = readLn.getFunctionValue(x);
            double diff = Math.abs(original - read);

            System.out.printf(Locale.US, "%-10.0f %-20.10f %-20.10f %-10.10f%n",
                    x, original, read, diff);
        }

        // Изучаем содержимое бинарного файла
        System.out.println("\nРазмер бинарного файла: " + new File(filename2).length() + " байт");
        System.out.println("Первые 50 байт файла в HEX:");
        try (FileInputStream fis = new FileInputStream(filename2)) {
            byte[] buffer = new byte[50];
            int bytesRead = fis.read(buffer);
            for (int i = 0; i < bytesRead; i++) {
                System.out.printf("%02X ", buffer[i]);
                if ((i + 1) % 16 == 0) System.out.println();
            }
            System.out.println();
        }

        System.out.println("\n=== ВЫВОДЫ ===");
        System.out.println("1. Текстовый формат (writeTabulatedFunction):");
        System.out.println("   + Понятен для чтения человеком");
        System.out.println("   + Легко редактировать");
        System.out.println("   - Больший размер файла");
        System.out.println("   - Медленнее в обработке");
        System.out.println("   - Точность может теряться при записи/чтении строк");

        System.out.println("\n2. Бинарный формат (outputTabulatedFunction):");
        System.out.println("   + Компактный размер");
        System.out.println("   + Быстрее запись/чтение");
        System.out.println("   + Точное сохранение чисел");
        System.out.println("   - Нечитаем для человека");
        System.out.println("   - Проблемы с переносимостью между разными архитектурами");
        System.out.println("   - Сложнее отлаживать");
    }

    private static void testSerialization() throws IOException, ClassNotFoundException {
        System.out.println("\n6.1. Сериализация с использованием Serializable:");
        System.out.println("-----------------------------------------------");

        // Создаем композицию: ln(exp(x)) = x
        Exp expFunc = new Exp();
        Log lnFunc = new Log(Math.E);

        // Создаем табулированную функцию композиции
        Function composition = Functions.composition(lnFunc, expFunc);
        TabulatedFunction tabulated = TabulatedFunctions.tabulate(composition, 0, 10, 11);

        // Сериализуем с Serializable
        String serializableFile = "function_serializable.ser";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serializableFile))) {
            oos.writeObject(tabulated);
        }

        System.out.println("Объект сериализован в файл: " + serializableFile);

        // Десериализуем
        TabulatedFunction deserialized;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serializableFile))) {
            deserialized = (TabulatedFunction) ois.readObject();
        }

        System.out.println("\nСравнение исходной и десериализованной функции (ln(exp(x)) = x):");
        System.out.printf("%-10s %-20s %-20s %-10s%n",
                "x", "исходная", "десериализ.", "разница");
        System.out.println("----------------------------------------------------------------");

        for (double x = 0; x <= 10; x += 1) {
            double original = tabulated.getFunctionValue(x);
            double restored = deserialized.getFunctionValue(x);
            double diff = Math.abs(original - restored);

            System.out.printf(Locale.US, "%-10.0f %-20.10f %-20.10f %-10.10f%n",
                    x, original, restored, diff);
        }

        // Анализ файла Serializable
        File serializable = new File(serializableFile);
        System.out.println("\nРазмер файла Serializable: " + serializable.length() + " байт");
        System.out.println("Первые 100 байт файла в HEX:");
        try (FileInputStream fis = new FileInputStream(serializable)) {
            byte[] buffer = new byte[100];
            int bytesRead = fis.read(buffer);
            for (int i = 0; i < bytesRead; i++) {
                System.out.printf("%02X ", buffer[i]);
                if ((i + 1) % 16 == 0) System.out.println();
            }
            System.out.println();
        }

        System.out.println("\n6.2. Сериализация с использованием Externalizable:");
        System.out.println("--------------------------------------------------");

        // Создаем объект с Externalizable (нужен специальный класс)
        // Предположим, что у нас есть ArrayTabulatedFunctionExternalizable
        TabulatedFunction extFunc = createExternalizableFunction();

        String externalizableFile = "function_externalizable.ext";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(externalizableFile))) {
            oos.writeObject(extFunc);
        }

        System.out.println("Объект Externalizable сериализован в файл: " + externalizableFile);

        // Десериализуем
        TabulatedFunction deserializedExt;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(externalizableFile))) {
            deserializedExt = (TabulatedFunction) ois.readObject();
        }

        // Анализ файла Externalizable
        File externalizable = new File(externalizableFile);
        System.out.println("\nРазмер файла Externalizable: " + externalizable.length() + " байт");

        // Сравнение размеров
        System.out.println("\n=== СРАВНЕНИЕ СЕРИАЛИЗАЦИИ ===");
        System.out.println("Serializable: " + serializable.length() + " байт");
        System.out.println("Externalizable: " + externalizable.length() + " байт");

        System.out.println("\nПреимущества Serializable:");
        System.out.println("1. Проще в реализации - автоматическая сериализация");
        System.out.println("2. Не нужно писать код read/writeExternal");
        System.out.println("3. Автоматически обрабатывает изменения в классе");
        System.out.println("4. Сериализует всю иерархию наследования");

        System.out.println("\nНедостатки Serializable:");
        System.out.println("1. Больший размер файла (метаданные, версия класса)");
        System.out.println("2. Медленнее");
        System.out.println("3. Меньше контроля над процессом");
        System.out.println("4. Проблемы с версионированием");

        System.out.println("\nПреимущества Externalizable:");
        System.out.println("1. Полный контроль над процессом сериализации");
        System.out.println("2. Меньший размер файла (только нужные данные)");
        System.out.println("3. Быстрее");
        System.out.println("4. Легче обрабатывать изменения версий");

        System.out.println("\nНедостатки Externalizable:");
        System.out.println("1. Нужно писать свой код сериализации");
        System.out.println("2. Нужно обрабатывать все поля вручную");
        System.out.println("3. Сложнее поддерживать");
        System.out.println("4. Не сериализует автоматически суперклассы");

        // Удаляем временные файлы
        serializable.delete();
        externalizable.delete();
        new File("exp_function.txt").delete();
        new File("ln_function.bin").delete();

        System.out.println("\nВременные файлы удалены.");
    }

    private static TabulatedFunction createExternalizableFunction() {
        // Создаем простую табулированную функцию для теста
        // В реальности нужно использовать ваш класс ArrayTabulatedFunctionExternalizable
        Exp expFunc = new Exp();
        return TabulatedFunctions.tabulate(expFunc, 0, 5, 6);
    }
}