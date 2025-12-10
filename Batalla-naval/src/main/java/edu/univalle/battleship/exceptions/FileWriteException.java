package edu.univalle.battleship.exceptions;

public class FileWriteException extends RuntimeException {
    public FileWriteException(String message) {
        super(message);
    }

    // Excepcion para errores al escribir archivos.
}
