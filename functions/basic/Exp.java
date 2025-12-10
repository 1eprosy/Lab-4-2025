package functions.basic;

import functions.Function;

/**
 * Класс для вычисления экспоненциальной функции f(x) = e^x
 */
public class Exp implements Function {

    /**
     * Конструктор по умолчанию
     */
    public Exp() {
        // Нет необходимости в инициализации
    }

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
     * Вычисляет значение экспоненты в заданной точке
     * @param x точка, в которой вычисляется значение
     * @return e^x
     */
    @Override
    public double getFunctionValue(double x) {
        // Экспонента определена для всех действительных чисел
        return Math.exp(x);
    }
}
