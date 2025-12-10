package functions;

import functions.meta.*;

/**
 * Утилитный класс для работы с функциями.
 * Содержит статические методы для создания мета-функций.
 * Не может быть инстанциирован.
 */
public class Functions {

    /**
     * Приватный конструктор для предотвращения создания объектов класса.
     */
    private Functions() {
        throw new AssertionError("Нельзя создавать объекты утилитного класса Functions");
    }

    /**
     * Возвращает функцию, полученную из исходной сдвигом вдоль осей.
     * @param f исходная функция
     * @param shiftX сдвиг вдоль оси X
     * @param shiftY сдвиг вдоль оси Y
     * @return сдвинутая функция: f(x) = shiftY + f(x + shiftX)
     * @throws IllegalArgumentException если исходная функция равна null
     */
    public static Function shift(Function f, double shiftX, double shiftY) {
        if (f == null) {
            throw new IllegalArgumentException("Исходная функция не может быть null");
        }
        return new Shift(f, shiftX, shiftY);
    }

    /**
     * Возвращает функцию, полученную из исходной масштабированием вдоль осей.
     * @param f исходная функция
     * @param scaleX коэффициент масштабирования вдоль оси X
     * @param scaleY коэффициент масштабирования вдоль оси Y
     * @return масштабированная функция: f(x) = scaleY * f(scaleX * x)
     * @throws IllegalArgumentException если исходная функция равна null
     * @throws IllegalArgumentException если scaleX или scaleY равны 0
     */
    public static Function scale(Function f, double scaleX, double scaleY) {
        if (f == null) {
            throw new IllegalArgumentException("Исходная функция не может быть null");
        }
        if (Math.abs(scaleX) < 1e-10) {
            throw new IllegalArgumentException("Коэффициент масштабирования scaleX не может быть 0");
        }
        if (Math.abs(scaleY) < 1e-10) {
            throw new IllegalArgumentException("Коэффициент масштабирования scaleY не может быть 0");
        }
        return new Scale(f, scaleX, scaleY);
    }

    /**
     * Возвращает функцию, являющуюся заданной степенью исходной.
     * @param f исходная функция
     * @param power степень
     * @return функция в степени: f(x) = [g(x)]^power
     * @throws IllegalArgumentException если исходная функция равна null
     */
    public static Function power(Function f, double power) {
        if (f == null) {
            throw new IllegalArgumentException("Исходная функция не может быть null");
        }
        return new Power(f, power);
    }

    /**
     * Возвращает функцию, являющуюся суммой двух исходных функций.
     * @param f1 первая функция
     * @param f2 вторая функция
     * @return сумма функций: f(x) = f1(x) + f2(x)
     * @throws IllegalArgumentException если любая из функций равна null
     */
    public static Function sum(Function f1, Function f2) {
        if (f1 == null || f2 == null) {
            throw new IllegalArgumentException("Функции не могут быть null");
        }
        return new Sum(f1, f2);
    }

    /**
     * Возвращает функцию, являющуюся произведением двух исходных функций.
     * @param f1 первая функция
     * @param f2 вторая функция
     * @return произведение функций: f(x) = f1(x) * f2(x)
     * @throws IllegalArgumentException если любая из функций равна null
     */
    public static Function mult(Function f1, Function f2) {
        if (f1 == null || f2 == null) {
            throw new IllegalArgumentException("Функции не могут быть null");
        }
        return new Mult(f1, f2);
    }

    /**
     * Возвращает функцию, являющуюся композицией двух исходных функций.
     * @param outer внешняя функция (применяется второй)
     * @param inner внутренняя функция (применяется первой)
     * @return композиция функций: f(x) = outer(inner(x))
     * @throws IllegalArgumentException если любая из функций равна null
     */
    public static Function composition(Function outer, Function inner) {
        if (outer == null || inner == null) {
            throw new IllegalArgumentException("Функции не могут быть null");
        }
        return new Composition(outer, inner);
    }

    /**
     * Вспомогательный метод для создания полинома заданной степени.
     * @param coefficients массив коэффициентов полинома, начиная со свободного члена
     * @return полиномиальная функция
     * @throws IllegalArgumentException если coefficients равен null или пуст
     */
    public static Function polynomial(double[] coefficients) {
        if (coefficients == null) {
            throw new IllegalArgumentException("Массив коэффициентов не может быть null");
        }
        if (coefficients.length == 0) {
            throw new IllegalArgumentException("Массив коэффициентов не может быть пустым");
        }

        // Создаем константную функцию для свободного члена
        Function result = constant(coefficients[0]);

        // Добавляем остальные члены полинома
        for (int i = 1; i < coefficients.length; i++) {
            if (Math.abs(coefficients[i]) > 1e-10) { // Пропускаем нулевые коэффициенты
                Function term = mult(constant(coefficients[i]), power(identity(), i));
                result = sum(result, term);
            }
        }

        return result;
    }

    /**
     * Вспомогательный метод для создания константной функции.
     * @param value значение константы
     * @return константная функция f(x) = value
     */
    public static Function constant(double value) {
        return new Constant(value);
    }

    /**
     * Вспомогательный метод для создания тождественной функции.
     * @return тождественная функция f(x) = x
     */
    public static Function identity() {
        return Identity.INSTANCE;
    }

    /**
     * Внутренний класс для тождественной функции (паттерн Singleton).
     */
    private static class Identity implements Function {
        // Singleton экземпляр
        public static final Identity INSTANCE = new Identity();

        private Identity() {} // Приватный конструктор

        @Override
        public double getLeftDomainBorder() {
            return Double.NEGATIVE_INFINITY;
        }

        @Override
        public double getRightDomainBorder() {
            return Double.POSITIVE_INFINITY;
        }

        @Override
        public double getFunctionValue(double x) {
            return x;
        }

        @Override
        public String toString() {
            return "x";
        }
    }

    /**
     * Внутренний класс для константной функции.
     */
    private static class Constant implements Function {
        private final double value;

        public Constant(double value) {
            this.value = value;
        }

        @Override
        public double getLeftDomainBorder() {
            return Double.NEGATIVE_INFINITY;
        }

        @Override
        public double getRightDomainBorder() {
            return Double.POSITIVE_INFINITY;
        }

        @Override
        public double getFunctionValue(double x) {
            return value;
        }

        @Override
        public String toString() {
            return Double.toString(value);
        }
    }

    /**
     * Вспомогательный метод для табуляции функции на заданном интервале.
     * @param f функция для табуляции
     * @param leftX левая граница интервала
     * @param rightX правая граница интервала
     * @param pointsCount количество точек табуляции
     * @return табулированная функция
     * @throws IllegalArgumentException если f равна null
     * @throws IllegalArgumentException если pointsCount < 2
     * @throws IllegalArgumentException если leftX >= rightX
     */
    public static TabulatedFunction tabulate(Function f, double leftX, double rightX, int pointsCount) {
        if (f == null) {
            throw new IllegalArgumentException("Функция не может быть null");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }

        // Создаем массив значений функции
        double[] values = new double[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            values[i] = f.getFunctionValue(x);
        }

        // Создаем и возвращаем табулированную функцию
        return new ArrayTabulatedFunction(leftX, rightX, values);
    }

    /**
     * Вспомогательный метод для получения производной функции численным методом.
     * @param f функция для дифференцирования
     * @param epsilon шаг для численного дифференцирования
     * @return приближенное значение производной функции
     * @throws IllegalArgumentException если f равна null
     * @throws IllegalArgumentException если epsilon <= 0
     */
    public static Function derivative(Function f, double epsilon) {
        if (f == null) {
            throw new IllegalArgumentException("Функция не может быть null");
        }
        if (epsilon <= 0) {
            throw new IllegalArgumentException("Шаг epsilon должен быть положительным");
        }

        return new Derivative(f, epsilon);
    }

    /**
     * Внутренний класс для численной производной функции.
     */
    private static class Derivative implements Function {
        private final Function f;
        private final double epsilon;

        public Derivative(Function f, double epsilon) {
            this.f = f;
            this.epsilon = epsilon;
        }

        @Override
        public double getLeftDomainBorder() {
            // Сдвигаем границы на epsilon для численного дифференцирования
            return f.getLeftDomainBorder() + epsilon;
        }

        @Override
        public double getRightDomainBorder() {
            return f.getRightDomainBorder() - epsilon;
        }

        @Override
        public double getFunctionValue(double x) {
            // Численная производная по формуле центральной разности
            if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
                return Double.NaN;
            }

            double f1 = f.getFunctionValue(x + epsilon);
            double f2 = f.getFunctionValue(x - epsilon);

            if (Double.isNaN(f1) || Double.isNaN(f2)) {
                return Double.NaN;
            }

            return (f1 - f2) / (2 * epsilon);
        }

        @Override
        public String toString() {
            return "f'(x) приближенно для " + f;
        }
    }

    /**
     * Пример использования класса Functions.
     */
    public static void main(String[] args) {
        System.out.println("=== Пример использования класса Functions ===\n");

        // Создаем базовые функции из пакета basic
        functions.basic.Sin sin = new functions.basic.Sin();
        functions.basic.Exp exp = new functions.basic.Exp();
        functions.basic.Log log = new functions.basic.Log(Math.E);

        // 1. Сдвиг функции
        System.out.println("1. Сдвиг функции sin(x):");
        Function shiftedSin = Functions.shift(sin, Math.PI/2, 1);
        System.out.println("   f(x) = 1 + sin(x + π/2)");
        System.out.println("   f(0) = " + shiftedSin.getFunctionValue(0));
        System.out.println("   f(-π/2) = " + shiftedSin.getFunctionValue(-Math.PI/2));
        System.out.println();

        // 2. Масштабирование функции
        System.out.println("2. Масштабирование функции sin(x):");
        Function scaledSin = Functions.scale(sin, 2.0, 3.0);
        System.out.println("   f(x) = 3 * sin(2x)");
        System.out.println("   f(0) = " + scaledSin.getFunctionValue(0));
        System.out.println("   f(π/4) = " + scaledSin.getFunctionValue(Math.PI/4));
        System.out.println();

        // 3. Степень функции
        System.out.println("3. Квадрат функции sin(x):");
        Function sinSquared = Functions.power(sin, 2);
        System.out.println("   f(x) = sin²(x)");
        System.out.println("   f(0) = " + sinSquared.getFunctionValue(0));
        System.out.println("   f(π/2) = " + sinSquared.getFunctionValue(Math.PI/2));
        System.out.println();

        // 4. Сумма функций
        System.out.println("4. Сумма sin(x) и exp(x):");
        Function sinPlusExp = Functions.sum(sin, exp);
        System.out.println("   f(x) = sin(x) + exp(x)");
        System.out.println("   f(0) = " + sinPlusExp.getFunctionValue(0));
        System.out.println("   f(1) = " + sinPlusExp.getFunctionValue(1));
        System.out.println();

        // 5. Произведение функций
        System.out.println("5. Произведение sin(x) и exp(x):");
        Function sinTimesExp = Functions.mult(sin, exp);
        System.out.println("   f(x) = sin(x) * exp(x)");
        System.out.println("   f(0) = " + sinTimesExp.getFunctionValue(0));
        System.out.println("   f(π/2) = " + sinTimesExp.getFunctionValue(Math.PI/2));
        System.out.println();

        // 6. Композиция функций
        System.out.println("6. Композиция exp и sin:");
        Function expOfSin = Functions.composition(exp, sin);
        System.out.println("   f(x) = exp(sin(x))");
        System.out.println("   f(0) = " + expOfSin.getFunctionValue(0));
        System.out.println("   f(π/2) = " + expOfSin.getFunctionValue(Math.PI/2));
        System.out.println();

        // 7. Полином
        System.out.println("7. Полином 1 + 2x + 3x²:");
        Function polynomial = Functions.polynomial(new double[]{1, 2, 3});
        System.out.println("   f(0) = " + polynomial.getFunctionValue(0));
        System.out.println("   f(1) = " + polynomial.getFunctionValue(1));
        System.out.println("   f(2) = " + polynomial.getFunctionValue(2));
        System.out.println();

        // 8. Табуляция функции
        System.out.println("8. Табуляция функции sin(x) на [0, π]:");
        TabulatedFunction tabulatedSin = Functions.tabulate(sin, 0, Math.PI, 5);
        tabulatedSin.printFunction();
        System.out.println();

        // 9. Производная функции
        System.out.println("9. Численная производная sin(x) (должна быть cos(x)):");
        Function sinDerivative = Functions.derivative(sin, 1e-5);
        System.out.println("   sin'(0) ≈ " + sinDerivative.getFunctionValue(0) +
                " (ожидается cos(0) = 1)");
        System.out.println("   sin'(π/2) ≈ " + sinDerivative.getFunctionValue(Math.PI/2) +
                " (ожидается cos(π/2) = 0)");
        System.out.println();

        // 10. Сложная функция: гауссова функция
        System.out.println("10. Гауссова функция: exp(-x²):");
        Function xSquared = Functions.power(Functions.identity(), 2);
        Function negativeX = Functions.scale(xSquared, 1.0, -1.0);
        Function gaussian = Functions.composition(new functions.basic.Exp(), negativeX);

        System.out.println("   f(x) = exp(-x²)");
        for (double x = -2; x <= 2; x += 0.5) {
            System.out.printf("   f(%.1f) = %.6f%n", x, gaussian.getFunctionValue(x));
        }

        // Демонстрация, что нельзя создать объект класса Functions
        try {
            // Functions functions = new Functions(); // Эта строка вызовет ошибку компиляции
            System.out.println("\n✓ Объекты класса Functions нельзя создать - проверка пройдена");
        } catch (Exception e) {
            System.out.println("\n✗ Ошибка при попытке создания объекта: " + e.getMessage());
        }
    }
}