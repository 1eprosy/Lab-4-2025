package functions;

/**
 * Интерфейс для табулированных функций (расширяет Function)
 */
public interface TabulatedFunction extends Function {
    // Методы доступа к точкам
    int getPointsCount();

    FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException;

    void setPoint(int index, FunctionPoint point) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException;

    double getPointX(int index) throws FunctionPointIndexOutOfBoundsException;

    void setPointX(int index, double x) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException;

    double getPointY(int index) throws FunctionPointIndexOutOfBoundsException;

    void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException;

    // Методы модификации точек
    void deletePoint(int index) throws FunctionPointIndexOutOfBoundsException, IllegalStateException;

    void addPoint(FunctionPoint point) throws InappropriateFunctionPointException;

    // Метод для вывода информации о функции
    void printFunction();

    // Методы из интерфейса Function остаются (наследуются):
    // double getLeftDomainBorder();
    // double getRightDomainBorder();
    // double getFunctionValue(double x);
}