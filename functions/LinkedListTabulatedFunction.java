package functions;

import java.io.Serializable;

public class LinkedListTabulatedFunction implements TabulatedFunction, Serializable {
    private static final long serialVersionUID = 3L;
    private static class Node implements Serializable {
        private static final long serialVersionUID = 4L;

        FunctionPoint point;
        Node next;
        Node prev;

        Node(FunctionPoint point) {
            this.point = new FunctionPoint(point);
        }
    }

    private Node head;
    private Node tail;
    private int size;

    // Конструкторы (остаются без изменений)
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (pointsCount < 2) {
            throw new IllegalArgumentException("pointsCount must be at least 2");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("leftX must be less than rightX");
        }

        this.size = pointsCount;
        double step = (rightX - leftX) / (pointsCount - 1);

        head = new Node(new FunctionPoint(leftX, 0));
        Node current = head;

        for (int i = 1; i < pointsCount; i++) {
            double x = leftX + i * step;
            Node newNode = new Node(new FunctionPoint(x, 0));
            current.next = newNode;
            newNode.prev = current;
            current = newNode;
        }
        tail = current;
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (values.length < 2) {
            throw new IllegalArgumentException("values array must have at least 2 elements");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("leftX must be less than rightX");
        }

        this.size = values.length;
        double step = (rightX - leftX) / (values.length - 1);

        head = new Node(new FunctionPoint(leftX, values[0]));
        Node current = head;

        for (int i = 1; i < values.length; i++) {
            double x = leftX + i * step;
            Node newNode = new Node(new FunctionPoint(x, values[i]));
            current.next = newNode;
            newNode.prev = current;
            current = newNode;
        }
        tail = current;
    }

    public LinkedListTabulatedFunction(FunctionPoint[] pointsArray) {
        if (pointsArray == null) {
            throw new IllegalArgumentException("Points array cannot be null");
        }
        if (pointsArray.length < 2) {
            throw new IllegalArgumentException("Points array must contain at least 2 points");
        }

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

        this.size = pointsArray.length;

        head = new Node(pointsArray[0]);
        Node current = head;

        for (int i = 1; i < pointsArray.length; i++) {
            Node newNode = new Node(pointsArray[i]);
            current.next = newNode;
            newNode.prev = current;
            current = newNode;
        }
        tail = current;
    }

    // Вспомогательные методы (остаются без изменений)
    private Node getNodeByIndex(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException("Index out of bounds: " + index);
        }

        Node current;
        if (index < size / 2) {
            current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
        }
        return current;
    }

    private int findInsertPosition(double x) {
        Node current = head;
        int index = 0;
        while (current != null && current.point.getX() < x) {
            current = current.next;
            index++;
        }
        return index;
    }

    // === РЕАЛИЗАЦИЯ МЕТОДОВ ИЗ TabulatedFunction ===
    @Override
    public int getPointsCount() {
        return size;
    }

    @Override
    public FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException {
        Node node = getNodeByIndex(index);
        return new FunctionPoint(node.point);
    }

    @Override
    public void setPoint(int index, FunctionPoint point)
            throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException("Index out of bounds: " + index);
        }

        Node node = getNodeByIndex(index);

        double newX = point.getX();
        if (node.prev != null && newX <= node.prev.point.getX()) {
            throw new InappropriateFunctionPointException(
                    "New x-coordinate would violate ordering (less than or equal to previous point)"
            );
        }
        if (node.next != null && newX >= node.next.point.getX()) {
            throw new InappropriateFunctionPointException(
                    "New x-coordinate would violate ordering (greater than or equal to next point)"
            );
        }

        if (node.prev != null && Math.abs(node.prev.point.getX() - newX) < 1e-10) {
            throw new InappropriateFunctionPointException("Point with this x already exists (previous point)");
        }
        if (node.next != null && Math.abs(node.next.point.getX() - newX) < 1e-10) {
            throw new InappropriateFunctionPointException("Point with this x already exists (next point)");
        }

        node.point = new FunctionPoint(point);
    }

    @Override
    public double getPointX(int index) throws FunctionPointIndexOutOfBoundsException {
        Node node = getNodeByIndex(index);
        return node.point.getX();
    }

    @Override
    public void setPointX(int index, double x)
            throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException("Index out of bounds: " + index);
        }

        Node node = getNodeByIndex(index);

        if (Math.abs(node.point.getX() - x) < 1e-10) {
            return;
        }

        FunctionPoint tempPoint = new FunctionPoint(x, node.point.getY());
        setPoint(index, tempPoint);
    }

    @Override
    public double getPointY(int index) throws FunctionPointIndexOutOfBoundsException {
        Node node = getNodeByIndex(index);
        return node.point.getY();
    }

    @Override
    public void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException("Index out of bounds: " + index);
        }

        Node node = getNodeByIndex(index);
        node.point = new FunctionPoint(node.point.getX(), y);
    }

    @Override
    public void deletePoint(int index) throws FunctionPointIndexOutOfBoundsException, IllegalStateException {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException("Index out of bounds: " + index);
        }
        if (size <= 2) {
            throw new IllegalStateException("Cannot delete point - function must have at least 2 points");
        }

        Node nodeToDelete = getNodeByIndex(index);

        if (nodeToDelete == head) {
            head = nodeToDelete.next;
            if (head != null) head.prev = null;
        } else if (nodeToDelete == tail) {
            tail = nodeToDelete.prev;
            if (tail != null) tail.next = null;
        } else {
            nodeToDelete.prev.next = nodeToDelete.next;
            nodeToDelete.next.prev = nodeToDelete.prev;
        }

        size--;
    }

    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        int insertIndex = findInsertPosition(point.getX());

        if (insertIndex < size) {
            Node existingNode = getNodeByIndex(insertIndex);
            if (Math.abs(existingNode.point.getX() - point.getX()) < 1e-10) {
                throw new InappropriateFunctionPointException("Point with x=" + point.getX() + " already exists");
            }
        }

        Node newNode = new Node(point);

        if (insertIndex == 0) {
            newNode.next = head;
            if (head != null) head.prev = newNode;
            head = newNode;
            if (tail == null) tail = newNode;
        } else if (insertIndex == size) {
            newNode.prev = tail;
            if (tail != null) tail.next = newNode;
            tail = newNode;
            if (head == null) head = newNode;
        } else {
            Node currentNode = getNodeByIndex(insertIndex);
            newNode.next = currentNode;
            newNode.prev = currentNode.prev;
            currentNode.prev.next = newNode;
            currentNode.prev = newNode;
        }

        size++;
    }

    @Override
    public void printFunction() {
        System.out.println("Табулированная функция (связный список):");
        System.out.println("-----------------------");

        Node current = head;
        int index = 0;
        while (current != null) {
            System.out.printf("Точка %d: (%.4f, %.4f)%n",
                    index, current.point.getX(), current.point.getY());
            current = current.next;
            index++;
        }

        System.out.println("-----------------------");
        System.out.printf("Область определения: [%.4f, %.4f]%n",
                getLeftDomainBorder(), getRightDomainBorder());
        System.out.printf("Количество точек: %d%n", getPointsCount());
    }

    // === РЕАЛИЗАЦИЯ МЕТОДОВ ИЗ Function ===
    @Override
    public double getLeftDomainBorder() {
        return head.point.getX();
    }

    @Override
    public double getRightDomainBorder() {
        return tail.point.getX();
    }

    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        Node current = head;
        while (current.next != null) {
            double x1 = current.point.getX();
            double x2 = current.next.point.getX();

            if (Math.abs(x - x1) < 1e-10) return current.point.getY();
            if (Math.abs(x - x2) < 1e-10) return current.next.point.getY();

            if (x > x1 && x < x2) {
                return linearInterpolation(current.point, current.next.point, x);
            }

            current = current.next;
        }

        return Double.NaN;
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