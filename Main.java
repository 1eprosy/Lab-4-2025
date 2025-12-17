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

        // Создаем табулированный аналог ln(exp(x)) = x
        Exp expFunc = new Exp();
        Log lnFunc = new Log(Math.E);

        // Создаем композицию ln(exp(x)) = x
        Function composition = Functions.composition(lnFunc, expFunc);

        // Табулируем композицию - получим ArrayTabulatedFunction (Serializable)
        TabulatedFunction tabulated = TabulatedFunctions.tabulate(composition, 0, 10, 11);

        System.out.println("Создана функция ln(exp(x)) = x (теоретически):");
        for (double x = 0; x <= 10; x += 1) {
            System.out.printf("x=%.1f, значение=%.10f (теория: %.1f)%n",
                    x, tabulated.getFunctionValue(x), x);
        }

        // Сериализуем с Serializable
        String serializableFile = "function_serializable.ser";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serializableFile))) {
            oos.writeObject(tabulated);
        }

        System.out.println("\nОбъект ArrayTabulatedFunction сериализован в файл: " + serializableFile);
        System.out.println("Размер файла: " + new File(serializableFile).length() + " байт");

        // Десериализуем
        TabulatedFunction deserialized;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serializableFile))) {
            deserialized = (TabulatedFunction) ois.readObject();
        }

        System.out.println("\nСравнение исходной и десериализованной функции (ln(exp(x))):");
        System.out.printf("%-10s %-20s %-20s %-10s%n",
                "x", "исходная", "десериализ.", "разница");
        System.out.println("----------------------------------------------------------------");

        double maxDiffSerializable = 0;
        for (double x = 0; x <= 10; x += 1) {
            double original = tabulated.getFunctionValue(x);
            double restored = deserialized.getFunctionValue(x);
            double diff = Math.abs(original - restored);
            if (diff > maxDiffSerializable) maxDiffSerializable = diff;

            System.out.printf(Locale.US, "%-10.0f %-20.10f %-20.10f %-10.10f%n",
                    x, original, restored, diff);
        }
        System.out.printf("Максимальная разница: %.10f%n", maxDiffSerializable);

        // Смотрим содержимое файла Serializable
        System.out.println("\nПервые 50 байт файла Serializable в HEX:");
        try (FileInputStream fis = new FileInputStream(serializableFile)) {
            byte[] buffer = new byte[50];
            int bytesRead = fis.read(buffer);
            for (int i = 0; i < bytesRead; i++) {
                System.out.printf("%02X ", buffer[i]);
                if ((i + 1) % 16 == 0) System.out.println();
            }
            System.out.println();
        }

        System.out.println("\n6.2. Сериализация с использованием Externalizable:");
        System.out.println("--------------------------------------------------");

        // Создаем ArrayTabulatedFunctionExternalizable
        // Нужно создать массив FunctionPoint для конструктора
        FunctionPoint[] pointsArray = new FunctionPoint[11];
        for (int i = 0; i <= 10; i++) {
            double x = i; // 0, 1, 2, ..., 10
            double y = composition.getFunctionValue(x); // ln(exp(x)) = x
            pointsArray[i] = new FunctionPoint(x, y);
        }

        // Создаем ArrayTabulatedFunctionExternalizable через конструктор с массивом точек
        ArrayTabulatedFunctionExternalizable extFunc =
                new ArrayTabulatedFunctionExternalizable(pointsArray);

        String externalizableFile = "function_externalizable.ext";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(externalizableFile))) {
            oos.writeObject(extFunc);
        }

        System.out.println("Объект ArrayTabulatedFunctionExternalizable сериализован в файл: " + externalizableFile);
        System.out.println("Размер файла: " + new File(externalizableFile).length() + " байт");

        // Десериализуем
        ArrayTabulatedFunctionExternalizable deserializedExt;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(externalizableFile))) {
            deserializedExt = (ArrayTabulatedFunctionExternalizable) ois.readObject();
        }

        System.out.println("\nСравнение исходной и десериализованной функции Externalizable:");
        System.out.printf("%-10s %-20s %-20s %-10s%n",
                "x", "исходная", "десериализ.", "разница");
        System.out.println("----------------------------------------------------------------");

        double maxDiffExternalizable = 0;
        for (double x = 0; x <= 10; x += 1) {
            double original = extFunc.getFunctionValue(x);
            double restored = deserializedExt.getFunctionValue(x);
            double diff = Math.abs(original - restored);
            if (diff > maxDiffExternalizable) maxDiffExternalizable = diff;

            System.out.printf(Locale.US, "%-10.0f %-20.10f %-20.10f %-10.10f%n",
                    x, original, restored, diff);
        }
        System.out.printf("Максимальная разница: %.10f%n", maxDiffExternalizable);

        // Дополнительно: тестируем работу методов вашего класса
        System.out.println("\nДополнительное тестирование методов ArrayTabulatedFunctionExternalizable:");

        // Тест метода printFunction()
        System.out.println("\nВызов метода printFunction():");
        extFunc.printFunction();

        // Тест добавления/удаления точек (если поддерживается)
        System.out.println("\nТестирование работы с точками:");
        System.out.println("Исходное количество точек: " + extFunc.getPointsCount());
        System.out.printf("Область определения: [%.4f, %.4f]%n",
                extFunc.getLeftDomainBorder(), extFunc.getRightDomainBorder());

        // Смотрим содержимое файла Externalizable
        System.out.println("\nПервые 50 байт файла Externalizable в HEX:");
        try (FileInputStream fis = new FileInputStream(externalizableFile)) {
            byte[] buffer = new byte[50];
            int bytesRead = fis.read(buffer);
            for (int i = 0; i < bytesRead; i++) {
                System.out.printf("%02X ", buffer[i]);
                if ((i + 1) % 16 == 0) System.out.println();
            }
            System.out.println();
        }

        // Сравнение размеров
        System.out.println("\n=== СРАВНЕНИЕ СЕРИАЛИЗАЦИИ ===");
        System.out.println("Serializable файл (ArrayTabulatedFunction): " + new File(serializableFile).length() + " байт");
        System.out.println("Externalizable файл (ArrayTabulatedFunctionExternalizable): " + new File(externalizableFile).length() + " байт");
        System.out.println("Разница: " +
                Math.abs(new File(serializableFile).length() - new File(externalizableFile).length()) +
                " байт");

        System.out.println("\n=== АНАЛИЗ ФОРМАТОВ СЕРИАЛИЗАЦИИ ===");

        // Анализируем структуру файлов
        System.out.println("\nСтруктура Serializable файла:");
        System.out.println("- Заголовок сериализации (класс, версия, метаданные)");
        System.out.println("- Сериализация всех полей через рефлексию");
        System.out.println("- Дополнительная информация о версии класса");
        System.out.println("- Возможные служебные данные для восстановления ссылок");

        System.out.println("\nСтруктура Externalizable файла:");
        System.out.println("- Только данные, записанные в writeExternal():");
        System.out.println("  1. Количество точек (int, 4 байта)");
        System.out.println("  2. Для каждой точки:");
        System.out.println("     - координата x (double, 8 байт)");
        System.out.println("     - координата y (double, 8 байт)");
        System.out.println("- Нет служебных метаданных");
        System.out.println("- Минимально необходимый набор данных");

        System.out.println("\n=== ВЫВОДЫ ===");
        System.out.println("Преимущества Serializable (ArrayTabulatedFunction):");
        System.out.println("1. Проще в реализации - не нужно писать код сериализации");
        System.out.println("2. Автоматически сериализует все не-static, non-transient поля");
        System.out.println("3. Обрабатывает сложные графы объектов");
        System.out.println("4. Автоматическое управление версиями (serialVersionUID)");
        System.out.println("5. Совместимость с наследованием");

        System.out.println("\nНедостатки Serializable:");
        System.out.println("1. Больший размер файла (дополнительные метаданные)");
        System.out.println("2. Медленнее из-за рефлексии");
        System.out.println("3. Меньше контроля над процессом");
        System.out.println("4. Может сериализовать лишние данные");
        System.out.println("5. Проблемы с производительностью при больших объемах");

        System.out.println("\nПреимущества Externalizable (ArrayTabulatedFunctionExternalizable):");
        System.out.println("1. Полный контроль над процессом сериализации");
        System.out.println("2. Меньший размер файла (только нужные данные)");
        System.out.println("3. Быстрее (прямой доступ к данным, нет рефлексии)");
        System.out.println("4. Легче обрабатывать изменения версий класса");
        System.out.println("5. Возможность оптимизации формата хранения");
        System.out.println("6. Улучшенная производительность для частой сериализации");

        System.out.println("\nНедостатки Externalizable:");
        System.out.println("1. Нужно писать свой код для readExternal/writeExternal");
        System.out.println("2. Нужно вручную обрабатывать все поля");
        System.out.println("3. Сложнее поддерживать (при изменении класса нужно обновлять методы)");
        System.out.println("4. Не сериализует автоматически поля суперклассов");
        System.out.println("5. Ответственность за обработку ошибок лежит на разработчике");

        System.out.println("\nРекомендации для данного проекта:");
        System.out.println("1. Для табулированных функций Externalizable предпочтительнее:");
        System.out.println("   - Функции часто содержат много точек данных");
        System.out.println("   - Важна компактность хранения");
        System.out.println("   - Производительность при частой сериализации");
        System.out.println("2. Serializable лучше использовать для:");
        System.out.println("   - Простых объектов с малым объемом данных");
        System.out.println("   - Когда важна простота реализации");
        System.out.println("   - Для объектов с быстроменяющейся структурой");

        System.out.println("\nПример расчёта размера файла для 11 точек:");
        System.out.println("Externalizable: 4 байт (количество) + 11 × (8 + 8) = 180 байт");
        System.out.println("Serializable: 180 байт + метаданные ≈ 250-400 байт");

        // Удаляем временные файлы
        System.out.println("\nУдаление временных файлов:");
        boolean deleted1 = new File(serializableFile).delete();
        boolean deleted2 = new File(externalizableFile).delete();
        boolean deleted3 = new File("exp_function.txt").delete();
        boolean deleted4 = new File("ln_function.bin").delete();

        System.out.println("Serializable файл удален: " + (deleted1 ? "да" : "нет"));
        System.out.println("Externalizable файл удален: " + (deleted2 ? "да" : "нет"));
        System.out.println("Текстовый файл удален: " + (deleted3 ? "да" : "нет"));
        System.out.println("Бинарный файл удален: " + (deleted4 ? "да" : "нет"));
    }
}