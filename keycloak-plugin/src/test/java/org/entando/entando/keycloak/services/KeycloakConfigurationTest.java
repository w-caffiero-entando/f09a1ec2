package org.entando.entando.keycloak.services;

import static org.mockito.ArgumentMatchers.any;

import com.agiletec.aps.util.ApsTenantApplicationUtils;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.entando.entando.aps.system.services.tenants.TenantConfig;
import org.entando.entando.aps.system.services.tenants.TenantManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KeycloakConfigurationTest {

    @Mock
    private TenantManager tenantManager;

    @Test
    void shouldConfigManageDefaultAndTenants(){
        KeycloakConfiguration kc = new KeycloakConfiguration();
        kc.setTenantManager(tenantManager);

        // we need this because there is no filter or http request
        ApsTenantApplicationUtils.setTenant("my-test-tenant");

        kc.setEnabled(true);
        kc.setClientId("default-client-id");
        kc.setClientSecret("default-client-secret");
        kc.setAuthUrl("/default-auth-url");
        kc.setSecureUris("/default-secure-uris");
        kc.setDefaultAuthorizations("default-auths");
        kc.setRealm("default-realm");
        kc.setPublicClientId("default-public-client-id");

        Assertions.assertThat(kc.isEnabled()).isTrue();
        Assertions.assertThat(kc.getClientId()).isEqualTo("default-client-id");
        Assertions.assertThat(kc.getClientSecret()).isEqualTo("default-client-secret");
        Assertions.assertThat(kc.getAuthUrl()).isEqualTo("/default-auth-url");
        Assertions.assertThat(kc.getSecureUris()).isEqualTo("/default-secure-uris");
        Assertions.assertThat(kc.getDefaultAuthorizations()).isEqualTo("default-auths");
        Assertions.assertThat(kc.getRealm()).isEqualTo("default-realm");
        Assertions.assertThat(kc.getPublicClientId()).isEqualTo("default-public-client-id");

        Map<String,String> map = Map.of("kcClientId","tenant-client-id",
                "kcEnabled","False",
                "kcRealm","tenant-realm",
                "kcClientSecret","",
                "kcAuthUrl","/tenant-auth-url",
                "kcSecureUris","/tenant-secure-uris");

        TenantConfig tc = new TenantConfig(map);
        Mockito.when(tenantManager.getConfig(any())).thenReturn(tc);
        Assertions.assertThat(kc.isEnabled()).isFalse();
        Assertions.assertThat(kc.getClientId()).isEqualTo("tenant-client-id");
        Assertions.assertThat(kc.getClientSecret()).isEqualTo("default-client-secret");
        Assertions.assertThat(kc.getRealm()).isEqualTo("tenant-realm");
        Assertions.assertThat(kc.getSecureUris()).isEqualTo("/tenant-secure-uris");
        Assertions.assertThat(kc.getAuthUrl()).isEqualTo("/tenant-auth-url");
    }

}
