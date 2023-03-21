package org.entando.entando.plugins.jpsolr.aps.system.solr;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.plugins.jacms.aps.system.services.searchengine.IIndexerDAO;
import java.util.stream.Stream;
import org.entando.entando.ent.exception.EntException;

public interface ISolrIndexerDAO extends IIndexerDAO {

    void addBulk(Stream<IApsEntity> entityStream) throws EntException;

    boolean deleteAllDocuments();
}
