package org.entando.entando.plugins.jpcds.aps.system.storage;

import org.apache.commons.lang3.BooleanUtils;

public final class CdsEnvironmentVariables {

    private static final String CDS_ACTIVE = "cds.enabled";
    private CdsEnvironmentVariables() {
    }

    public static boolean active() {
        return BooleanUtils.toBoolean(System.getProperty(CDS_ACTIVE,"false"));
    }

}
