package functions.meta;

import functions.Function;

/**
 * Класс для масштабирования функции вдоль осей координат:
 * f(x) = yScale * g(xScale * x)
 */
public class Scale implements Function {
    private final Function function;
    private final double xScale;
    private final double yScale;

    /**
     * Конструктор масштабированной функции
     * @param function исходная функция
     * @param xScale коэффициент масштабирования по оси X
     * @param yScale коэффициент масштабирования по оси Y
     */
    public Scale(Function function, double xScale, double yScale) {
        if (function == null) {
            throw new IllegalArgumentException("Функция не может быть null");
        }
        if (Math.abs(xScale) < 1e-10) {
            throw new IllegalArgumentException("Коэффициент масштабирования X не может быть нулевым");
        }
        if (Math.abs(yScale) < 1e-10) {
            throw new IllegalArgumentException("Коэффициент масштабирования Y не может быть нулевым");
        }

        this.function = function;
        this.xScale = xScale;
        this.yScale = yScale;
    }

    /**
     * Возвращает левую границу области определения
     * @return левая граница после масштабирования по X
     */
    @Override
    public double getLeftDomainBorder() {
        if (xScale > 0) {
            return function.getLeftDomainBorder() / xScale;
        } else {
            // Если xScale отрицательный, происходит отражение
            return function.getRightDomainBorder() / xScale;
        }
    }

    /**
     * Возвращает правую границу области определения
     * @return правая граница после масштабирования по X
     */
    @Override
    public double getRightDomainBorder() {
        if (xScale > 0) {
            return function.getRightDomainBorder() / xScale;
        } else {
            // Если xScale отрицательный, происходит отражение
            return function.getLeftDomainBorder() / xScale;
        }
    }

    /**
     * Вычисляет значение масштабированной функции в заданной точке
     * @param x точка, в которой вычисляется значение
     * @return yScale * f(xScale * x) или Double.NaN если x вне области определения
     */
    @Override
    public double getFunctionValue(double x) {
        // Проверяем, что точка в области определения
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        double scaledX = xScale * x;

        // Проверяем, что масштабированная точка в области определения исходной функции
        if (scaledX < function.getLeftDomainBorder() || scaledX > function.getRightDomainBorder()) {
            return Double.NaN;
        }

        double originalValue = function.getFunctionValue(scaledX);

        // Если исходное значение NaN, возвращаем NaN
        if (Double.isNaN(originalValue)) {
            return Double.NaN;
        }

        return yScale * originalValue;
    }

    /**
     * Возвращает исходную функцию
     * @return исходная функция
     */
    public Function getFunction() {
        return function;
    }

    /**
     * Возвращает коэффициент масштабирования по X
     * @return коэффициент масштабирования по X
     */
    public double getXScale() {
        return xScale;
    }

    /**
     * Возвращает коэффициент масштабирования по Y
     * @return коэффициент масштабирования по Y
     */
    public double getYScale() {
        return yScale;
    }

    @Override
    public String toString() {
        return "Scale(" + function + ", xScale=" + xScale + ", yScale=" + yScale + ")";
    }
}