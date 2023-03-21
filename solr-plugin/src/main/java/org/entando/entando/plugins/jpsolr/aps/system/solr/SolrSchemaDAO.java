package org.entando.entando.plugins.jpsolr.aps.system.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest.MultiUpdate;
import org.apache.solr.client.solrj.request.schema.SchemaRequest.Update;
import org.apache.solr.common.util.SimpleOrderedMap;

@Slf4j
public class SolrSchemaDAO implements ISolrSchemaDAO {

    private final SolrClient solrClient;
    private final String solrCore;

    public SolrSchemaDAO(SolrClient solrClient, String solrCore) {
        this.solrClient = solrClient;
        this.solrCore = solrCore;
    }

    @Override
    public List<Map<String, ?>> getFields() {
        SchemaRequest.Fields getFieldsRequest = new SchemaRequest.Fields();
        List<Map<String, ?>> fields = new ArrayList<>();
        try {
            List<SimpleOrderedMap<Object>> items = (List<SimpleOrderedMap<Object>>)
                    solrClient.request(getFieldsRequest, this.solrCore).get("fields");
            for (SimpleOrderedMap<Object> item : items) {
                fields.add(item.asMap());
            }
        } catch (SolrServerException | IOException ex) {
            log.error("Error retrieving fields from Solr", ex);
        }
        return fields;
    }

    public boolean updateFields(List<Map<String, ?>> fieldsToAdd, List<Map<String, ?>> fieldsToReplace) {
        try {
            List<Update> updates = new ArrayList<>();
            for (Map<String, ?> fieldToAdd : fieldsToAdd) {
                SchemaRequest.AddField addFieldRequest = new SchemaRequest.AddField((Map<String, Object>) fieldToAdd);
                updates.add(addFieldRequest);
            }
            for (Map<String, ?> fieldToReplace : fieldsToReplace) {
                SchemaRequest.ReplaceField replaceFieldRequest =
                        new SchemaRequest.ReplaceField((Map<String, Object>) fieldToReplace);
                updates.add(replaceFieldRequest);
            }
            SchemaRequest.MultiUpdate multiUpdateRequest = new MultiUpdate(updates);
            solrClient.request(multiUpdateRequest, this.solrCore);
        } catch (SolrServerException | IOException ex) {
            log.error("Error executing Solr multi-update request", ex);
            return false;
        }
        return true;
    }
}
