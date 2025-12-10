package functions.meta;

import functions.Function;

/**
 * Класс для сдвига функции вдоль осей координат:
 * f(x) = yShift + g(x + xShift)
 */
public class Shift implements Function {
    private final Function function;
    private final double xShift;
    private final double yShift;

    /**
     * Конструктор сдвинутой функции
     * @param function исходная функция
     * @param xShift сдвиг по оси X
     * @param yShift сдвиг по оси Y
     */
    public Shift(Function function, double xShift, double yShift) {
        if (function == null) {
            throw new IllegalArgumentException("Функция не может быть null");
        }
        this.function = function;
        this.xShift = xShift;
        this.yShift = yShift;
    }

    /**
     * Возвращает левую границу области определения
     * @return левая граница после сдвига по X
     */
    @Override
    public double getLeftDomainBorder() {
        return function.getLeftDomainBorder() - xShift;
    }

    /**
     * Возвращает правую границу области определения
     * @return правая граница после сдвига по X
     */
    @Override
    public double getRightDomainBorder() {
        return function.getRightDomainBorder() - xShift;
    }

    /**
     * Вычисляет значение сдвинутой функции в заданной точке
     * @param x точка, в которой вычисляется значение
     * @return yShift + f(x + xShift) или Double.NaN если x вне области определения
     */
    @Override
    public double getFunctionValue(double x) {
        // Проверяем, что точка в области определения
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        double shiftedX = x + xShift;

        // Проверяем, что сдвинутая точка в области определения исходной функции
        if (shiftedX < function.getLeftDomainBorder() || shiftedX > function.getRightDomainBorder()) {
            return Double.NaN;
        }

        double originalValue = function.getFunctionValue(shiftedX);

        // Если исходное значение NaN, возвращаем NaN
        if (Double.isNaN(originalValue)) {
            return Double.NaN;
        }

        return yShift + originalValue;
    }

    /**
     * Возвращает исходную функцию
     * @return исходная функция
     */
    public Function getFunction() {
        return function;
    }

    /**
     * Возвращает сдвиг по X
     * @return сдвиг по X
     */
    public double getXShift() {
        return xShift;
    }

    /**
     * Возвращает сдвиг по Y
     * @return сдвиг по Y
     */
    public double getYShift() {
        return yShift;
    }

    @Override
    public String toString() {
        return "Shift(" + function + ", xShift=" + xShift + ", yShift=" + yShift + ")";
    }
}