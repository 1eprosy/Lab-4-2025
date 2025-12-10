package functions;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Утилитный класс для работы с табулированными функциями.
 * Содержит статические методы для создания, преобразования и сериализации табулированных функций.
 * Не может быть инстанциирован.
 */
public class TabulatedFunctions {

    /**
     * Приватный конструктор для предотвращения создания объектов класса.
     */
    private TabulatedFunctions() {
        throw new AssertionError("Нельзя создавать объекты утилитного класса TabulatedFunctions");
    }

    // ==================== Методы для создания табулированных функций ====================

    /**
     * Табулирует функцию на заданном отрезке с заданным количеством точек.
     *
     * @param function функция для табулирования
     * @param leftX левая граница отрезка табулирования
     * @param rightX правая граница отрезка табулирования
     * @param pointsCount количество точек табулирования (должно быть >= 2)
     * @return табулированная функция
     * @throws IllegalArgumentException если function равна null
     * @throws IllegalArgumentException если pointsCount < 2
     * @throws IllegalArgumentException если leftX >= rightX
     * @throws IllegalArgumentException если границы табулирования выходят за область определения функции
     */
    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        // Проверка входных параметров
        if (function == null) {
            throw new IllegalArgumentException("Функция не может быть null");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2. Получено: " + pointsCount);
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException(
                    "Левая граница должна быть меньше правой. Получено: leftX = " + leftX + ", rightX = " + rightX
            );
        }

        // Проверка, что границы табулирования входят в область определения функции
        if (leftX < function.getLeftDomainBorder()) {
            throw new IllegalArgumentException(
                    "Левая граница табулирования " + leftX +
                            " выходит за область определения функции [" +
                            function.getLeftDomainBorder() + ", " + function.getRightDomainBorder() + "]"
            );
        }
        if (rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException(
                    "Правая граница табулирования " + rightX +
                            " выходит за область определения функции [" +
                            function.getLeftDomainBorder() + ", " + function.getRightDomainBorder() + "]"
            );
        }

        // Создаем массив значений функции
        double[] values = new double[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            values[i] = function.getFunctionValue(x);
        }

        // Возвращаем табулированную функцию (используем ArrayTabulatedFunction по умолчанию)
        return new ArrayTabulatedFunction(leftX, rightX, values);
    }

    /**
     * Табулирует функцию на её полной области определения с заданным количеством точек.
     *
     * @param function функция для табулирования
     * @param pointsCount количество точек табулирования (должно быть >= 2)
     * @return табулированная функция
     * @throws IllegalArgumentException если function равна null
     * @throws IllegalArgumentException если pointsCount < 2
     */
    public static TabulatedFunction tabulate(Function function, int pointsCount) {
        if (function == null) {
            throw new IllegalArgumentException("Функция не может быть null");
        }

        return tabulate(function, function.getLeftDomainBorder(), function.getRightDomainBorder(), pointsCount);
    }

    /**
     * Создает табулированную функцию из массива точек.
     *
     * @param points массив точек функции
     * @return табулированная функция
     * @throws IllegalArgumentException если points равна null
     * @throws IllegalArgumentException если массив содержит меньше 2 точек
     */
    public static TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
        if (points == null) {
            throw new IllegalArgumentException("Массив точек не может быть null");
        }
        if (points.length < 2) {
            throw new IllegalArgumentException("Массив должен содержать не менее 2 точек. Получено: " + points.length);
        }

        // Проверяем, что все точки не null
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) {
                throw new IllegalArgumentException("Массив точек содержит null элемент в позиции " + i);
            }
        }

        // Проверяем упорядоченность по x
        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i-1].getX()) {
                throw new IllegalArgumentException(
                        "Точки должны быть строго упорядочены по x. " +
                                "Точка " + i + ": x = " + points[i].getX() +
                                " не больше точки " + (i-1) + ": x = " + points[i-1].getX()
                );
            }
        }

        return new ArrayTabulatedFunction(points);
    }

    /**
     * Создает табулированную функцию из массивов координат.
     *
     * @param xValues массив значений x
     * @param yValues массив значений y
     * @return табулированная функция
     * @throws IllegalArgumentException если любой из массивов равен null
     * @throws IllegalArgumentException если массивы имеют разную длину
     * @throws IllegalArgumentException если массивы содержат меньше 2 элементов
     */
    public static TabulatedFunction createTabulatedFunction(double[] xValues, double[] yValues) {
        if (xValues == null || yValues == null) {
            throw new IllegalArgumentException("Массивы координат не могут быть null");
        }
        if (xValues.length != yValues.length) {
            throw new IllegalArgumentException(
                    "Массивы x и y должны иметь одинаковую длину. " +
                            "x.length = " + xValues.length + ", y.length = " + yValues.length
            );
        }
        if (xValues.length < 2) {
            throw new IllegalArgumentException("Массивы должны содержать не менее 2 элементов. Получено: " + xValues.length);
        }

        // Создаем массив точек
        FunctionPoint[] points = new FunctionPoint[xValues.length];
        for (int i = 0; i < xValues.length; i++) {
            points[i] = new FunctionPoint(xValues[i], yValues[i]);
        }

        return createTabulatedFunction(points);
    }

    /**
     * Создает равномерно табулированную функцию.
     *
     * @param leftX левая граница области определения
     * @param rightX правая граница области определения
     * @param pointsCount количество точек
     * @return табулированная функция
     * @throws IllegalArgumentException если pointsCount < 2
     * @throws IllegalArgumentException если leftX >= rightX
     */
    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
        return new ArrayTabulatedFunction(leftX, rightX, pointsCount);
    }

    /**
     * Создает равномерно табулированную функцию с заданными значениями.
     *
     * @param leftX левая граница области определения
     * @param rightX правая граница области определения
     * @param yValues массив значений функции
     * @return табулированная функция
     * @throws IllegalArgumentException если yValues равна null
     * @throws IllegalArgumentException если yValues содержит меньше 2 элементов
     * @throws IllegalArgumentException если leftX >= rightX
     */
    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] yValues) {
        return new ArrayTabulatedFunction(leftX, rightX, yValues);
    }

    /**
     * Интерполирует значение функции в точке на основе табулированной функции.
     *
     * @param function табулированная функция
     * @param x точка для интерполяции
     * @return интерполированное значение
     * @throws IllegalArgumentException если function равна null
     */
    public static double interpolate(TabulatedFunction function, double x) {
        if (function == null) {
            throw new IllegalArgumentException("Функция не может быть null");
        }

        return function.getFunctionValue(x);
    }

    /**
     * Находит производную табулированной функции в точке.
     * Использует численное дифференцирование.
     *
     * @param function табулированная функция
     * @param x точка для вычисления производной
     * @param epsilon шаг для численного дифференцирования
     * @return приближенное значение производной
     * @throws IllegalArgumentException если function равна null
     * @throws IllegalArgumentException если epsilon <= 0
     */
    public static double derivative(TabulatedFunction function, double x, double epsilon) {
        if (function == null) {
            throw new IllegalArgumentException("Функция не может быть null");
        }
        if (epsilon <= 0) {
            throw new IllegalArgumentException("Шаг epsilon должен быть положительным. Получено: " + epsilon);
        }

        // Проверяем, что точка в области определения
        if (x < function.getLeftDomainBorder() || x > function.getRightDomainBorder()) {
            return Double.NaN;
        }

        // Используем центральную разность для более точного результата
        double x1 = x + epsilon;
        double x2 = x - epsilon;

        // Корректируем точки, если они выходят за границы
        if (x1 > function.getRightDomainBorder()) {
            x1 = function.getRightDomainBorder();
        }
        if (x2 < function.getLeftDomainBorder()) {
            x2 = function.getLeftDomainBorder();
        }

        double y1 = function.getFunctionValue(x1);
        double y2 = function.getFunctionValue(x2);

        if (Double.isNaN(y1) || Double.isNaN(y2)) {
            return Double.NaN;
        }

        return (y1 - y2) / (x1 - x2);
    }

    /**
     * Находит интеграл табулированной функции на отрезке [a, b].
     * Использует метод трапеций.
     *
     * @param function табулированная функция
     * @param a нижний предел интегрирования
     * @param b верхний предел интегрирования
     * @return приближенное значение интеграла
     * @throws IllegalArgumentException если function равна null
     * @throws IllegalArgumentException если a или b выходят за область определения
     */
    public static double integrate(TabulatedFunction function, double a, double b) {
        if (function == null) {
            throw new IllegalArgumentException("Функция не может быть null");
        }

        // Проверяем границы интегрирования
        if (a < function.getLeftDomainBorder() || a > function.getRightDomainBorder() ||
                b < function.getLeftDomainBorder() || b > function.getRightDomainBorder()) {
            throw new IllegalArgumentException(
                    "Границы интегрирования выходят за область определения функции [" +
                            function.getLeftDomainBorder() + ", " + function.getRightDomainBorder() + "]"
            );
        }

        // Если границы совпадают, интеграл равен 0
        if (Math.abs(a - b) < 1e-10) {
            return 0.0;
        }

        // Меняем пределы, если a > b
        if (a > b) {
            double temp = a;
            a = b;
            b = temp;
        }

        // Используем метод трапеций с шагом, основанным на точках табуляции
        int pointsCount = function.getPointsCount();
        double result = 0.0;

        // Находим индексы точек, между которыми находятся a и b
        int startIndex = 0;
        while (startIndex < pointsCount && function.getPointX(startIndex) < a) {
            startIndex++;
        }

        int endIndex = startIndex;
        while (endIndex < pointsCount && function.getPointX(endIndex) < b) {
            endIndex++;
        }

        // Если a не совпадает с точкой табуляции, добавляем первую трапецию
        if (startIndex > 0 && function.getPointX(startIndex - 1) < a) {
            double x0 = function.getPointX(startIndex - 1);
            double x1 = function.getPointX(startIndex);
            double y0 = function.getPointY(startIndex - 1);
            double y1 = function.getPointY(startIndex);

            // Интерполируем значение в точке a
            double ya = y0 + (y1 - y0) * (a - x0) / (x1 - x0);
            result += (x1 - a) * (y1 + ya) / 2;
        }

        // Суммируем трапеции между точками табуляции
        for (int i = startIndex; i < endIndex - 1; i++) {
            double x1 = function.getPointX(i);
            double x2 = function.getPointX(i + 1);
            double y1 = function.getPointY(i);
            double y2 = function.getPointY(i + 1);
            result += (x2 - x1) * (y1 + y2) / 2;
        }

        // Если b не совпадает с точкой табуляции, добавляем последнюю трапецию
        if (endIndex < pointsCount && function.getPointX(endIndex) > b) {
            double x0 = function.getPointX(endIndex - 1);
            double x1 = function.getPointX(endIndex);
            double y0 = function.getPointY(endIndex - 1);
            double y1 = function.getPointY(endIndex);

            // Интерполируем значение в точке b
            double yb = y0 + (y1 - y0) * (b - x0) / (x1 - x0);
            result += (b - x0) * (y0 + yb) / 2;
        }

        return result;
    }

    // ==================== Методы для ввода/вывода табулированных функций ====================

    /**
     * Выводит табулированную функцию в байтовый поток.
     * Формат: количество точек (int), затем пары координат (double, double).
     *
     * <p><strong>Обоснование обработки исключений:</strong> Метод пробрасывает IOException,
     * так как только вызывающий код знает контекст операции и может решить,
     * как правильно обработать ошибку ввода-вывода.</p>
     *
     * <p><strong>Обоснование закрытия потоков:</strong> Поток НЕ закрывается внутри метода,
     * так как метод получает уже открытый поток и не должен управлять его жизненным циклом.</p>
     *
     * @param function табулированная функция для вывода
     * @param out выходной байтовый поток
     * @throws NullPointerException если function или out равны null
     * @throws IOException если произошла ошибка ввода-вывода
     */
    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) throws IOException {
        if (function == null) {
            throw new NullPointerException("Функция не может быть null");
        }
        if (out == null) {
            throw new NullPointerException("Выходной поток не может быть null");
        }

        DataOutputStream dataOut = new DataOutputStream(out);

        // Записываем количество точек
        int pointsCount = function.getPointsCount();
        dataOut.writeInt(pointsCount);

        // Записываем координаты всех точек
        for (int i = 0; i < pointsCount; i++) {
            dataOut.writeDouble(function.getPointX(i));
            dataOut.writeDouble(function.getPointY(i));
        }

        // Принудительно сбрасываем буфер
        dataOut.flush();
        // Не закрываем dataOut, чтобы не закрывать переданный поток out
    }

    /**
     * Вводит табулированную функцию из байтового потока.
     * Ожидает формат: количество точек (int), затем пары координат (double, double).
     *
     * <p><strong>Обоснование обработки исключений:</strong> Метод пробрасывает IOException,
     * так как только вызывающий код знает, как обрабатывать ошибки чтения
     * (например, повторить попытку или использовать резервные данные).</p>
     *
     * <p><strong>Обоснование закрытия потоков:</strong> Поток НЕ закрывается внутри метода,
     * так как он может использоваться для дальнейшего чтения после вызова метода.</p>
     *
     * @param in входной байтовый поток
     * @return восстановленная табулированная функция
     * @throws NullPointerException если in равен null
     * @throws IOException если произошла ошибка ввода-вывода или данные некорректны
     */
    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException {
        if (in == null) {
            throw new NullPointerException("Входной поток не может быть null");
        }

        DataInputStream dataIn = new DataInputStream(in);

        // Читаем количество точек
        int pointsCount = dataIn.readInt();
        if (pointsCount < 2) {
            throw new IOException("Некорректные данные: количество точек должно быть не менее 2, получено: " + pointsCount);
        }

        // Читаем координаты точек
        double[] xValues = new double[pointsCount];
        double[] yValues = new double[pointsCount];

        for (int i = 0; i < pointsCount; i++) {
            xValues[i] = dataIn.readDouble();
            yValues[i] = dataIn.readDouble();
        }

        // Создаем и возвращаем табулированную функцию
        return createTabulatedFunction(xValues, yValues);
    }

    /**
     * Записывает табулированную функцию в символьный поток.
     * Формат: количество точек на первой строке, затем пары координат на отдельных строках.
     *
     * <p><strong>Обоснование обработки исключений:</strong> Метод пробрасывает IOException,
     * предоставляя вызывающему коду контроль над обработкой ошибок записи.</p>
     *
     * <p><strong>Обоснование закрытия потоков:</strong> Поток НЕ закрывается внутри метода,
     * так как он может использоваться для дальнейшей записи после вызова метода.</p>
     *
     * @param function табулированная функция для записи
     * @param out выходной символьный поток
     * @throws NullPointerException если function или out равны null
     * @throws IOException если произошла ошибка ввода-вывода
     */
    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) throws IOException {
        if (function == null) {
            throw new NullPointerException("Функция не может быть null");
        }
        if (out == null) {
            throw new NullPointerException("Выходной поток не может быть null");
        }

        PrintWriter writer = new PrintWriter(new BufferedWriter(out));

        // Записываем количество точек
        int pointsCount = function.getPointsCount();
        writer.println(pointsCount);

        // Записываем координаты всех точек
        for (int i = 0; i < pointsCount; i++) {
            writer.print(function.getPointX(i));
            writer.print(' ');
            writer.println(function.getPointY(i));
        }

        // Принудительно сбрасываем буфер
        writer.flush();
        // Не закрываем writer, чтобы не закрывать переданный поток out
    }

    /**
     * Читает табулированную функцию из символьного потока.
     * Ожидает формат: количество точек на первой строке, затем пары координат на отдельных строках.
     * Использует StreamTokenizer для разбора чисел.
     *
     * <p><strong>Обоснование обработки исключений:</strong> Метод пробрасывает IOException,
     * позволяя вызывающему коду решать, как реагировать на ошибки формата данных.</p>
     *
     * <p><strong>Обоснование закрытия потоков:</strong> Поток НЕ закрывается внутри метода,
     * так как он может использоваться для дальнейшего чтения после вызова метода.</p>
     *
     * @param in входной символьный поток
     * @return восстановленная табулированная функция
     * @throws NullPointerException если in равен null
     * @throws IOException если произошла ошибка ввода-вывода или данные некорректны
     */
    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException {
        if (in == null) {
            throw new NullPointerException("Входной поток не может быть null");
        }

        // Настраиваем StreamTokenizer для чтения чисел
        StreamTokenizer tokenizer = new StreamTokenizer(new BufferedReader(in));
        tokenizer.resetSyntax();
        tokenizer.wordChars('0', '9');  // Цифры
        tokenizer.wordChars('.', '.');  // Точка для десятичных дробей
        tokenizer.wordChars('-', '-');  // Минус для отрицательных чисел
        tokenizer.wordChars('+', '+');  // Плюс (на всякий случай)
        tokenizer.wordChars('e', 'e');  // Экспоненциальная запись
        tokenizer.wordChars('E', 'E');  // Экспоненциальная запись
        tokenizer.whitespaceChars(' ', ' ');    // Пробел как разделитель
        tokenizer.whitespaceChars('\t', '\t');  // Табуляция как разделитель
        tokenizer.whitespaceChars('\n', '\n');  // Перевод строки как разделитель
        tokenizer.whitespaceChars('\r', '\r');  // Возврат каретки как разделитель
        tokenizer.parseNumbers();  // Автоматически парсить числа

        // Читаем количество точек
        if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
            throw new IOException("Ожидалось число (количество точек)");
        }
        int pointsCount = (int) tokenizer.nval;

        if (pointsCount < 2) {
            throw new IOException("Количество точек должно быть не менее 2, получено: " + pointsCount);
        }

        // Читаем координаты точек
        List<Double> xValuesList = new ArrayList<>();
        List<Double> yValuesList = new ArrayList<>();

        for (int i = 0; i < pointsCount; i++) {
            // Читаем x
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Ожидалось число (координата x точки " + i + ")");
            }
            double x = tokenizer.nval;

            // Читаем y
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Ожидалось число (координата y точки " + i + ")");
            }
            double y = tokenizer.nval;

            xValuesList.add(x);
            yValuesList.add(y);
        }

        // Преобразуем списки в массивы
        double[] xValues = new double[pointsCount];
        double[] yValues = new double[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            xValues[i] = xValuesList.get(i);
            yValues[i] = yValuesList.get(i);
        }

        // Создаем и возвращаем табулированную функцию
        return createTabulatedFunction(xValues, yValues);
    }

    /**
     * Пример использования класса TabulatedFunctions.
     */
    public static void main(String[] args) {
        System.out.println("=== Пример использования класса TabulatedFunctions ===\n");

        try {
            // 1. Табулирование аналитической функции
            System.out.println("1. Табулирование функции sin(x) на [0, π]:");
            Function sin = new functions.basic.Sin();
            TabulatedFunction tabulatedSin = TabulatedFunctions.tabulate(sin, 0, Math.PI, 5);
            tabulatedSin.printFunction();
            System.out.println();

            // 2. Проверка обработки ошибок
            System.out.println("2. Проверка обработки ошибок:");
            try {
                // Попытка табулирования с неправильными границами
                TabulatedFunctions.tabulate(sin, -10, 10, 5);
                System.out.println("   ОШИБКА: Должно было быть выброшено исключение!");
            } catch (IllegalArgumentException e) {
                System.out.println("   ✓ Правильно выброшено исключение: " + e.getMessage());
            }

            try {
                // Попытка табулирования с pointsCount < 2
                TabulatedFunctions.tabulate(sin, 0, Math.PI, 1);
                System.out.println("   ОШИБКА: Должно было быть выброшено исключение!");
            } catch (IllegalArgumentException e) {
                System.out.println("   ✓ Правильно выброшено исключение: " + e.getMessage());
            }
            System.out.println();

            // 3. Создание табулированной функции из массива точек
            System.out.println("3. Создание функции из массива точек:");
            FunctionPoint[] points = {
                    new FunctionPoint(0.0, 0.0),
                    new FunctionPoint(1.0, 1.0),
                    new FunctionPoint(2.0, 4.0),
                    new FunctionPoint(3.0, 9.0)
            };
            TabulatedFunction parabola = TabulatedFunctions.createTabulatedFunction(points);
            parabola.printFunction();
            System.out.println();

            // 4. Интерполяция
            System.out.println("4. Интерполяция значения в точке x = 1.5:");
            double interpolatedValue = TabulatedFunctions.interpolate(parabola, 1.5);
            System.out.println("   f(1.5) ≈ " + interpolatedValue + " (ожидается 2.25)");
            System.out.println();

            // 5. Численное дифференцирование
            System.out.println("5. Численное дифференцирование:");
            double derivative = TabulatedFunctions.derivative(parabola, 1.0, 0.001);
            System.out.println("   f'(1.0) ≈ " + derivative + " (ожидается около 2.0)");
            System.out.println();

            // 6. Численное интегрирование
            System.out.println("6. Численное интегрирование методом трапеций:");
            double integral = TabulatedFunctions.integrate(parabola, 0, 2);
            System.out.println("   ∫₀² x² dx ≈ " + integral + " (ожидается около " + (8.0/3) + " ≈ 2.6667)");
            System.out.println();

            // 7. Табулирование на полной области определения
            System.out.println("7. Табулирование exp(x) на полной области определения:");
            Function exp = new functions.basic.Exp();
            // Для exp(x) область определения (-∞, +∞), поэтому укажем конкретные границы
            TabulatedFunction tabulatedExp = TabulatedFunctions.tabulate(exp, -1, 1, 5);
            tabulatedExp.printFunction();
            System.out.println();

            // 8. Создание функции из массивов координат
            System.out.println("8. Создание функции из массивов координат:");
            double[] xValues = {0.0, 0.5, 1.0, 1.5, 2.0};
            double[] yValues = {0.0, 0.25, 1.0, 2.25, 4.0};
            TabulatedFunction funcFromArrays = TabulatedFunctions.createTabulatedFunction(xValues, yValues);
            funcFromArrays.printFunction();
            System.out.println();

            // 9. Тестирование методов ввода/вывода
            System.out.println("9. Тестирование методов ввода/вывода:");

            // Тестирование байтового ввода/вывода
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            outputTabulatedFunction(parabola, byteOut);
            byte[] byteData = byteOut.toByteArray();
            System.out.println("   Байтовый вывод: записано " + byteData.length + " байт");

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteData);
            TabulatedFunction restoredFromBytes = inputTabulatedFunction(byteIn);
            System.out.println("   Байтовый ввод: функция восстановлена");

            // Тестирование символьного ввода/вывода
            StringWriter stringWriter = new StringWriter();
            writeTabulatedFunction(parabola, stringWriter);
            String stringData = stringWriter.toString();
            System.out.println("   Символьный вывод: записано " + stringData.length() + " символов");

            StringReader stringReader = new StringReader(stringData);
            TabulatedFunction restoredFromString = readTabulatedFunction(stringReader);
            System.out.println("   Символьный ввод: функция восстановлена");

            // Проверка корректности восстановления
            boolean bytesMatch = compareFunctions(parabola, restoredFromBytes);
            boolean stringMatch = compareFunctions(parabola, restoredFromString);
            System.out.println("   Байтовое восстановление корректно: " + (bytesMatch ? "ДА" : "НЕТ"));
            System.out.println("   Символьное восстановление корректно: " + (stringMatch ? "ДА" : "НЕТ"));
            System.out.println();

            // 10. Демонстрация, что нельзя создать объект класса TabulatedFunctions
            System.out.println("10. Попытка создания объекта класса TabulatedFunctions:");
            try {
                // TabulatedFunctions tf = new TabulatedFunctions(); // Ошибка компиляции
                // Рефлексия для проверки приватного конструктора
                java.lang.reflect.Constructor<TabulatedFunctions> constructor =
                        TabulatedFunctions.class.getDeclaredConstructor();
                constructor.setAccessible(true);
                constructor.newInstance();
                System.out.println("   ✗ Ошибка: объект был создан через рефлексию");
            } catch (Exception e) {
                System.out.println("   ✓ Нельзя создать объект класса TabulatedFunctions: " + e.getCause().getMessage());
            }

        } catch (Exception e) {
            System.err.println("Ошибка в демонстрационном примере: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Вспомогательный метод для сравнения двух табулированных функций.
     */
    private static boolean compareFunctions(TabulatedFunction f1, TabulatedFunction f2) {
        if (f1.getPointsCount() != f2.getPointsCount()) {
            return false;
        }

        for (int i = 0; i < f1.getPointsCount(); i++) {
            double diffX = Math.abs(f1.getPointX(i) - f2.getPointX(i));
            double diffY = Math.abs(f1.getPointY(i) - f2.getPointY(i));

            if (diffX > 1e-10 || diffY > 1e-10) {
                return false;
            }
        }

        return true;
    }
}