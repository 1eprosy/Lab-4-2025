package functions;

public class InappropriateFunctionPointException extends Exception {
    public InappropriateFunctionPointException() {
        super();
    }

    public InappropriateFunctionPointException(String message) {
        super(message);
    }

    public InappropriateFunctionPointException(String message, Throwable cause) {
        super(message, cause);
    }

    public InappropriateFunctionPointException(FunctionPoint point) {
        super("Inappropriate function point: (" + point.getX() + "; " + point.getY() + ")");
    }
}