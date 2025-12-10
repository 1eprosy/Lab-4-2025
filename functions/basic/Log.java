package functions.basic;

import functions.Function;

/**
 * Класс для вычисления логарифмической функции f(x) = log_base(x)
 */
public class Log implements Function {
    private final double base;

    /**
     * Конструктор с заданием основания логарифма
     * @param base основание логарифма (должно быть положительным и не равным 1)
     * @throws IllegalArgumentException если основание некорректно
     */
    public Log(double base) {
        if (base <= 0 || Math.abs(base - 1.0) < 1e-10) {
            throw new IllegalArgumentException(
                    "Основание логарифма должно быть положительным и не равным 1. " +
                            "Получено: " + base
            );
        }
        this.base = base;
    }

    /**
     * Возвращает левую границу области определения
     * @return 0.0 (логарифм определен только для положительных чисел)
     */
    @Override
    public double getLeftDomainBorder() {
        return 0.0;
    }

    /**
     * Возвращает правую границу области определения
     * @return Double.POSITIVE_INFINITY (плюс бесконечность)
     */
    @Override
    public double getRightDomainBorder() {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * Вычисляет значение логарифма в заданной точке
     * @param x точка, в которой вычисляется значение (должна быть положительной)
     * @return log_base(x) или Double.NaN если x вне области определения
     */
    @Override
    public double getFunctionValue(double x) {
        // Логарифм определен только для положительных чисел
        if (x <= 0) {
            return Double.NaN;
        }

        // Используем формулу замены основания: log_base(x) = ln(x) / ln(base)
        return Math.log(x) / Math.log(base);
    }

    /**
     * Возвращает основание логарифма
     * @return основание логарифма
     */
    public double getBase() {
        return base;
    }

    @Override
    public String toString() {
        return "Log(base=" + base + ")";
    }
}
