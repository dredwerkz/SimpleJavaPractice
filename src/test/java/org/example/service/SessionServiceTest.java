package org.example.service;

import org.example.exceptions.ExternalStorageException;
import org.example.interfaces.StorageRepository;
import org.example.storage.InMemoryStorageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SessionServiceTest {

    private StorageRepository storageRepository;
    private Logger logger;
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        storageRepository = mock(InMemoryStorageRepository.class);
        logger = mock(Logger.class);
        sessionService = new SessionService(logger, storageRepository, 3);
    }

    @Test
    void testGetSession_ReturnsFromCache() throws ExternalStorageException {
        // Arrange
        var userId = "user1";
        var session = "cached-session";

        when(storageRepository.getSession(userId, 3)).thenReturn(session);

        // Act
        // First invocation: session is not in local cache
        var result = sessionService.getSession(userId);
        // Second invocation: session should now be in local cache
        var result2 = sessionService.getSession(userId);

        // Assert
        assertNotNull(result);
        assertEquals(session, result);
        assertEquals(result, result2);

        // Verify
        verify(storageRepository, atMostOnce()).getSession(userId, 3);
    }

    @Test
    void testGetSession_NewSessionCreatedWhenNotFound() throws ExternalStorageException {
        // Arrange
        var userId = "user2";

        when(storageRepository.getSession(userId, 3)).thenReturn("");

        // Act
        var session = sessionService.getSession(userId);

        // Assert
        assertNotNull(session);
        assertTrue(session.startsWith("session-" + userId + "-"));

        // Verify
        verify(storageRepository).putSession(eq(userId), anyString());
    }

    @Test
    void testInvalidate_ThrowsExternalStorageExceptionOnDeleteFailure() throws ExternalStorageException {
        // Arrange
        var userId = "user3";
        var errorMessage = "Deletion Failed";
        var session = "cached-session";

        // Ensure that session exists in cache so function does not return early
        when(storageRepository.getSession(userId, 3)).thenReturn(session);
        doThrow(new ExternalStorageException(errorMessage)).when(storageRepository).deleteSession(userId);

        // Act
        // Insert session to be deleted
        sessionService.getSession(userId);

        // Catch thrown exception
        var ex = assertThrows(
                ExternalStorageException.class,
                () -> sessionService.invalidate(userId)
        );

        // Assert
        assertTrue(ex.getMessage().contains(errorMessage));

        // Verify
        verify(storageRepository, atLeastOnce()).deleteSession(userId);
    }
}
