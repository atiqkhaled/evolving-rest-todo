package sme.todo.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TaskExceptionHandler{
    private class ErrorResponse {
        public int status;
        public String message;
        public String error;

        public ErrorResponse(int status, String message, String error) {
            this.status = status;
            this.message = message;
            this.error = error;
        }
    }

    @ExceptionHandler(BusinessNotFoundException.class)
    public ResponseEntity<Object> handleTaskNotFoundException(BusinessNotFoundException ex) {
        return new ResponseEntity<Object>(buildError(HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),HttpStatus.NOT_FOUND.name()),
                HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        return new ResponseEntity<Object>(buildError(HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),HttpStatus.BAD_REQUEST.name()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        return new ResponseEntity<Object>(buildError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.name()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponse buildError(int status,String message,String error) {
        return new ErrorResponse(status,message,error);
    }

}
