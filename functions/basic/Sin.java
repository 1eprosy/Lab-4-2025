package functions.basic;

/**
 * Класс для вычисления синуса f(x) = sin(x)
 */
public class Sin extends TrigonometricFunction {

    /**
     * Конструктор по умолчанию
     */
    public Sin() {
        // Нет необходимости в инициализации
    }

    /**
     * Вычисляет значение синуса в заданной точке
     * @param x точка, в которой вычисляется значение (в радианах)
     * @return sin(x)
     */
    @Override
    public double getFunctionValue(double x) {
        // Синус определен для всех действительных чисел
        return Math.sin(x);
    }

    @Override
    public String toString() {
        return "Sin";
    }
}