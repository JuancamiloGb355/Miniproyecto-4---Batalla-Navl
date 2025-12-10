package edu.univalle.battleship.model.planeTextFiles;

import java.io.IOException;

/**
 * Interface {@code IPlaneTextFileHandler}
 *
 * <p>Defines the contract for classes that handle reading from and writing to
 * plain text files in the EISC Uno application. Implementations of this interface
 * can be used for storing logs, configuration files, or any other text-based data.</p>
 */
public interface IPlaneTextFileHandler {
    /**
     * Writes the specified content to a plain text file.
     *
     * @param filename the name or path of the file where the content will be written.
     * @param content  the {@link String} data to be written into the file.
     * @throws IOException if an I/O error occurs while writing to the file.
     */
    void write(String filename, String content) throws IOException;
    /**
     * Reads all lines from the specified plain text file.
     *
     * @param filename the name or path of the file to read.
     * @return an array of {@link String} objects, where each element represents
     *         one line of the file's content. Returns {@code null} if the file
     *         could not be read or was not found.
     */
    String[] read(String filename);
}