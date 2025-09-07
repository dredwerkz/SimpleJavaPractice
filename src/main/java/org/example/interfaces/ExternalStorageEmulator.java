package org.example.interfaces;

public interface ExternalStorageEmulator {
    String get(String key);
    void put(String key, String value);
    void delete(String key);
}
