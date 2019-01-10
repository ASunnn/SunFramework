package sunnn.sunframework.exception;

public class CircularDependsException extends BeanException {

    public CircularDependsException() {
    }

    public CircularDependsException(String message) {
        super(message);
    }
}
