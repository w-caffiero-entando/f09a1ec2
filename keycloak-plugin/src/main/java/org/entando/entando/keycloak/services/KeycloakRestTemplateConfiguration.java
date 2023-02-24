/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.entando.entando.keycloak.services;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class KeycloakRestTemplateConfiguration {

    @Value("${MAX_CONN_PER_ROUTE:32}")
    private int maxConnPerRoute;
    @Value("${MAX_CONN_TOTAL:64}")
    private int maxConnTotal;
    @Value("${CONN_REQ_TIMEOUT_MS:5000}")
    private int connReqTimeout;
    @Value("${CONN_TIMEOUT_MS:5000}")
    private int connTimeout;
    @Value("${CONN_SOCKET_TIMEOUT_MS:25000}")
    private int connSocketTimeout;

    @Bean(name="keycloakRestTemplate")
    public RestTemplate keycloakRestTemplate() {
        HttpClientBuilder builder = HttpClients.custom()
                .setMaxConnPerRoute(maxConnPerRoute)
                .setMaxConnTotal(maxConnTotal)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectionRequestTimeout(connReqTimeout)
                        .setConnectTimeout(connTimeout)
                        .setSocketTimeout(connSocketTimeout).build());

        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setHttpClient(builder.build());

        return new RestTemplate(httpRequestFactory);
    }

    @Bean(name="keycloakRestTemplateWithRedirect")
    public RestTemplate keycloakRestTemplateWithRedirect() {
        HttpClientBuilder builder = HttpClients.custom()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setMaxConnPerRoute(maxConnPerRoute)
                .setMaxConnTotal(maxConnTotal)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectionRequestTimeout(connReqTimeout)
                        .setConnectTimeout(connTimeout)
                        .setSocketTimeout(connSocketTimeout).build());

        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setHttpClient(builder.build());

        return new RestTemplate(httpRequestFactory);
    }

}
