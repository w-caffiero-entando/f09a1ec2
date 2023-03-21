package org.entando.entando.plugins.jpsolr.aps.system.solr;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolrHttpClientBuilderConfiguration {

    @Value("${SOLR_MAX_CONN_PER_ROUTE:32}")
    private int maxConnPerRoute;
    @Value("${SOLR_MAX_CONN_TOTAL:64}")
    private int maxConnTotal;
    @Value("${SOLR_CONN_REQ_TIMEOUT_MS:5000}")
    private int connReqTimeout;
    @Value("${SOLR_CONN_TIMEOUT_MS:5000}")
    private int connTimeout;
    @Value("${SOLR_CONN_SOCKET_TIMEOUT_MS:25000}")
    private int connSocketTimeout;

    @Bean("solrHttpClientBuilder")
    public HttpClientBuilder solrHttpClientBuilder() {
        return HttpClients.custom()
                .setMaxConnPerRoute(maxConnPerRoute)
                .setMaxConnTotal(maxConnTotal)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectionRequestTimeout(connReqTimeout)
                        .setConnectTimeout(connTimeout)
                        .setSocketTimeout(connSocketTimeout).build());
    }
}
