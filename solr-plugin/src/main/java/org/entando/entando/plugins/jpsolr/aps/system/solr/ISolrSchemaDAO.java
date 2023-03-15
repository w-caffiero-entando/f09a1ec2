package org.entando.entando.plugins.jpsolr.aps.system.solr;

import java.util.List;
import java.util.Map;

public interface ISolrSchemaDAO {

    List<Map<String, ?>> getFields();

    boolean updateFields(List<Map<String, ?>> fieldsToAdd, List<Map<String, ?>> fieldsToReplace);
}
