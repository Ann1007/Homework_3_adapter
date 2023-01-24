package by.tsuprikova.adapter.advice;

import by.tsuprikova.adapter.exceptions.ResponseWithFineNullException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
@Slf4j
public class ApplicationExceptionHandler {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleInvalidArgumentInRequest(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });

        return errorMap;
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ResponseWithFineNullException.class)
    public Map<String, String> handleResponseWithFineNullException(ResponseWithFineNullException ex) {
        log.error(ex.getMessage());
        Map<String, String> map = new HashMap<>();
        map.put("error message ", "No information found on the given sts, try again later...");
        return map;

    }

    /*@ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ExceptionHandler(Exception.class)
    public ResponseWithFineNullException handleResponseWithWebclientRequest(RuntimeException we) {
        log.error(we.getMessage());
        return new ResponseWithFineNullException("No information found, please try again later");
    }*/

}