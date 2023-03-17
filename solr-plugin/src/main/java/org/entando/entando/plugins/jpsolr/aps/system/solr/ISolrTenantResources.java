package org.entando.entando.plugins.jpsolr.aps.system.solr;

public interface ISolrTenantResources {

    ISolrIndexerDAO getIndexerDAO();

    ISolrSearcherDAO getSearcherDAO();

    ISolrSchemaDAO getSolrSchemaDAO();

    String getSolrCore();

    int getStatus();

    void setStatus(int status);
}
