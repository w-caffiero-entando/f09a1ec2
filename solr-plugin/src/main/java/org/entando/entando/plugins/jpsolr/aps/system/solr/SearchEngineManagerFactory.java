package org.entando.entando.plugins.jpsolr.aps.system.solr;

import com.agiletec.aps.system.common.notify.INotifyManager;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.category.ICategoryManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.searchengine.ICmsSearchEngineManager;
import com.agiletec.plugins.jacms.aps.system.services.searchengine.SearchEngineDAOFactory;
import com.agiletec.plugins.jacms.aps.system.services.searchengine.SearchEngineManager;
import lombok.Setter;
import org.apache.http.impl.client.HttpClientBuilder;
import org.entando.entando.aps.system.services.cache.ICacheInfoManager;
import org.entando.entando.aps.system.services.searchengine.SolrEnvironmentVariables;
import org.entando.entando.aps.system.services.tenants.ITenantManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public class SearchEngineManagerFactory implements BeanFactoryAware {

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
    @Setter
    private INotifyManager notifyManager;

    private BeanFactory beanFactory;

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
            solrSearchEngineManager.setNotifyManager(notifyManager);
            solrSearchEngineManager.setBeanFactory(beanFactory);
            solrSearchEngineManager.setBeanName(JacmsSystemConstants.SEARCH_ENGINE_MANAGER);
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
            baseSearchEngineManager.setNotifyManager(notifyManager);
            baseSearchEngineManager.setBeanFactory(beanFactory);
            baseSearchEngineManager.setBeanName(JacmsSystemConstants.SEARCH_ENGINE_MANAGER);
            return baseSearchEngineManager;
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
