package com.agiletec.aps.system.services.baseconfig;

import com.agiletec.aps.system.SystemConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileUploadUtilsTest {

    @Mock
    private ConfigInterface configManager;

    @Test
    void shouldGetConfiguredMaxSize() {
        Mockito.when(configManager.getParam(SystemConstants.PAR_FILEUPLOAD_MAXSIZE)).thenReturn("1000");
        Assertions.assertEquals(1000l, FileUploadUtils.getFileUploadMaxSize(configManager));
    }

    @Test
    void shouldFallbackToDefaultMaxSizeIfConfiguredSizeIsNaN() {
        Mockito.when(configManager.getParam(SystemConstants.PAR_FILEUPLOAD_MAXSIZE)).thenReturn("NaN");
        Assertions.assertEquals(10485760l, FileUploadUtils.getFileUploadMaxSize(configManager));
    }

    @Test
    void shouldFallbackToDefaultMaxSizeIfConfigurationIsMissing() {
        Assertions.assertEquals(10485760l, FileUploadUtils.getFileUploadMaxSize(configManager));
    }
}
