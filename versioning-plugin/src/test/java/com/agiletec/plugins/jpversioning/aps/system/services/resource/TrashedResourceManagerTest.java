package com.agiletec.plugins.jpversioning.aps.system.services.resource;

import com.agiletec.plugins.jacms.aps.system.services.resource.model.AttachResource;
import org.entando.entando.aps.system.services.storage.IStorageManager;
import org.entando.entando.ent.exception.EntException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrashedResourceManagerTest {

    @Mock
    private IStorageManager storageManager;

    @InjectMocks
    private TrashedResourceManager trashedResourceManager;

    @Test
    void shouldCreateRootFolderIfItDoesNotExist() throws Exception {
        AttachResource resource = new AttachResource();
        Mockito.when(storageManager.exists(Mockito.any(), Mockito.eq(true))).thenReturn(false);
        trashedResourceManager.getSubfolder(resource);
        Mockito.verify(storageManager, Mockito.times(1)).createDirectory(Mockito.any(), Mockito.eq(true));
    }

    @Test
    void shouldThrowExceptionOnStorageManagerFailure() throws Exception {
        AttachResource resource = new AttachResource();
        Mockito.doThrow(EntException.class).when(storageManager).exists(Mockito.any(), Mockito.eq(true));
        Assertions.assertThrows(RuntimeException.class, () -> trashedResourceManager.getSubfolder(resource));
    }
}
