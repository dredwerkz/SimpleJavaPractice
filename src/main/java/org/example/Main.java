package org.example;

import org.example.interfaces.StorageRepository;
import org.example.service.SessionService;
import org.example.storage.InMemoryStorageRepository;
import org.example.storage.LiveAccessStorageEmulator;

import java.util.logging.Level;
import java.util.logging.Logger;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        final int managerRetryLimit = 3;
        final Logger logger = Logger.getLogger(SessionService.class.getName());

        final StorageRepository _storageRepository = new InMemoryStorageRepository(
                logger,
                new LiveAccessStorageEmulator(logger)
        );

        var liveAccessManager = new SessionService(logger, _storageRepository, managerRetryLimit);
        logger.log(Level.INFO, "Live Access Manager Started");
    }
}
