package org.entando.entando.aps.system.services.actionlog;

import java.util.Set;
import org.entando.entando.ent.exception.EntException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActivityStreamCleanerThreadTest {

    private static int MAX_ACTIVITY_SIZE_BY_GROUP = 100;

    @Mock
    private IActionLogManager actionLogManager;

    private ActivityStreamCleanerThread thread;

    @BeforeEach
    void setUp() {
        thread = new ActivityStreamCleanerThread(MAX_ACTIVITY_SIZE_BY_GROUP, actionLogManager);
    }

    @Test
    void shouldDeleteOldRecords() throws Exception {
        Mockito.when(actionLogManager.extractOldRecords(MAX_ACTIVITY_SIZE_BY_GROUP)).thenReturn(Set.of(1, 2));
        thread.run();
        thread.join();
        Mockito.verify(actionLogManager).deleteActionRecord(1);
        Mockito.verify(actionLogManager).deleteActionRecord(2);
    }

    @Test
    void shouldHandleNullRecords() throws Exception {
        thread.run();
        thread.join();
        Mockito.verify(actionLogManager).extractOldRecords(MAX_ACTIVITY_SIZE_BY_GROUP);
    }

    @Test
    void shouldHandleActionLogManagerException() throws Exception {
        Mockito.doThrow(EntException.class).when(actionLogManager).extractOldRecords(MAX_ACTIVITY_SIZE_BY_GROUP);
        thread.run();
        thread.join();
        Mockito.verify(actionLogManager).extractOldRecords(MAX_ACTIVITY_SIZE_BY_GROUP);
    }
}
