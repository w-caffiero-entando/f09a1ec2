/*
 * Copyright 2023-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.entando.entando.aps.system.services.searchengine;

import org.apache.commons.lang3.StringUtils;

public final class SolrEnvironmentVariables {

    private static final String SOLR_ACTIVE = "SOLR_ACTIVE";
    private static final String SOLR_ADDRESS = "SOLR_ADDRESS";
    private static final String SOLR_CORE = "SOLR_CORE";

    private SolrEnvironmentVariables() {
    }

    public static boolean active() {
        return Boolean.toString(true).equals(System.getenv(SOLR_ACTIVE));
    }

    public static String solrAddress() {
        return get(SOLR_ADDRESS, "http://localhost:8983/solr");
    }

    public static String solrCore() {
        return get(SOLR_CORE, "entando");
    }

    private static String get(String name, String defaultValue) {
        String valueFromEnv = System.getenv(name);
        if (StringUtils.isBlank(valueFromEnv)) {
            return defaultValue;
        }
        return valueFromEnv;
    }
}
