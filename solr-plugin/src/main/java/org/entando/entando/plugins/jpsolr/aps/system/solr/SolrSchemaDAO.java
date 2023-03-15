package org.entando.entando.plugins.jpsolr.aps.system.solr;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
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
    public List<Map<String, Serializable>> getFields() {
        SchemaRequest.Fields getFieldsRequest = new SchemaRequest.Fields();
        List<Map<String, Serializable>> fields = new ArrayList<>();
        try {
            List<SimpleOrderedMap<Serializable>> items = (List<SimpleOrderedMap<Serializable>>)
                    solrClient.request(getFieldsRequest, this.solrCore).get("fields");
            for (SimpleOrderedMap<Serializable> item : items) {
                fields.add(item.asMap());
            }
        } catch (SolrServerException | IOException ex) {
            log.error("Error retrieving fields from Solr", ex);
        }
        return fields;
    }

    @Override
    public boolean addField(Map<String, ?> properties) {
        try {
            SchemaRequest.AddField addFieldRequest = new SchemaRequest.AddField((Map<String, Object>) properties);
            solrClient.request(addFieldRequest, this.solrCore);
        } catch (SolrServerException | IOException ex) {
            log.error("Error adding field to Solr", ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean replaceField(Map<String, ?> properties) {
        try {
            SchemaRequest.ReplaceField replaceFieldRequest =
                    new SchemaRequest.ReplaceField((Map<String, Object>) properties);
            solrClient.request(replaceFieldRequest, this.solrCore);
        } catch (SolrServerException | IOException ex) {
            log.error("Error replacing field in Solr", ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteField(String fieldKey) {
        try {
            SchemaRequest.DeleteField deleteFieldRequest = new SchemaRequest.DeleteField(fieldKey);
            solrClient.request(deleteFieldRequest, this.solrCore);
        } catch (SolrServerException | IOException ex) {
            log.error("Error deleting field in Solr", ex);
            return false;
        }
        return true;
    }
}
