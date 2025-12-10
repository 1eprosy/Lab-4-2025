package functions.basic;

import functions.Function;

/**
 * Базовый класс для тригонометрических функций
 * Определяет общие методы для получения границ области определения
 */
public abstract class TrigonometricFunction implements Function {

    /**
     * Возвращает левую границу области определения
     * @return Double.NEGATIVE_INFINITY (минус бесконечность)
     */
    @Override
    public double getLeftDomainBorder() {
        return Double.NEGATIVE_INFINITY;
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
     * Абстрактный метод для вычисления значения функции
     * @param x точка, в которой вычисляется значение
     * @return значение функции
     */
    @Override
    public abstract double getFunctionValue(double x);
}
