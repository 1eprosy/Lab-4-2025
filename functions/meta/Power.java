package functions.meta;

import functions.Function;

/**
 * Класс для представления функции в степени: f(x) = [g(x)]^power
 */
public class Power implements Function {
    private final Function baseFunction;
    private final double power;

    /**
     * Конструктор степени функции
     * @param baseFunction базовая функция
     * @param power степень, в которую возводится функция
     */
    public Power(Function baseFunction, double power) {
        if (baseFunction == null) {
            throw new IllegalArgumentException("Базовая функция не может быть null");
        }
        this.baseFunction = baseFunction;
        this.power = power;
    }

    /**
     * Возвращает левую границу области определения
     * @return левая граница базовой функции
     */
    @Override
    public double getLeftDomainBorder() {
        return baseFunction.getLeftDomainBorder();
    }

    /**
     * Возвращает правую границу области определения
     * @return правая граница базовой функции
     */
    @Override
    public double getRightDomainBorder() {
        return baseFunction.getRightDomainBorder();
    }

    /**
     * Вычисляет значение функции в степени в заданной точке
     * @param x точка, в которой вычисляется значение
     * @return [g(x)]^power или Double.NaN если x вне области определения
     */
    @Override
    public double getFunctionValue(double x) {
        // Проверяем, что точка в области определения
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        double baseValue = baseFunction.getFunctionValue(x);

        // Если базовое значение NaN, возвращаем NaN
        if (Double.isNaN(baseValue)) {
            return Double.NaN;
        }

        // Проверка на отрицательное основание и нецелую степень
        if (baseValue < 0 && !isInteger(power)) {
            // Math.pow вернет NaN для отрицательного основания с нецелой степенью
            return Math.pow(baseValue, power);
        }

        return Math.pow(baseValue, power);
    }

    /**
     * Проверяет, является ли число целым
     * @param value число для проверки
     * @return true если число целое (с учетом погрешности)
     */
    private boolean isInteger(double value) {
        return Math.abs(value - Math.round(value)) < 1e-10;
    }

    /**
     * Возвращает базовую функцию
     * @return базовая функция
     */
    public Function getBaseFunction() {
        return baseFunction;
    }

    /**
     * Возвращает степень
     * @return степень
     */
    public double getPower() {
        return power;
    }

    @Override
    public String toString() {
        return "(" + baseFunction + ")^" + power;
    }
}