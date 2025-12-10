package functions.meta;

import functions.Function;

/**
 * Класс для представления произведения двух функций: f(x) = g(x) * h(x)
 */
public class Mult implements Function {
    private final Function first;
    private final Function second;

    /**
     * Конструктор произведения двух функций
     * @param first первая функция
     * @param second вторая функция
     */
    public Mult(Function first, Function second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Функции не могут быть null");
        }
        this.first = first;
        this.second = second;
    }

    /**
     * Возвращает левую границу области определения
     * @return максимум левых границ двух функций
     */
    @Override
    public double getLeftDomainBorder() {
        return Math.max(first.getLeftDomainBorder(), second.getLeftDomainBorder());
    }

    /**
     * Возвращает правую границу области определения
     * @return минимум правых границ двух функций
     */
    @Override
    public double getRightDomainBorder() {
        return Math.min(first.getRightDomainBorder(), second.getRightDomainBorder());
    }

    /**
     * Вычисляет значение произведения функций в заданной точке
     * @param x точка, в которой вычисляется значение
     * @return произведение значений функций или Double.NaN если x вне области определения
     */
    @Override
    public double getFunctionValue(double x) {
        // Проверяем, что точка в пересечении областей определения
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        double value1 = first.getFunctionValue(x);
        double value2 = second.getFunctionValue(x);

        // Если любое из значений NaN, возвращаем NaN
        if (Double.isNaN(value1) || Double.isNaN(value2)) {
            return Double.NaN;
        }

        return value1 * value2;
    }

    /**
     * Возвращает первую функцию
     * @return первая функция
     */
    public Function getFirst() {
        return first;
    }

    /**
     * Возвращает вторую функцию
     * @return вторая функция
     */
    public Function getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "(" + first + " * " + second + ")";
    }
}