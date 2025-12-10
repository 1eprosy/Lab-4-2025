package functions;

public class FunctionPointIndexOutOfBoundsException extends IndexOutOfBoundsException {
    public FunctionPointIndexOutOfBoundsException() {
        super();
    }

    public FunctionPointIndexOutOfBoundsException(String message) {
        super(message);
    }

    public FunctionPointIndexOutOfBoundsException(int index) {
        super("Function point index out of range: " + index);
    }

    public FunctionPointIndexOutOfBoundsException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
