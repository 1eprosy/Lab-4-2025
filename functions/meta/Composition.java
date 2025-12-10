package functions.meta;

import functions.Function;

/**
 * Класс для композиции двух функций: f(x) = g(h(x))
 */
public class Composition implements Function {
    private final Function outer; // Внешняя функция g
    private final Function inner; // Внутренняя функция h

    /**
     * Конструктор композиции функций
     * @param outer внешняя функция g
     * @param inner внутренняя функция h
     */
    public Composition(Function outer, Function inner) {
        if (outer == null || inner == null) {
            throw new IllegalArgumentException("Функции не могут быть null");
        }
        this.outer = outer;
        this.inner = inner;
    }

    /**
     * Возвращает левую границу области определения
     * @return левая граница внутренней функции
     */
    @Override
    public double getLeftDomainBorder() {
        return inner.getLeftDomainBorder();
    }

    /**
     * Возвращает правую границу области определения
     * @return правая граница внутренней функции
     */
    @Override
    public double getRightDomainBorder() {
        return inner.getRightDomainBorder();
    }

    /**
     * Вычисляет значение композиции функций в заданной точке
     * @param x точка, в которой вычисляется значение
     * @return g(h(x)) или Double.NaN если x вне области определения
     */
    @Override
    public double getFunctionValue(double x) {
        // Проверяем, что точка в области определения внутренней функции
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        // Вычисляем значение внутренней функции
        double innerValue = inner.getFunctionValue(x);

        // Если значение внутренней функции NaN, возвращаем NaN
        if (Double.isNaN(innerValue)) {
            return Double.NaN;
        }

        // Проверяем, что значение внутренней функции в области определения внешней
        if (innerValue < outer.getLeftDomainBorder() || innerValue > outer.getRightDomainBorder()) {
            return Double.NaN;
        }

        // Вычисляем значение внешней функции
        return outer.getFunctionValue(innerValue);
    }

    /**
     * Возвращает внешнюю функцию
     * @return внешняя функция
     */
    public Function getOuter() {
        return outer;
    }

    /**
     * Возвращает внутреннюю функцию
     * @return внутренняя функция
     */
    public Function getInner() {
        return inner;
    }

    @Override
    public String toString() {
        return outer + "(" + inner + "(x))";
    }
}