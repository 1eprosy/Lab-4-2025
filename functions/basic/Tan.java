package functions.basic;

/**
 * Класс для вычисления тангенса f(x) = tan(x)
 */
public class Tan extends TrigonometricFunction {

    /**
     * Конструктор по умолчанию
     */
    public Tan() {
        // Нет необходимости в инициализации
    }

    /**
     * Вычисляет значение тангенса в заданной точке
     * @param x точка, в которой вычисляется значение (в радианах)
     * @return tan(x) или Double.NaN если x = π/2 + π*k
     */
    @Override
    public double getFunctionValue(double x) {
        // Тангенс не определен в точках π/2 + π*k
        // Math.tan() сам возвращает правильные значения или NaN в особых случаях
        return Math.tan(x);
    }

    @Override
    public String toString() {
        return "Tan";
    }
}