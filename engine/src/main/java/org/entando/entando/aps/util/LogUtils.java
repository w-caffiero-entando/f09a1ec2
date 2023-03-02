package org.entando.entando.aps.util;

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public final class LogUtils {

    private LogUtils(){}

    public static String cleanupDataForLog(String data){
        return Optional.ofNullable(data)
                .filter(StringUtils::isNotBlank)
                .map(d -> d.replaceAll("[\n\r]", "_"))
                //.map(d -> d.replaceAll("\\p{C}", "?"))
                .orElse(data);
    }
}
