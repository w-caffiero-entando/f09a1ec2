package com.agiletec.aps.system.services.baseconfig;

import com.agiletec.aps.system.SystemConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class FileUploadUtils {

    private static final Long DEFAULT_MAX_SIZE = 10485760l;

    private FileUploadUtils() {
    }

    public static long getFileUploadMaxSize(ConfigInterface configManager) {
        String maxSizeParam = configManager.getParam(SystemConstants.PAR_FILEUPLOAD_MAXSIZE);
        if (null != maxSizeParam) {
            try {
                return Long.parseLong(maxSizeParam);
            } catch (NumberFormatException t) {
                log.error("Error parsing param 'maxSize' - value '{}'", maxSizeParam, t);
            }
        }
        return DEFAULT_MAX_SIZE;
    }
}
