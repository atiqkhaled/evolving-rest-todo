package sme.todo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class BusinessServiceUnavailableException extends RuntimeException {
    public BusinessServiceUnavailableException() {
        super();
    }
}
