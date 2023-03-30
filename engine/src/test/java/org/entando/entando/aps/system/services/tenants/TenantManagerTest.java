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

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.apache.commons.dbcp2.BasicDataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class TenantManagerTest {

    private String tenantConfigs="[{\n"
            + "    \"tenantCode\": \"TE_nant1\",\n"
            + "    \"kcEnabled\": true,\n"
            + "    \"fqdns\": \"tenant1.com,tenant2.com\",\n"
            + "    \"kcAuthUrl\": \"http://tenant1.test.nip.io/auth\",\n"
            + "    \"kcRealm\": \"tenant1\",\n"
            + "    \"kcClientId\": \"quickstart\",\n"
            + "    \"kcClientSecret\": \"secret1\",\n"
            + "    \"kcPublicClientId\": \"entando-web\",\n"
            + "    \"kcSecureUris\": \"\",\n"
            + "    \"kcDefaultAuthorizations\": \"\",\n"
            + "    \"dbDriverClassName\": \"org.postgresql.Driver\",\n"
            + "    \"dbUrl\": \"jdbc:postgresql://testDbServer:5432/tenantDb1\",\n"
            + "    \"dbUsername\": \"db_user_2\",\n"
            + "    \"dbPassword\": \"db_password_2\"\n"
            + "}, {\n"
            + "    \"tenantCode\": \"tenant2\",\n"
            + "    \"kcEnabled\": true,\n"
            + "    \"kcAuthUrl\": \"http://tenant2.test.nip.io/auth\",\n"
            + "    \"kcRealm\": \"tenant2\",\n"
            + "    \"kcClientId\": \"quickstart\",\n"
            + "    \"kcClientSecret\": \"secret2\",\n"
            + "    \"kcPublicClientId\": \"entando-web\",\n"
            + "    \"kcSecureUris\": \"\",\n"
            + "    \"kcDefaultAuthorizations\": \"\",\n"
            + "    \"dbDriverClassName\": \"org.postgresql.Driver\",\n"
            + "    \"dbUrl\": \"jdbc:postgresql://testDbServer:5432/tenantDb2\",\n"
            + "    \"dbUsername\": \"db_user_1\",\n"
            + "    \"dbPassword\": \"db_password_1\"\n"
            + "}]\n";

    private String tenantConfigsWithCustomFields="[{\n"
            + "    \"tenantCode\": \"TE_nant1\",\n"
            + "    \"kcEnabled\": true,\n"
            + "    \"kcAuthUrl\": \"http://tenant1.test.nip.io/auth\",\n"
            + "    \"kcRealm\": \"tenant1\",\n"
            + "    \"kcClientId\": \"quickstart\",\n"
            + "    \"kcClientSecret\": \"secret1\",\n"
            + "    \"kcPublicClientId\": \"entando-web\",\n"
            + "    \"kcSecureUris\": \"\",\n"
            + "    \"kcDefaultAuthorizations\": \"\",\n"
            + "    \"dbDriverClassName\": \"org.postgresql.Driver\",\n"
            + "    \"dbUrl\": \"jdbc:postgresql://testDbServer:5432/tenantDb1\",\n"
            + "    \"dbUsername\": \"db_user_2\",\n"
            + "    \"dbPassword\": \"db_password_2\",\n"
            + "    \"fqdns\": \"tenant1.com\"\n,"
            + "    \"customField1\": \"custom_value_1\"\n,"
            + "    \"customField2\": \"custom_value_2\""
            + "}]";

    private String tenantWithPrimaryCodeConfigs="[{\n"
            + "    \"tenantCode\": \"primary\",\n"
            + "    \"fqdns\": \"tenant1.com,tenant2.com\",\n"
            + "    \"kcEnabled\": true,\n"
            + "    \"kcAuthUrl\": \"http://tenant2.test.nip.io/auth\",\n"
            + "    \"kcRealm\": \"tenant2\",\n"
            + "    \"kcClientId\": \"quickstart\",\n"
            + "    \"kcClientSecret\": \"secret2\",\n"
            + "    \"kcPublicClientId\": \"entando-web\",\n"
            + "    \"kcSecureUris\": \"\",\n"
            + "    \"kcDefaultAuthorizations\": \"\",\n"
            + "    \"dbDriverClassName\": \"org.postgresql.Driver\",\n"
            + "    \"dbUrl\": \"jdbc:postgresql://testDbServer:5432/tenantDb2\",\n"
            + "    \"dbUsername\": \"db_user_1\",\n"
            + "    \"dbPassword\": \"db_password_1\"\n"
            + "}]\n";

    private String errorToCheck = "Error status for tenant with code '%s' is not ready please visit health status endpoint to check";

    @Test
    void shouldAllOperationWorkFineWithConfigMapsWithCustomFields() throws Throwable {
        TenantManager tm = new TenantManager(tenantConfigsWithCustomFields, new ObjectMapper(), new TenantAsynchInitService());
        tm.afterPropertiesSet();
        tm.startAsynchInitializeTenants().join();

        Optional<TenantConfig> otc = tm.getConfig("TE_nant1");
        Assertions.assertThat(otc).isNotEmpty();
        TenantConfig tc = otc.get();
        Optional<String> customValue1 = tc.getProperty("customField1");
        Optional<String> customValue2 = tc.getProperty("customField2");
        Optional<String> customValue3 = tc.getProperty("customField3");
        Assertions.assertThat(customValue1).isNotEmpty().hasValue("custom_value_1");
        Assertions.assertThat(customValue2).isNotEmpty().hasValue("custom_value_2");
        Assertions.assertThat(customValue3).isEmpty();
    }
    @Test
    void shouldAllOperationWorkFineWithCorrectInput() throws Throwable {

        TenantManager tm = new TenantManager(tenantConfigs, new ObjectMapper(), new TenantAsynchInitService());
        tm.afterPropertiesSet();
        tm.startAsynchInitializeTenants().join();

        Optional<TenantConfig> otc = tm.getConfig("TE_nant1");
        Assertions.assertThat(otc).isNotEmpty();
        TenantConfig tc = otc.get();
        Assertions.assertThat(tc.isKcEnabled()).isTrue();
        otc = tm.getTenantConfigByDomain("tenant1.com");
        Assertions.assertThat(otc).isNotEmpty();
        tc = otc.get();
        Assertions.assertThat(tc.getFqdns()).contains("tenant1.com");
        BasicDataSource ds = (BasicDataSource)tm.getDatasource("TE_nant1");
        Assertions.assertThat(ds.getDriverClassName()).isEqualTo("org.postgresql.Driver");
        Assertions.assertThat(tm.exists("pippo")).isFalse();
        tm.release();

        ds = (BasicDataSource)tm.getDatasource("TE_nant_not_found");
        Assertions.assertThat(ds).isNull();

    }

    @Test
    void shouldAllOperationWorkFineWithBadInput() throws Throwable {
        TenantManager tm = new TenantManager("[\"pippo\"pippo]", new ObjectMapper(), new TenantAsynchInitService());
        Assertions.catchThrowableOfType(() -> tm.afterPropertiesSet(), JsonMappingException.class);

        Optional<TenantConfig> otc = tm.getConfig("TE_nant1");
        Assertions.assertThat(otc).isEmpty();

        otc = tm.getTenantConfigByDomain("tenant2.com");
        Assertions.assertThat(otc).isEmpty();

        BasicDataSource ds = (BasicDataSource)tm.getDatasource("TE_nant_not_found");
        Assertions.assertThat(ds).isNull();

    }

    @Test
    void shouldInitThrowExceptionWithTenantCodeWithValuePrimary() throws Throwable {
        TenantManager tm = new TenantManager(tenantWithPrimaryCodeConfigs, new ObjectMapper(), new TenantAsynchInitService());
        RuntimeException ex = Assertions.catchThrowableOfType(() -> tm.afterPropertiesSet(), RuntimeException.class);
        Assertions.assertThat(ex.getMessage()).isEqualTo("You cannot use 'primary' as tenant code");

        Optional<TenantConfig> otc = tm.getConfig("TE_nant1");
        Assertions.assertThat(otc).isEmpty();

        otc = tm.getTenantConfigByDomain("tenant2.com");
        Assertions.assertThat(otc).isEmpty();

        BasicDataSource ds = (BasicDataSource)tm.getDatasource("TE_nant_not_found");
        Assertions.assertThat(ds).isNull();

    }

    @Test
    void shouldOperationThrowExceptionWithTenantNotInitiated() throws Throwable {
        TenantManager tm = new TenantManager(tenantConfigs, new ObjectMapper(), new TenantAsynchInitService());
        tm.afterPropertiesSet();

        RuntimeException ex = Assertions.catchThrowableOfType(() -> tm.getConfig("TE_nant1"), RuntimeException.class);
        Assertions.assertThat(ex.getMessage()).isEqualTo(String.format(errorToCheck,"TE_nant1"));

        ex = Assertions.catchThrowableOfType(() -> tm.getTenantConfigByDomain("tenant2.com"), RuntimeException.class);
        Assertions.assertThat(ex.getMessage()).isEqualTo(String.format(errorToCheck,"TE_nant1"));

        ex = Assertions.catchThrowableOfType(() -> tm.getDatasource("TE_nant1"), RuntimeException.class);
        Assertions.assertThat(ex.getMessage()).isEqualTo(String.format(errorToCheck,"TE_nant1"));

        Optional<TenantConfig> otc = tm.getConfig("TE_pippo123");
        Assertions.assertThat(otc).isEmpty();

        otc = tm.getTenantConfigByDomain("pippo123.com");
        Assertions.assertThat(otc).isEmpty();

        BasicDataSource ds = (BasicDataSource)tm.getDatasource("TE_nant_not_found");
        Assertions.assertThat(ds).isNull();

    }

}
