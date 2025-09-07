package org.example.storage;

import org.example.exceptions.ExternalStorageException;
import org.example.interfaces.ExternalStorageEmulator;
import org.example.interfaces.StorageRepository;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InMemoryStorageRepository implements StorageRepository {

    private final Logger logger;
    private final ExternalStorageEmulator storageEmulator;


    public InMemoryStorageRepository(Logger logger, ExternalStorageEmulator storageEmulator) {
        this.logger = logger;
        this.storageEmulator = storageEmulator;
    }

    public String getSession(String key, int retryLimit) throws ExternalStorageException {

        return String.valueOf(
                CompletableFuture.supplyAsync(() -> storageEmulator.get(key))
                        .orTimeout(retryLimit, TimeUnit.SECONDS)
                        .exceptionally(ex -> {
                            logger.log(
                                    Level.WARNING,
                                    "Could not fetch session from external storage"
                            );

                            throw new ExternalStorageException(
                                    "Could not successfully retrieve session from external storage"
                            );
                        })
        );

    }


    public void putSession(String key, String value) throws ExternalStorageException {
        logger.log(Level.INFO, "Attempting to add Session to External Storage");

        try {
            storageEmulator.put(key, value);
        } catch (Exception e) {
            throw new ExternalStorageException(e.getMessage());
        }
    }

    public void deleteSession(String key) throws ExternalStorageException {
        logger.log(Level.INFO, "Attempting to delete Session from External Storage");

        try {
            storageEmulator.delete(key);
        } catch (Exception e) {
            throw new ExternalStorageException(e.getMessage());
        }
    }
}