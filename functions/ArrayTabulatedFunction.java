package functions;

import java.io.Serializable;

public class ArrayTabulatedFunction implements TabulatedFunction, Serializable {
    private static final long serialVersionUID = 2L;
    private FunctionPoint[] points;
    private int size;

    // Конструкторы (остаются без изменений)
    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (pointsCount < 2) {
            throw new IllegalArgumentException("pointsCount must be at least 2");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("leftX must be less than rightX");
        }

        this.points = new FunctionPoint[pointsCount + 2];
        this.size = pointsCount;
        double step = (rightX - leftX) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            this.points[i] = new FunctionPoint(x, 0);
        }
    }

    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (values.length < 2) {
            throw new IllegalArgumentException("values array must have at least 2 elements");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("leftX must be less than rightX");
        }

        int pointsCount = values.length;
        this.points = new FunctionPoint[pointsCount + 2];
        this.size = pointsCount;
        double step = (rightX - leftX) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            this.points[i] = new FunctionPoint(x, values[i]);
        }
    }

    public ArrayTabulatedFunction(FunctionPoint[] pointsArray) {
        if (pointsArray == null) {
            throw new IllegalArgumentException("Points array cannot be null");
        }
        if (pointsArray.length < 2) {
            throw new IllegalArgumentException("Points array must contain at least 2 points");
        }

        // Проверяем упорядоченность точек по x
        for (int i = 1; i < pointsArray.length; i++) {
            if (pointsArray[i] == null || pointsArray[i-1] == null) {
                throw new IllegalArgumentException("Points array cannot contain null elements");
            }
            if (pointsArray[i].getX() <= pointsArray[i-1].getX()) {
                throw new IllegalArgumentException(
                        "Points must be strictly increasing by x. " +
                                "Point " + i + " has x=" + pointsArray[i].getX() +
                                " which is not greater than point " + (i-1) +
                                " with x=" + pointsArray[i-1].getX()
                );
            }
        }

        this.points = new FunctionPoint[pointsArray.length + 2];
        this.size = pointsArray.length;

        for (int i = 0; i < pointsArray.length; i++) {
            this.points[i] = new FunctionPoint(pointsArray[i]);
        }
    }

    // === РЕАЛИЗАЦИЯ МЕТОДОВ ИЗ TabulatedFunction ===
    @Override
    public int getPointsCount() {
        return size;
    }

    @Override
    public FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException("Index out of bounds: " + index);
        }
        return new FunctionPoint(points[index]);
    }

    @Override
    public void setPoint(int index, FunctionPoint point) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException("Index out of bounds: " + index);
        }

        if (!isValidXPosition(index, point.getX())) {
            throw new InappropriateFunctionPointException(
                    "New x-coordinate " + point.getX() + " at index " + index +
                            " would violate the ordering of points"
            );
        }

        points[index] = new FunctionPoint(point);
    }

    @Override
    public double getPointX(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException("Index out of bounds: " + index);
        }
        return points[index].getX();
    }

    @Override
    public void setPointX(int index, double x) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException("Index out of bounds: " + index);
        }

        double currentX = points[index].getX();
        double y = points[index].getY();

        if (Math.abs(currentX - x) < 1e-10) {
            return;
        }

        if (!isValidXPosition(index, x)) {
            throw new InappropriateFunctionPointException(
                    "New x-coordinate " + x + " at index " + index +
                            " would violate the ordering of points"
            );
        }

        // Проверка на существование точки с таким же x
        for (int i = 0; i < size; i++) {
            if (i != index && Math.abs(points[i].getX() - x) < 1e-10) {
                throw new InappropriateFunctionPointException("Point with x=" + x + " already exists");
            }
        }

        points[index] = new FunctionPoint(x, y);
    }

    @Override
    public double getPointY(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException("Index out of bounds: " + index);
        }
        return points[index].getY();
    }

    @Override
    public void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException("Index out of bounds: " + index);
        }

        double x = points[index].getX();
        points[index] = new FunctionPoint(x, y);
    }

    @Override
    public void deletePoint(int index) throws FunctionPointIndexOutOfBoundsException, IllegalStateException {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException("Index out of bounds: " + index);
        }
        if (size <= 2) {
            throw new IllegalStateException("Cannot delete point - function must have at least 2 points");
        }

        System.arraycopy(points, index + 1, points, index, size - index - 1);
        points[size - 1] = null;
        size--;

        double loadFactor = (double) size / points.length;
        double minLoadFactor = 0.5;

        if (loadFactor + Math.ulp(loadFactor) < minLoadFactor && points.length > 4) {
            shrinkArray();
        }
    }

    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        int insertIndex = 0;
        while (insertIndex < size && points[insertIndex].getX() < point.getX()) {
            insertIndex++;
        }

        if (insertIndex < size && Math.abs(points[insertIndex].getX() - point.getX()) < 1e-10) {
            throw new InappropriateFunctionPointException("Point with x=" + point.getX() + " already exists");
        }

        if (size >= points.length) {
            expandArray();
        }

        if (insertIndex < size) {
            System.arraycopy(points, insertIndex, points, insertIndex + 1, size - insertIndex);
        }

        points[insertIndex] = new FunctionPoint(point);
        size++;
    }

    @Override
    public void printFunction() {
        System.out.println("Табулированная функция:");
        System.out.println("-----------------------");

        for (int i = 0; i < getPointsCount(); i++) {
            try {
                FunctionPoint point = getPoint(i);
                System.out.printf("Точка %d: (%.4f, %.4f)%n",
                        i, point.getX(), point.getY());
            } catch (FunctionPointIndexOutOfBoundsException e) {
                System.out.println("Ошибка при получении точки " + i);
            }
        }

        System.out.println("-----------------------");
        System.out.printf("Область определения: [%.4f, %.4f]%n",
                getLeftDomainBorder(), getRightDomainBorder());
        System.out.printf("Количество точек: %d%n", getPointsCount());
    }

    // === РЕАЛИЗАЦИЯ МЕТОДОВ ИЗ Function (которые теперь наследуются) ===
    @Override
    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    @Override
    public double getRightDomainBorder() {
        return points[size - 1].getX();
    }

    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        for (int i = 0; i < size - 1; i++) {
            double x1 = points[i].getX();
            double x2 = points[i + 1].getX();

            if (Math.abs(x - x1) < 1e-10) return points[i].getY();
            if (Math.abs(x - x2) < 1e-10) return points[i + 1].getY();

            if (x > x1 && x < x2) {
                return linearInterpolation(points[i], points[i + 1], x);
            }
        }

        return Double.NaN;
    }

    // Вспомогательные методы
    private boolean isValidXPosition(int index, double newX) {
        if (index > 0 && newX <= points[index - 1].getX()) {
            return false;
        }
        if (index < size - 1 && newX >= points[index + 1].getX()) {
            return false;
        }
        return true;
    }

    private void expandArray() {
        int newCapacity = points.length * 3 / 2 + 1;
        FunctionPoint[] newArray = new FunctionPoint[newCapacity];
        System.arraycopy(points, 0, newArray, 0, size);
        points = newArray;
    }

    private void shrinkArray() {
        int newCapacity = Math.max(size + 2, points.length / 2);
        FunctionPoint[] newArray = new FunctionPoint[newCapacity];
        System.arraycopy(points, 0, newArray, 0, size);
        points = newArray;
    }

    private double linearInterpolation(FunctionPoint p1, FunctionPoint p2, double x) {
        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();

        double k = (y2 - y1) / (x2 - x1);
        return y1 + k * (x - x1);
    }
}