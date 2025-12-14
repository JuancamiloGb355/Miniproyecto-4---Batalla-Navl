package edu.univalle.battleship.model.serializable;

import java.io.*;

/**
 * Class {@code SerializableFileHandler}
 *
 * <p>Provides a concrete implementation of the {@link ISerializableFileHandler} interface,
 * enabling serialization and deserialization of objects to and from files.</p>
 *
 * <p>This class uses Java's built-in {@link ObjectOutputStream} and
 * {@link ObjectInputStream} for object persistence. Any object passed to
 * {@link #serialize(String, Object)} must implement {@link java.io.Serializable},
 * otherwise a {@link java.io.NotSerializableException} will be thrown.</p>
 *
 * @see ISerializableFileHandler
 * @see Serializable
 */
public class SerializableFileHandler implements ISerializableFileHandler{
    /**
     * Serializes the given object and saves it to the specified file.
     *
     * @param filename the path of the file where the object should be stored.
     * @param element  the object to be serialized; must implement {@link Serializable}.
     */
    @Override
    public void serialize(String filename, Object element) {
        try(ObjectOutputStream obs = new ObjectOutputStream(new FileOutputStream(filename))){
            obs.writeObject(element);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Deserializes an object from the specified file.
     *
     * @param filename the path of the file to read the serialized object from.
     * @return the deserialized object, or {@code null} if an error occurred
     *         or the object could not be found.
     */
    @Override
    public Object deserialize(String filename) {
        try(ObjectInputStream inp = new ObjectInputStream(new FileInputStream(filename))){
            return (Object) inp.readObject();
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }
}
