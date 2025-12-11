package edu.univalle.battleship.model.planeTextFiles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;

/**
 * Class {@code PlaneTextFileHandler}
 *
 * <p>Concrete implementation of {@link IPlaneTextFileHandler} for handling plain text file
 * operations. This class provides methods for writing string data to a file and reading
 * content from a text file line by line.</p>
 *
 * <p>Useful for saving logs, user data, or any configuration files that do not require
 * serialization.</p>
 *
 * @see IPlaneTextFileHandler
 */
public class PlaneTextFileHandler implements IPlaneTextFileHandler {
    /**
     * Writes the specified string content to a file, replacing any existing content.
     *
     * @param filename the name or path of the file where the content will be written.
     * @param content  the text content to write to the file.
     * @throws IOException if an error occurs while writing to the file.
     */
    @Override
    public void write(String filename, String content) throws IOException{
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filename))){
            writer.write(content);
            writer.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Reads the contents of the specified text file line by line.
     *
     * <p>Each line is returned as an element in a string array. If the file could not
     * be read, the returned array will contain a single empty string.</p>
     *
     * @param filename the name or path of the file to read.
     * @return an array of strings representing each line of the file.
     */
    @Override
    public String[] read(String filename) {
        StringBuilder content = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))){
            String line;
            while ((line = reader.readLine()) != null){
                content.append(line).append(",");
            }
        }catch (IOException e ){
            e.printStackTrace();
        }
        return content.toString().split(",");
    }
}

