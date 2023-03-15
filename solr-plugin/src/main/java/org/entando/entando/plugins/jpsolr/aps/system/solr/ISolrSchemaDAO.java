package org.entando.entando.plugins.jpsolr.aps.system.solr;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface ISolrSchemaDAO {

    List<Map<String, Serializable>> getFields();

    boolean addField(Map<String, ?> properties);

    boolean replaceField(Map<String, ?> properties);

    boolean deleteField(String fieldKey);
}
