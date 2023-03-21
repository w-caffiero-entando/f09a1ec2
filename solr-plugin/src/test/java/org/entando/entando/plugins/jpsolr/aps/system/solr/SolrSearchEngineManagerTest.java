package org.entando.entando.plugins.jpsolr.aps.system.solr;

import static com.agiletec.plugins.jacms.aps.system.services.searchengine.ICmsSearchEngineManager.STATUS_NEED_TO_RELOAD_INDEXES;
import static com.agiletec.plugins.jacms.aps.system.services.searchengine.ICmsSearchEngineManager.STATUS_READY;
import static com.agiletec.plugins.jacms.aps.system.services.searchengine.ICmsSearchEngineManager.STATUS_RELOADING_INDEXES_IN_PROGRESS;
import static org.mockito.ArgumentMatchers.any;

import com.agiletec.aps.system.services.category.ICategoryManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.entando.entando.aps.system.services.cache.ICacheInfoManager;
import org.entando.entando.aps.system.services.tenants.ITenantManager;
import org.entando.entando.ent.exception.EntException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SolrSearchEngineManagerTest {

    @Mock
    private ILangManager langManager;
    @Mock
    private ICategoryManager categoryManager;
    @Mock
    private ITenantManager tenantManager;
    @Mock
    private HttpClientBuilder solrHttpClientBuilder;
    @Mock
    private IContentManager contentManager;
    @Mock
    private ICacheInfoManager cacheInfoManager;

    private SolrProxyTenantAware solrProxy;
    private SolrSearchEngineManager solrSearchEngineManager;

    private MockedConstruction<HttpSolrClient.Builder> mockedConstructionSolrClientBuilder;

    @BeforeEach
    void setUp() throws Exception {
        mockedConstructionSolrClientBuilder = Mockito.mockConstruction(HttpSolrClient.Builder.class,
                (builder, context) -> {
                    HttpSolrClient solrClient = Mockito.mock(HttpSolrClient.class);
                    Mockito.when(builder.withHttpClient(any())).thenReturn(builder);
                    Mockito.when(builder.build()).thenReturn(solrClient);
                });
        solrProxy = new SolrProxyTenantAware(
                langManager, categoryManager, tenantManager, solrHttpClientBuilder
        );
        solrProxy.afterPropertiesSet();
        solrSearchEngineManager = new SolrSearchEngineManager();
        solrSearchEngineManager.setSolrProxy(solrProxy);
        solrSearchEngineManager.setContentManager(contentManager);
        solrSearchEngineManager.setLangManager(langManager);
        solrSearchEngineManager.setCacheInfoManager(cacheInfoManager);
    }

    @AfterEach
    void tearDown() {
        mockedConstructionSolrClientBuilder.close();
    }

    @Test
    void shouldRollbackIndexStatusIfReloadThreadDoesNotStart() throws Exception {
        solrProxy.getIndexStatus().setValue(STATUS_NEED_TO_RELOAD_INDEXES);
        try (MockedConstruction<SolrIndexLoaderThread> c = Mockito.mockConstruction(SolrIndexLoaderThread.class,
                (thread, context) -> Mockito.doThrow(RuntimeException.class).when(thread).start())) {
            Assertions.assertThrows(EntException.class, () -> solrSearchEngineManager.startReloadContentsReferences());
        }
        Assertions.assertEquals(STATUS_NEED_TO_RELOAD_INDEXES, solrProxy.getIndexStatus().getValue());
    }

    @Test
    void shouldSetStatusInProgressIfReloadThreadIsStartedStart() throws Exception {
        solrProxy.getIndexStatus().setValue(STATUS_READY);
        try (MockedConstruction<SolrIndexLoaderThread> c = Mockito.mockConstruction(SolrIndexLoaderThread.class,
                (thread, context) -> Mockito.doNothing().when(thread).start())) {
            Assertions.assertNotNull(solrSearchEngineManager.startReloadContentsReferences());
        }
        Assertions.assertEquals(STATUS_RELOADING_INDEXES_IN_PROGRESS, solrProxy.getIndexStatus().getValue());
    }

    @Test
    void shouldSkipReloadThreadIfReloadIsAlreadyInProgress() throws Exception {
        solrProxy.getIndexStatus().setValue(STATUS_RELOADING_INDEXES_IN_PROGRESS);
        Assertions.assertNull(solrSearchEngineManager.startReloadContentsReferences());
        Assertions.assertEquals(STATUS_RELOADING_INDEXES_IN_PROGRESS, solrProxy.getIndexStatus().getValue());
    }
}
