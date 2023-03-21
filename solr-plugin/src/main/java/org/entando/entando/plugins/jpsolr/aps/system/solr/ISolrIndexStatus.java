package org.entando.entando.plugins.jpsolr.aps.system.solr;

public interface ISolrIndexStatus {

    int getValue();

    void setValue(int value);

    boolean canReloadIndexes();

    void setReloadInProgress();

    void rollbackStatus();

    void setReadyIfPossible();
}
