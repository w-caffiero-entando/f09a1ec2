package org.entando.entando.aps.system.services.tenants;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class TenantManagerTest {

    private String tenantConfigs="[{\n"
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
            + "    \"domainPrefix\": \"tenant1\"\n"
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

    @Test
    void shouldAllOperationWorkFineWithCorrectInput() throws Throwable {
        TenantManager tm = new TenantManager(tenantConfigs, new ObjectMapper());
        tm.refresh();
        TenantConfig tc = tm.getConfig("TE_nant1");
        Assertions.assertThat(tc.isKcEnabled()).isTrue();
        tc = tm.getTenantConfigByDomainPrefix("tenant1");
        Assertions.assertThat(tc.getDomainPrefix()).isEqualTo("tenant1");
        BasicDataSource ds = (BasicDataSource)tm.getDatasource("TE_nant1");
        Assertions.assertThat(ds.getDriverClassName()).isEqualTo("org.postgresql.Driver");
        Assertions.assertThat(tm.exists("pippo")).isFalse();
        tm.release();

        ds = (BasicDataSource)tm.getDatasource("TE_nant_not_found");
        Assertions.assertThat(ds).isNull();
    }

    @Test
    void shouldAllOperationWorkFineWithBadInput() throws Throwable {
        TenantManager tm = new TenantManager("[\"pippo\"pippo]", new ObjectMapper());
        tm.refresh();
        TenantConfig tc = tm.getConfig("TE_nant1");
        Assertions.assertThat(tc).isNull();

        tc = tm.getTenantConfigByDomainPrefix("tenant1");
        Assertions.assertThat(tc).isNull();

        BasicDataSource ds = (BasicDataSource)tm.getDatasource("TE_nant_not_found");
        Assertions.assertThat(ds).isNull();

    }
}
