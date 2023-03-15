/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.services.tenants;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class TenantConfigTest {

    @Test
    void shouldConstructorFromMapAndCopyConstructorWorkFine() {

        Map<String, String> map = Map.of("tenantCode", "CodeTenant1", "kCEnabled", "true", "kcClientId",
                "tenant1ClientId", "dbMigrationStrategy", "disabled");

        TenantConfig tc = new TenantConfig(map);

        TenantConfig clone = new TenantConfig(tc);
        Map<String, String> map2 = map.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        map2.put("", null);
        clone.putAll(map2);

        Map<String, String> mapResult = clone.getAll();

        Assertions.assertThat(mapResult).hasSize(map2.size()).containsOnlyKeys(map2.keySet());
        Assertions.assertThat(clone.getDbMigrationStrategy()).isEqualTo(Optional.of("disabled"));
    }

    @Test
    void shouldGetIntegerDefaultWorkFine() {

        Map<String, String> map = Map.of(
                        "dbMaxIdle", "",
                        "dbMaxWaitMillis", "100000",
                        "dbInitialSize", "12").entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        map.put("dbMaxTotal", null);

        TenantConfig tc = new TenantConfig(map);
        int dbMaxTotal = tc.getMaxTotal();
        int dbMaxIdle = tc.getMaxIdle();
        int dbMaxWaitMillis = tc.getMaxWaitMillis();
        int dbInitialSize = tc.getInitialSize();

        Assertions.assertThat(dbMaxTotal).isEqualTo(10);
        Assertions.assertThat(dbMaxIdle).isEqualTo(2);
        Assertions.assertThat(dbMaxWaitMillis).isEqualTo(100000);
        Assertions.assertThat(dbInitialSize).isEqualTo(12);
    }

    @Test
    void shouldDefaultGetWorkFine() {
        Map<String, String> map = Map.of("tenantCode", "1",
                "kcEnabled", "True",
                "kcAuthUrl", "3",
                "kcRealm", "4",
                "kcClientId", "5",
                "kcClientSecret", "6",
                "kcPublicClientId", "7",
                "kcSecureUris", "8",
                "kcDefaultAuthorizations", "9",
                "dbDriverClassName", "10");
        TenantConfig tc = new TenantConfig(map);

        Assertions.assertThat(tc.getTenantCode()).isEqualTo("1");
        Assertions.assertThat(tc.isKcEnabled()).isTrue();
        Assertions.assertThat(tc.getKcAuthUrl()).isEqualTo("3");
        Assertions.assertThat(tc.getKcRealm()).isEqualTo("4");
        Assertions.assertThat(tc.getKcClientId()).isEqualTo("5");
        Assertions.assertThat(tc.getKcClientSecret()).isEqualTo("6");
        Assertions.assertThat(tc.getKcPublicClientId()).isEqualTo("7");
        Assertions.assertThat(tc.getKcSecureUris()).isEqualTo("8");
        Assertions.assertThat(tc.getKcDefaultAuthorizations()).isEqualTo("9");
        Assertions.assertThat(tc.getDbDriverClassName()).isEqualTo("10");

        Map<String, String> map2 = Map.of("dbUrl", "1",
                "dbUsername", "2",
                "dbPassword", "3", "fqdns", "test.com,pippo.com,www.com");

        tc.putAll(map2);
        Assertions.assertThat(tc.getDbUrl()).isEqualTo("1");
        Assertions.assertThat(tc.getDbUsername()).isEqualTo("2");
        Assertions.assertThat(tc.getDbPassword()).isEqualTo("3");
        Assertions.assertThat(tc.getFqdns()).contains("pippo.com");

    }
    
}
