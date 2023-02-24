package org.entando.entando.plugins.jpsolr.aps.system.solr;

import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.category.ICategoryManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.searchengine.ICmsSearchEngineManager;
import com.agiletec.plugins.jacms.aps.system.services.searchengine.SearchEngineManager;
import org.entando.entando.aps.system.services.cache.ICacheInfoManager;
import org.entando.entando.aps.system.services.tenants.ITenantManager;
import org.entando.entando.plugins.jpsolr.SolrEnvironmentVariables;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchEngineManagerFactoryTest {

    @Mock
    private ConfigInterface configManager;
    @Mock
    private ILangManager langManager;
    @Mock
    private ICategoryManager categoryManager;
    @Mock
    private IContentManager contentManager;
    @Mock
    private ITenantManager tenantManager;
    @Mock
    private ICacheInfoManager cacheInfoManager;

    @InjectMocks
    private SearchEngineManagerFactory factory;

    @Test
    void shouldLoadDefaultSearchEngineIfSolrIsNotActive() throws Exception {
        try (MockedStatic<SolrEnvironmentVariables> solrEnvStaticMock = Mockito.mockStatic(
                SolrEnvironmentVariables.class)) {
            solrEnvStaticMock.when(() -> SolrEnvironmentVariables.active()).thenReturn(false);
            ICmsSearchEngineManager manager = factory.createSearchEngineManager();
            Assertions.assertEquals(SearchEngineManager.class, manager.getClass());
        }
    }

    @Test
    void shouldLoadSolrSearchEngineIfSolrIsActive() throws Exception {
        try (MockedStatic<SolrEnvironmentVariables> solrEnvStaticMock = Mockito.mockStatic(
                SolrEnvironmentVariables.class)) {
            solrEnvStaticMock.when(() -> SolrEnvironmentVariables.active()).thenReturn(true);
            ICmsSearchEngineManager manager = factory.createSearchEngineManager();
            Assertions.assertEquals(SolrSearchEngineManager.class, manager.getClass());
        }
    }
}
