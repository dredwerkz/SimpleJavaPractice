package org.example.interfaces;

public interface StorageRepository {
    String getSession(String key, int retryLimit);
    void putSession(String key, String value);
    void deleteSession(String key);
}
