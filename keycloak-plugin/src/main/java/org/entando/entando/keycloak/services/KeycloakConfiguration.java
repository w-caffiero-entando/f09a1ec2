package org.entando.entando.keycloak.services;

import com.agiletec.aps.util.ApsTenantApplicationUtils;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.services.tenants.ITenantManager;
import org.entando.entando.aps.system.services.tenants.TenantConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class KeycloakConfiguration {

    private ITenantManager tenantManager;
    private boolean enabled;
    private String authUrl;
    private String realm;
    private String clientId;
    private String clientSecret;
    private String publicClientId;
    private String secureUris;
    private String defaultAuthorizations;

    @Autowired
    public void setTenantManager(ITenantManager tenantManager) {
        this.tenantManager = tenantManager;
    }

    public boolean isEnabled() {
        return getCurrentConfig()
                .map(TenantConfig::isKcEnabled)
                .orElse(enabled);
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAuthUrl() {
        return getCurrentConfig()
                .map(TenantConfig::getKcAuthUrl)
                .filter(StringUtils::isNotBlank)
                .orElse(authUrl);
    }
    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public String getRealm() {
        return getCurrentConfig()
                .map(TenantConfig::getKcRealm)
                .filter(StringUtils::isNotBlank)
                .orElse(realm);
    }
    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getClientId() {
        return getCurrentConfig()
                .map(TenantConfig::getKcClientId)
                // FIXME we should fix for blank ...
                .filter(StringUtils::isNotBlank)
                .orElse(clientId);
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return getCurrentConfig()
                .map(TenantConfig::getKcClientSecret)
                .filter(StringUtils::isNotBlank)
                .orElse(clientSecret);
    }
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getPublicClientId() {
        return getCurrentConfig()
                .map(TenantConfig::getKcPublicClientId)
                .filter(StringUtils::isNotBlank)
                .orElse(publicClientId);
    }
    public void setPublicClientId(String publicClientId) {
        this.publicClientId = publicClientId;
    }

    public String getSecureUris() {
        return getCurrentConfig()
                .map(TenantConfig::getKcSecureUris)
                .filter(StringUtils::isNotBlank)
                .orElse(secureUris);
    }
    public void setSecureUris(String secureUris) {
        this.secureUris = secureUris;
    }

    public String getDefaultAuthorizations() {
        return getCurrentConfig()
                .map(TenantConfig::getKcDefaultAuthorizations)
                .filter(StringUtils::isNotBlank)
                .orElse(defaultAuthorizations);
    }
    public void setDefaultAuthorizations(String defaultAuthorizations) {
        this.defaultAuthorizations = defaultAuthorizations;
    }

    private Optional<TenantConfig> getCurrentConfig() {
        return ApsTenantApplicationUtils.getTenant()
                .filter(StringUtils::isNotBlank)
                .map(tenantManager::getConfig);
    }
}
