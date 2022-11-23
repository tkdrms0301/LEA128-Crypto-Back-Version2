package kumoh.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {
//    @ExceptionHandler(value = Exception.class)
//    public String proceedAllException(Exception e){
//        return String.format("<h1>%s</h1>", e.getMessage());
//    }

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<?> proceedAllException(CustomException e){
        return ResponseEntity.badRequest().body(e.getErrorDto());
    }
}
