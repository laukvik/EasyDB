package no.laukvik.easydb.exception;

public class NoEntityException extends RuntimeException {

    public NoEntityException(Exception exception) {
        this(exception.getMessage());
    }

    public NoEntityException(String message) {
        super(message);
    }
}
