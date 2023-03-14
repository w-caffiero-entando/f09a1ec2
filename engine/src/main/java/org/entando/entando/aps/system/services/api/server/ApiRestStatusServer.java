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
package org.entando.entando.aps.system.services.api.server;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.model.LegacyApiError;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.StringApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author E.Santoboni
 */
@Slf4j
@RestController
@RequestMapping(value = "/legacy/status")
public class ApiRestStatusServer {

    @GetMapping(value = "/{resourceName}/{httpMethod}.xml",
            produces = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public Object getApiStatusXml(@PathVariable String resourceName,
            @PathVariable String httpMethod, HttpServletRequest request) {
        return this.executeGetApiStatus(httpMethod, null, resourceName, request);
    }

    @GetMapping(value = "/{resourceName}/{httpMethod}.json",
            produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public Object getApiStatusJson(@PathVariable String resourceName,
            @PathVariable String httpMethod, HttpServletRequest request) {
        return this.executeGetApiStatus(httpMethod, null, resourceName, request);
    }

    @GetMapping(value = "/{resourceName}/{httpMethod}",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE,
                    org.springframework.http.MediaType.APPLICATION_JSON_VALUE})
    public Object getApiStatus(@PathVariable String resourceName,
            @PathVariable String httpMethod, HttpServletRequest request) {
        return this.executeGetApiStatus(httpMethod, null, resourceName, request);
    }

    @GetMapping(value = "/{namespace}/{resourceName}/{httpMethod}.xml",
            produces = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public Object getApiStatusXml(@PathVariable String namespace,
            @PathVariable String resourceName, @PathVariable String httpMethod, HttpServletRequest request) {
        return this.executeGetApiStatus(httpMethod, namespace, resourceName, request);
    }

    @GetMapping(value = "/{namespace}/{resourceName}/{httpMethod}.json",
            produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public Object getApiStatusJson(@PathVariable String namespace,
            @PathVariable String resourceName, @PathVariable String httpMethod, HttpServletRequest request) {
        return this.executeGetApiStatus(httpMethod, namespace, resourceName, request);
    }

    @GetMapping(value = "/{namespace}/{resourceName}/{httpMethod}",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE,
                    org.springframework.http.MediaType.APPLICATION_JSON_VALUE})
    public Object getApiStatus(@PathVariable String namespace,
            @PathVariable String resourceName, @PathVariable String httpMethod, HttpServletRequest request) {
        return this.executeGetApiStatus(httpMethod, namespace, resourceName, request);
    }

    protected ResponseEntity<StringApiResponse> executeGetApiStatus(String httpMethodString,
            String namespace, String resourceName, HttpServletRequest request) {
        StringApiResponse response = new StringApiResponse();
        ApiMethod.HttpMethod httpMethod = Enum.valueOf(ApiMethod.HttpMethod.class, httpMethodString.toUpperCase());
        try {
            IResponseBuilder responseBuilder = (IResponseBuilder) ApsWebApplicationUtils.getBean(
                    SystemConstants.API_RESPONSE_BUILDER, request);
            ApiMethod apiMethod = responseBuilder.extractApiMethod(httpMethod, namespace, resourceName);
            if (null != apiMethod.getRequiredPermission()) {
                response.setResult(ApiStatus.AUTHORIZATION_REQUIRED.toString(), null);
            } else if (apiMethod.getRequiredAuth()) {
                response.setResult(ApiStatus.AUTHENTICATION_REQUIRED.toString(), null);
            } else {
                response.setResult(ApiStatus.FREE.toString(), null);
            }
        } catch (ApiException ae) {
            response.addErrors(ae.getErrors());
            response.setResult(ApiStatus.INACTIVE.toString(), null);
        } catch (Throwable t) {
            return this.buildErrorResponse(httpMethod, namespace, resourceName, t);
        }
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<StringApiResponse> buildErrorResponse(ApiMethod.HttpMethod httpMethod,
            String namespace, String resourceName, Throwable t) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Method '").append(httpMethod).
                append("' Namespace '").append(namespace).append("' Resource '").append(resourceName).append("'");
        log.error("Error building api response  - {}", buffer, t);
        StringApiResponse response = new StringApiResponse();
        LegacyApiError error = new LegacyApiError(IApiErrorCodes.SERVER_ERROR, "Error building response - " + buffer,
                HttpStatus.INTERNAL_SERVER_ERROR);
        response.addError(error);
        response.setResult(IResponseBuilder.FAILURE, null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    public enum ApiStatus {
        FREE, INACTIVE, AUTHENTICATION_REQUIRED, AUTHORIZATION_REQUIRED
    }

}
