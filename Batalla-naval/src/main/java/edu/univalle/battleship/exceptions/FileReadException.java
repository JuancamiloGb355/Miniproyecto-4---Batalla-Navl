package edu.univalle.battleship.exceptions;

public class FileReadException extends RuntimeException {
    public FileReadException(String message) {
        super(message);
    }

    // Excepcion para errores al leer archivos.
}
