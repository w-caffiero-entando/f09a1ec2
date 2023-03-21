package org.entando.entando.plugins.jpsolr.aps.system.solr;

import static com.agiletec.plugins.jacms.aps.system.services.searchengine.ICmsSearchEngineManager.STATUS_NEED_TO_RELOAD_INDEXES;
import static com.agiletec.plugins.jacms.aps.system.services.searchengine.ICmsSearchEngineManager.STATUS_READY;
import static com.agiletec.plugins.jacms.aps.system.services.searchengine.ICmsSearchEngineManager.STATUS_RELOADING_INDEXES_IN_PROGRESS;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SolrIndexStatusTest {

    @Test
    void shouldSetReadyIfPossible() {
        SolrIndexStatus status = new SolrIndexStatus();
        status.setValue(STATUS_RELOADING_INDEXES_IN_PROGRESS);
        status.setReadyIfPossible();
        Assertions.assertEquals(STATUS_READY, status.getValue());
    }

    @Test
    void shouldNotSetReadyIfNotPossible() {
        SolrIndexStatus status = new SolrIndexStatus();
        status.setValue(STATUS_NEED_TO_RELOAD_INDEXES);
        status.setReadyIfPossible();
        Assertions.assertEquals(STATUS_NEED_TO_RELOAD_INDEXES, status.getValue());
    }

    @Test
    void reloadIndexesShouldBePossibleIfStatusIsReady() {
        SolrIndexStatus status = new SolrIndexStatus();
        status.setValue(STATUS_NEED_TO_RELOAD_INDEXES);
        Assertions.assertTrue(status.canReloadIndexes());
    }

    @Test
    void reloadIndexesShouldBePossibleIfStatusIsNeedToReloadIndexes() {
        SolrIndexStatus status = new SolrIndexStatus();
        status.setValue(STATUS_NEED_TO_RELOAD_INDEXES);
        Assertions.assertTrue(status.canReloadIndexes());
    }

    @Test
    void reloadIndexesShouldNotBePossibleIfStatusIsReloadingIndexesInProgress() {
        SolrIndexStatus status = new SolrIndexStatus();
        status.setValue(STATUS_RELOADING_INDEXES_IN_PROGRESS);
        Assertions.assertFalse(status.canReloadIndexes());
    }
}
