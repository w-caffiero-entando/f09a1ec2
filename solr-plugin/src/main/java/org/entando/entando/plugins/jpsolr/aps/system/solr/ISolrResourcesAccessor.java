package org.entando.entando.plugins.jpsolr.aps.system.solr;

/**
 * Provides the classes to define Solr schema structure and indexes and perform searches on a specific Solr core.
 * A new object implementing this interface is created for each tenant.
 */
public interface ISolrResourcesAccessor {

    ISolrIndexerDAO getIndexerDAO();

    ISolrSearcherDAO getSearcherDAO();

    ISolrSchemaDAO getSolrSchemaDAO();

    ISolrIndexStatus getIndexStatus();

    String getSolrCore();
}
