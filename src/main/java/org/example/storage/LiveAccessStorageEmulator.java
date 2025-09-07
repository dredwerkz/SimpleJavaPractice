package org.example.storage;

import org.example.exceptions.ExternalStorageException;
import org.example.interfaces.ExternalStorageEmulator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LiveAccessStorageEmulator implements ExternalStorageEmulator {
    private final Logger logger;
    private final static Map<String, String> storage = new ConcurrentHashMap<>();

    public LiveAccessStorageEmulator(Logger logger) {
        this.logger = logger;
    }

    public String get(String key) throws ExternalStorageException {
        if (Math.random() < 0.3) { // random failure
            logger.log(Level.WARNING, "An unknown error occurred while getting session from external storage");
            throw new ExternalStorageException("Failed to retrieve session data from External Storage");
        }

        // If the key exists, return it
        if (storage.containsKey(key)) return storage.get(key);

        // Otherwise, return an empty string
        return "";
    }

    public void put(String key, String value) throws ExternalStorageException {
        if (Math.random() < 0.2) {
            logger.log(Level.WARNING, "An unknown error occurred while getting session from external storage");
            throw new ExternalStorageException("Failed to write session data to External Storage");
        }
        storage.put(key, value);
    }

    public void delete(String key) throws ExternalStorageException {
        if (Math.random() < 0.1) {
            logger.log(Level.WARNING, "An unknown error occurred while getting session from external storage");
            throw new ExternalStorageException("Failed to delete session data from External Storage");
        }
        storage.remove(key);
    }
}
