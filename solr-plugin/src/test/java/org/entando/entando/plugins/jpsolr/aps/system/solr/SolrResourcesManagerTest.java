package org.entando.entando.plugins.jpsolr.aps.system.solr;

import static org.mockito.ArgumentMatchers.any;

import com.agiletec.aps.system.services.category.ICategoryManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.util.ApsTenantApplicationUtils;
import java.util.Optional;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.entando.entando.aps.system.services.tenants.ITenantManager;
import org.entando.entando.aps.system.services.tenants.TenantConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SolrResourcesManagerTest {

    private static final String TENANT_1 = "tenant1";

    @Mock
    private ILangManager langManager;
    @Mock
    private ICategoryManager categoryManager;
    @Mock
    private ITenantManager tenantManager;
    @Mock
    private HttpClientBuilder solrHttpClientBuilder;

    @InjectMocks
    private SolrProxyTenantAware resourcesManager;

    private MockedConstruction<HttpSolrClient.Builder> mockedConstructionSolrClientBuilder;

    @BeforeEach
    void setUp() throws Exception {
        mockedConstructionSolrClientBuilder = Mockito.mockConstruction(HttpSolrClient.Builder.class,
                (builder, context) -> {
                    HttpSolrClient solrClient = Mockito.mock(HttpSolrClient.class);
                    Mockito.when(builder.withHttpClient(any())).thenReturn(builder);
                    Mockito.when(builder.build()).thenReturn(solrClient);
                });
        resourcesManager.afterPropertiesSet();
    }

    @AfterEach
    void tearDown() {
        mockedConstructionSolrClientBuilder.close();
    }

    @Test
    void shouldCreatePrimaryIndexerAndSearcher() throws Exception {
        Assertions.assertNotNull(resourcesManager.getIndexerDAO());
        Assertions.assertNotNull(resourcesManager.getSearcherDAO());
    }

    @Test
    void shouldCreateTenantIndexerAndSearcher() throws Exception {
        mockTenantConfig();
        try (MockedStatic<ApsTenantApplicationUtils> tenantUtils
                = Mockito.mockStatic(ApsTenantApplicationUtils.class)) {
            tenantUtils.when(() -> ApsTenantApplicationUtils.getTenant()).thenReturn(Optional.of(TENANT_1));
            resourcesManager.init();
            Assertions.assertNotNull(resourcesManager.getIndexerDAO());
            Assertions.assertNotNull(resourcesManager.getSearcherDAO());
        }
    }

    private void mockTenantConfig() {
        TenantConfig tenantConfig = Mockito.mock(TenantConfig.class);
        Mockito.when(tenantConfig.getProperty("solrAddress")).thenReturn(Optional.of("http://localhost:9999/solr"));
        Mockito.when(tenantConfig.getProperty("solrCore")).thenReturn(Optional.of(TENANT_1));
        Mockito.when(tenantManager.getConfig(TENANT_1)).thenReturn(Optional.of(tenantConfig));
    }
}
