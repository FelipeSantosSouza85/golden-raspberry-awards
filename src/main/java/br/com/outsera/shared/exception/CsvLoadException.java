package br.com.outsera.shared.exception;

public class CsvLoadException extends RuntimeException {

    public CsvLoadException(String message) {
        super(message);
    }

    public CsvLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
