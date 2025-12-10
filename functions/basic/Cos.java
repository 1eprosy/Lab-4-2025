package functions.basic;

/**
 * Класс для вычисления косинуса f(x) = cos(x)
 */
public class Cos extends TrigonometricFunction {

    /**
     * Конструктор по умолчанию
     */
    public Cos() {
        // Нет необходимости в инициализации
    }

    /**
     * Вычисляет значение косинуса в заданной точке
     * @param x точка, в которой вычисляется значение (в радианах)
     * @return cos(x)
     */
    @Override
    public double getFunctionValue(double x) {
        // Косинус определен для всех действительных чисел
        return Math.cos(x);
    }

    @Override
    public String toString() {
        return "Cos";
    }
}
