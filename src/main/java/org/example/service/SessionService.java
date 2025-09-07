package org.example.service;

import org.example.exceptions.ExternalStorageException;
import org.example.interfaces.StorageRepository;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessionService {
    private final Logger logger;
    private final Map<String, String> cache = new ConcurrentHashMap<>();
    private final StorageRepository storageRepository;
    private final int retryLimit;

    public SessionService(Logger logger, StorageRepository storageRepository, int retryLimit) {
        this.logger = logger;
        this.storageRepository = storageRepository;
        this.retryLimit = retryLimit;
    }

    // Get A User Session
    public String getSession(String userId) throws ExternalStorageException {

        if (cache.containsKey(userId)) {
            return cache.get(userId);
        }

        var externalSession = storageRepository.getSession(userId, retryLimit);

        if (externalSession.isEmpty()) {

            var newSession = "session-" + userId + "-" + java.util.UUID.randomUUID();

            try {
                storageRepository.putSession(userId, newSession);
            } catch (Exception e) {
                throw new ExternalStorageException(e.getMessage());
            }

            cache.put(userId, newSession);

            return newSession;
        }

        cache.put(userId, externalSession);

        return externalSession;

    }

    // Kill session
    public void invalidate(String userId) throws ExternalStorageException {
        if (!cache.containsKey(userId)) {
            logger.log(Level.INFO, "Attempted to invalidate userId: " + userId +
                    ", however userId was not present in cache");

            return;
        }

        cache.remove(userId);

        try {
            storageRepository.deleteSession(userId);
        } catch (Exception e) {
            throw new ExternalStorageException("Error deleting " + userId + ": " + e.getMessage());
        }
    }
}
