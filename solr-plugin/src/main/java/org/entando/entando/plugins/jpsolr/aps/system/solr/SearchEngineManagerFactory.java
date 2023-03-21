package org.entando.entando.plugins.jpsolr.aps.system.solr;

import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.category.ICategoryManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.searchengine.ICmsSearchEngineManager;
import com.agiletec.plugins.jacms.aps.system.services.searchengine.SearchEngineDAOFactory;
import com.agiletec.plugins.jacms.aps.system.services.searchengine.SearchEngineManager;
import lombok.Setter;
import org.apache.http.impl.client.HttpClientBuilder;
import org.entando.entando.aps.system.services.cache.ICacheInfoManager;
import org.entando.entando.aps.system.services.tenants.ITenantManager;
import org.entando.entando.plugins.jpsolr.SolrEnvironmentVariables;

public class SearchEngineManagerFactory {

    @Setter
    private String indexDiskRootFolder;
    @Setter
    private ConfigInterface configManager;
    @Setter
    private ILangManager langManager;
    @Setter
    private ICategoryManager categoryManager;
    @Setter
    private IContentManager contentManager;
    @Setter
    private ITenantManager tenantManager;
    @Setter
    private ICacheInfoManager cacheInfoManager;
    @Setter
    private HttpClientBuilder solrHttpClientBuilder;

    public ICmsSearchEngineManager createSearchEngineManager() throws Exception {
        if (SolrEnvironmentVariables.active()) {
            SolrProxyTenantAware solrProxy = new SolrProxyTenantAware(
                    langManager, categoryManager, tenantManager, solrHttpClientBuilder
            );
            solrProxy.afterPropertiesSet();
            SolrSearchEngineManager solrSearchEngineManager = new SolrSearchEngineManager();
            solrSearchEngineManager.setSolrProxy(solrProxy);
            solrSearchEngineManager.setContentManager(contentManager);
            solrSearchEngineManager.setLangManager(langManager);
            solrSearchEngineManager.setCacheInfoManager(cacheInfoManager);
            return solrSearchEngineManager;
        } else {
            SearchEngineDAOFactory factory = new SearchEngineDAOFactory();
            factory.setIndexDiskRootFolder(indexDiskRootFolder);
            factory.setConfigManager(configManager);
            factory.setLangManager(langManager);
            factory.setCategoryManager(categoryManager);
            SearchEngineManager baseSearchEngineManager = new SearchEngineManager();
            baseSearchEngineManager.setFactory(factory);
            baseSearchEngineManager.setContentManager(contentManager);
            return baseSearchEngineManager;
        }
    }
}
