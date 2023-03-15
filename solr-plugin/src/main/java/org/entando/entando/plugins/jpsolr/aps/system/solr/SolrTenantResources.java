package org.entando.entando.plugins.jpsolr.aps.system.solr;

import com.agiletec.aps.system.services.category.ICategoryManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Getter;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

public class SolrTenantResources {

    private final ReentrantLock lock = new ReentrantLock();

    private final SolrClient solrClient;
    @Getter
    private final ISolrIndexerDAO indexerDAO;
    @Getter
    private final ISolrSearcherDAO searcherDAO;
    @Getter
    private final ISolrSchemaDAO solrSchemaDAO;

    public SolrTenantResources(String solrAddress, String solrCore, ILangManager langManager,
            ICategoryManager categoryManager) {
        this.solrClient = new HttpSolrClient.Builder(solrAddress)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();

        this.indexerDAO = new IndexerDAO(solrClient, solrCore);
        this.indexerDAO.setLangManager(langManager);
        this.indexerDAO.setTreeNodeManager(categoryManager);

        this.searcherDAO = new SearcherDAO(solrClient, solrCore);
        this.searcherDAO.setLangManager(langManager);
        this.searcherDAO.setTreeNodeManager(categoryManager);

        this.solrSchemaDAO = new SolrSchemaDAO(solrClient, solrCore);
    }

    public void close() throws IOException {
        solrClient.close();
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public boolean isLocked() {
        return lock.isLocked();
    }
}
