package edu.univalle.battleship.model.serializable;

import java.io.Serializable;

/**
 * Interface {@code ISerializableFileHandler}
 *
 * <p>Defines the contract for classes that handle serialization and deserialization of objects
 * to and from files. This interface allows the saving and loading of game states or other
 * serializable data structures used in the EISC Uno application.</p>
 *
 * @see Serializable
 */
public interface ISerializableFileHandler {
    /**
     * Serializes the given object and saves it to the specified file.
     *
     * @param filename the name or path of the file where the object will be stored.
     * @param element  the {@link Object} to be serialized and written to the file.
     *                  <p><b>Note:</b> The provided object should implement {@link Serializable}
     *                  for the serialization process to succeed.</p>
     */
    void serialize(String filename, Object element);
    /**
     * Deserializes an object from the specified file.
     *
     * @param filename the name or path of the file to read from.
     * @return the {@link Object} that was deserialized from the file,
     *         or {@code null} if the process failed or the file was not found.
     */
    Object deserialize(String filename);
}

