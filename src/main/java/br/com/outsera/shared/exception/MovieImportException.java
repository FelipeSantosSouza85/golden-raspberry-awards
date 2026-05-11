package br.com.outsera.shared.exception;

public class MovieImportException extends RuntimeException {

    public MovieImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
