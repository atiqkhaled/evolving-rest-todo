package sme.todo.exceptions;

public class BusinessNotFoundException extends RuntimeException {
    public BusinessNotFoundException(String msg) {
        super(msg);
    }
}
