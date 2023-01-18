package by.tsuprikova.adapter.exceptions;

public class ResponseWithFineNullException extends RuntimeException{
    public ResponseWithFineNullException() {
    }

    public ResponseWithFineNullException(String message) {
        super(message);
    }
}
