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
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.role.Role;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonValue;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.UnmarshalUtils;
import org.entando.entando.aps.system.services.api.model.AbstractApiResponse;
import org.entando.entando.aps.system.services.api.model.ApiError;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.StringApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author E.Santoboni
 */
@RestController
@RequestMapping("/legacy")
public class ApiRestServer {

    private static final Logger logger = LoggerFactory.getLogger(ApiRestServer.class);

    @GetMapping(value = "/{langCode}/{resourceName}.xml",
            produces = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public Object doGetXml(@PathVariable String langCode, @PathVariable String resourceName,
            HttpServletRequest request) {
        return this.buildGetDeleteResponse(langCode, ApiMethod.HttpMethod.GET, null, resourceName, request);
    }

    @GetMapping(value = "/{langCode}/{resourceName}.json",
            produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public Object doGetJson(@PathVariable String langCode, @PathVariable String resourceName,
            HttpServletRequest request) {
        return this.buildGetDeleteResponse(langCode, ApiMethod.HttpMethod.GET, null, resourceName, request);
    }

    @GetMapping(value = "/{langCode}/{resourceName}",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE,
                    org.springframework.http.MediaType.TEXT_PLAIN_VALUE,
                    org.springframework.http.MediaType.APPLICATION_JSON_VALUE})
    public Object doGet(@PathVariable String langCode, @PathVariable String resourceName,
            HttpServletRequest request) {
        return this.buildGetDeleteResponse(langCode, ApiMethod.HttpMethod.GET, null, resourceName, request);
    }

    @GetMapping(value = "/{langCode}/{namespace}/{resourceName}.xml",
            produces = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public Object doGetXml(@PathVariable String langCode, @PathVariable String namespace,
            @PathVariable String resourceName, HttpServletRequest request) {
        return this.buildGetDeleteResponse(langCode, ApiMethod.HttpMethod.GET, namespace, resourceName, request);
    }

    @GetMapping(value = "/{langCode}/{namespace}/{resourceName}.json",
            produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public Object doGetJson(@PathVariable String langCode, @PathVariable String namespace,
            @PathVariable String resourceName, HttpServletRequest request) {
        return this.buildGetDeleteResponse(langCode, ApiMethod.HttpMethod.GET, namespace, resourceName, request);
    }

    @GetMapping(value = "/{langCode}/{namespace}/{resourceName}",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE,
                    org.springframework.http.MediaType.TEXT_PLAIN_VALUE,
                    org.springframework.http.MediaType.APPLICATION_JSON_VALUE})
    public Object doGet(@PathVariable String langCode, @PathVariable String namespace,
            @PathVariable String resourceName, HttpServletRequest request) {
        return this.buildGetDeleteResponse(langCode, ApiMethod.HttpMethod.GET, namespace, resourceName, request);
    }

    @PostMapping(value = "/{langCode}/{resourceName}.xml",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public Object doPostXmlFromXmlBody(@PathVariable String langCode, @PathVariable String resourceName,
            HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.POST, null, resourceName, request, response,
                MediaType.APPLICATION_XML);
    }

    @PostMapping(value = "/{langCode}/{resourceName}.json",
            produces = {org.springframework.http.MediaType.APPLICATION_JSON_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public Object doPostJsonFromXmlBody(@PathVariable String langCode, @PathVariable String resourceName,
            HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.POST, null, resourceName, request, response,
                MediaType.APPLICATION_XML);
    }

    @PostMapping(value = "/{langCode}/{resourceName}",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE,
                    org.springframework.http.MediaType.APPLICATION_JSON_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public Object doPostFromXmlBody(@PathVariable String langCode, @PathVariable String resourceName,
            HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.POST, null, resourceName, request, response,
                MediaType.APPLICATION_XML);
    }

    @PostMapping(value = "/{langCode}/{namespace}/{resourceName}.xml",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public Object doPostXmlFromXmlBody(@PathVariable String langCode, @PathVariable String namespace,
            @PathVariable String resourceName, HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.POST, namespace, resourceName, request,
                response, MediaType.APPLICATION_XML);
    }

    @PostMapping(value = "/{langCode}/{namespace}/{resourceName}.json",
            produces = {org.springframework.http.MediaType.APPLICATION_JSON_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public Object doPostJsonFromXmlBody(@PathVariable String langCode, @PathVariable String namespace,
            @PathVariable String resourceName, HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.POST, namespace, resourceName, request,
                response, MediaType.APPLICATION_XML);
    }

    @PostMapping(value = "/{langCode}/{namespace}/{resourceName}",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE,
                    org.springframework.http.MediaType.APPLICATION_JSON_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public Object doPostFromXmlBody(@PathVariable String langCode, @PathVariable String namespace,
            @PathVariable String resourceName, HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.POST, namespace, resourceName, request,
                response, MediaType.APPLICATION_XML);
    }

    @PostMapping(value = "/{langCode}/{resourceName}.xml",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public Object doPostXmlFromJsonBody(@PathVariable String langCode, @PathVariable String resourceName,
            HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.POST, null, resourceName, request, response,
                MediaType.APPLICATION_JSON);
    }

    @PostMapping(value = "/{langCode}/{resourceName}.json",
            produces = {org.springframework.http.MediaType.APPLICATION_JSON_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public Object doPostJsonFromJsonBody(@PathVariable String langCode, @PathVariable String resourceName,
            HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.POST, null, resourceName, request, response,
                MediaType.APPLICATION_JSON);
    }

    @PostMapping(value = "/{langCode}/{resourceName}",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE,
                    org.springframework.http.MediaType.APPLICATION_JSON_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public Object doPostFromJsonBody(@PathVariable String langCode, @PathVariable String resourceName,
            HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.POST, null, resourceName, request, response,
                MediaType.APPLICATION_JSON);
    }

    @PostMapping(value = "/{langCode}/{namespace}/{resourceName}.json",
            produces = {org.springframework.http.MediaType.APPLICATION_JSON_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public Object doPostJsonFromJsonBody(@PathVariable String langCode, @PathVariable String namespace,
            @PathVariable String resourceName, HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.POST, namespace, resourceName, request,
                response, MediaType.APPLICATION_JSON);
    }

    @PostMapping(value = "/{langCode}/{namespace}/{resourceName}",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE,
                    org.springframework.http.MediaType.APPLICATION_JSON_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public Object doPostFromJsonBody(@PathVariable String langCode, @PathVariable String namespace,
            @PathVariable String resourceName, HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.POST, namespace, resourceName, request,
                response, MediaType.APPLICATION_JSON);
    }

    @PutMapping(value = "/{langCode}/{resourceName}.json",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public Object doPutXmlFromXmlBody(@PathVariable String langCode, @PathVariable String resourceName,
            HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.PUT, null, resourceName, request, response,
                MediaType.APPLICATION_XML);
    }

    @PutMapping(value = "/{langCode}/{resourceName}.json",
            produces = {org.springframework.http.MediaType.APPLICATION_JSON_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public Object doPutJsonFromXmlBody(@PathVariable String langCode, @PathVariable String resourceName,
            HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.PUT, null, resourceName, request, response,
                MediaType.APPLICATION_XML);
    }

    @PutMapping(value = "/{langCode}/{resourceName}",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE,
                    org.springframework.http.MediaType.APPLICATION_JSON_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public Object doPutFromXmlBody(@PathVariable String langCode, @PathVariable String resourceName,
            HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.PUT, null, resourceName, request, response,
                MediaType.APPLICATION_XML);
    }

    @PutMapping(value = "/{langCode}/{namespace}/{resourceName}.xml",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public Object doPutXmlFromXmlBody(@PathVariable String langCode, @PathVariable String namespace,
            @PathVariable String resourceName, HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.PUT, namespace, resourceName, request, response,
                MediaType.APPLICATION_XML);
    }

    @PutMapping(value = "/{langCode}/{namespace}/{resourceName}.json",
            produces = {org.springframework.http.MediaType.APPLICATION_JSON_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public Object doPutJsonFromXmlBody(@PathVariable String langCode, @PathVariable String namespace,
            @PathVariable String resourceName, HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.PUT, namespace, resourceName, request, response,
                MediaType.APPLICATION_XML);
    }

    @PutMapping(value = "/{langCode}/{namespace}/{resourceName}",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE,
                    org.springframework.http.MediaType.APPLICATION_JSON_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public Object doPutFromXmlBody(@PathVariable String langCode, @PathVariable String namespace,
            @PathVariable String resourceName, HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.PUT, namespace, resourceName, request, response,
                MediaType.APPLICATION_XML);
    }

    @PutMapping(value = "/{langCode}/{resourceName}.xml",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public Object doPutXmlFromJsonBody(@PathVariable String langCode, @PathVariable String resourceName,
            HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.PUT, null, resourceName, request, response,
                MediaType.APPLICATION_JSON);
    }

    @PutMapping(value = "/{langCode}/{resourceName}.json",
            produces = {org.springframework.http.MediaType.APPLICATION_JSON_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public Object doPutJsonFromJsonBody(@PathVariable String langCode, @PathVariable String resourceName,
            HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.PUT, null, resourceName, request, response,
                MediaType.APPLICATION_JSON);
    }

    @PutMapping(value = "/{langCode}/{resourceName}",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE,
                    org.springframework.http.MediaType.APPLICATION_JSON_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public Object doPutFromJsonBody(@PathVariable String langCode, @PathVariable String resourceName,
            HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.PUT, null, resourceName, request, response,
                MediaType.APPLICATION_JSON);
    }

    @PutMapping(value = "/{langCode}/{namespace}/{resourceName}.xml",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public Object doPutXmlFromJsonBody(@PathVariable String langCode, @PathVariable String namespace,
            @PathVariable String resourceName, HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.PUT, namespace, resourceName, request, response,
                MediaType.APPLICATION_JSON);
    }

    @PutMapping(value = "/{langCode}/{namespace}/{resourceName}.json",
            produces = {org.springframework.http.MediaType.APPLICATION_JSON_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public Object doPutJsonFromJsonBody(@PathVariable String langCode, @PathVariable String namespace,
            @PathVariable String resourceName, HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.PUT, namespace, resourceName, request, response,
                MediaType.APPLICATION_JSON);
    }

    @PutMapping(value = "/{langCode}/{namespace}/{resourceName}",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE,
                    org.springframework.http.MediaType.APPLICATION_JSON_VALUE},
            consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public Object doPutFromJsonBody(@PathVariable String langCode, @PathVariable String namespace,
            @PathVariable String resourceName, HttpServletRequest request, HttpServletResponse response) {
        return this.buildPostPutResponse(langCode, ApiMethod.HttpMethod.PUT, namespace, resourceName, request, response,
                MediaType.APPLICATION_JSON);
    }

    @DeleteMapping(value = "/{langCode}/{resourceName}.xml",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE})
    public Object doDeleteXml(@PathVariable String langCode, @PathVariable String resourceName,
            HttpServletRequest request) {
        return this.buildGetDeleteResponse(langCode, ApiMethod.HttpMethod.DELETE, null, resourceName, request);
    }

    @DeleteMapping(value = "/{langCode}/{resourceName}.json",
            produces = {org.springframework.http.MediaType.APPLICATION_JSON_VALUE})
    public Object doDeleteJson(@PathVariable String langCode, @PathVariable String resourceName,
            HttpServletRequest request) {
        return this.buildGetDeleteResponse(langCode, ApiMethod.HttpMethod.DELETE, null, resourceName, request);
    }

    @DeleteMapping(value = "/{langCode}/{resourceName}",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE,
                    org.springframework.http.MediaType.APPLICATION_JSON_VALUE})
    public Object doDelete(@PathVariable String langCode, @PathVariable String resourceName,
            HttpServletRequest request) {
        return this.buildGetDeleteResponse(langCode, ApiMethod.HttpMethod.DELETE, null, resourceName, request);
    }

    @DeleteMapping(value = "/{langCode}/{namespace}/{resourceName}.xml",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE})
    public Object doDeleteXml(@PathVariable String langCode, @PathVariable String namespace,
            @PathVariable String resourceName, HttpServletRequest request) {
        return this.buildGetDeleteResponse(langCode, ApiMethod.HttpMethod.DELETE, namespace, resourceName, request);
    }

    @DeleteMapping(value = "/{langCode}/{namespace}/{resourceName}.json",
            produces = {org.springframework.http.MediaType.APPLICATION_JSON_VALUE})
    public Object doDeleteJson(@PathVariable String langCode, @PathVariable String namespace,
            @PathVariable String resourceName, HttpServletRequest request) {
        return this.buildGetDeleteResponse(langCode, ApiMethod.HttpMethod.DELETE, namespace, resourceName, request);
    }

    @DeleteMapping(value = "/{langCode}/{namespace}/{resourceName}",
            produces = {org.springframework.http.MediaType.APPLICATION_XML_VALUE})
    public Object doDelete(@PathVariable String langCode, @PathVariable String namespace,
            @PathVariable String resourceName, HttpServletRequest request) {
        return this.buildGetDeleteResponse(langCode, ApiMethod.HttpMethod.DELETE, namespace, resourceName, request);
    }

    protected Object buildGetDeleteResponse(String langCode, ApiMethod.HttpMethod httpMethod,
            String namespace, String resourceName, HttpServletRequest request) {
        Object responseObject;
        try {
            IResponseBuilder responseBuilder = (IResponseBuilder) ApsWebApplicationUtils.getBean(
                    SystemConstants.API_RESPONSE_BUILDER, request);
            Properties properties = this.extractProperties(langCode, request);
            ApiMethod apiMethod = responseBuilder.extractApiMethod(httpMethod, namespace, resourceName);
            this.extractOAuthParameters(request, apiMethod, properties);
            responseObject = responseBuilder.createResponse(apiMethod, properties);
        } catch (ApiException ae) {
            responseObject = this.buildErrorResponse(httpMethod, namespace, resourceName, ae);
        } catch (Throwable t) {
            responseObject = this.buildErrorResponse(httpMethod, namespace, resourceName, t);
        }
        return this.createResponseEntity(responseObject);
    }

    protected Object buildPostPutResponse(String langCode, ApiMethod.HttpMethod httpMethod,
                                          String namespace, String resourceName, HttpServletRequest request, HttpServletResponse response, MediaType mediaType) {
        Object responseObject = null;
        try {
            IResponseBuilder responseBuilder = (IResponseBuilder) ApsWebApplicationUtils.getBean(SystemConstants.API_RESPONSE_BUILDER, request);
            Properties properties = this.extractProperties(langCode, request);
            ApiMethod apiMethod = responseBuilder.extractApiMethod(httpMethod, namespace, resourceName);
            this.extractOAuthParameters(request, apiMethod, properties);
            Object bodyObject = UnmarshalUtils.unmarshal(apiMethod.getExpectedType(), request, mediaType);
            responseObject = responseBuilder.createResponse(apiMethod, bodyObject, properties);
        } catch (ApiException ae) {
            responseObject = this.buildErrorResponse(httpMethod, namespace, resourceName, ae);
        } catch (Throwable t) {
            responseObject = this.buildErrorResponse(httpMethod, namespace, resourceName, t);
        }
        return this.createResponseEntity(responseObject);
    }

    protected Properties extractProperties(String langCode, HttpServletRequest request) throws Exception {
        ILangManager langManager = (ILangManager) ApsWebApplicationUtils.getBean(SystemConstants.LANGUAGE_MANAGER, request);
        Properties properties = this.extractRequestParameters(request);
        if (null == langManager.getLang(langCode)) {
            langCode = langManager.getDefaultLang().getCode();
        }
        String applicationBaseUrl = this.extractApplicationBaseUrl(request);
        if (null != applicationBaseUrl) {
            properties.put(SystemConstants.API_APPLICATION_BASE_URL_PARAMETER, applicationBaseUrl);
        }
        properties.put(SystemConstants.API_LANG_CODE_PARAMETER, langCode);
        properties.put(SystemConstants.API_PRODUCES_MEDIA_TYPE_PARAMETER, this.extractProducesMediaType(request));
        return properties;
    }

    protected Properties extractRequestParameters(HttpServletRequest request) throws Exception {
        Properties properties = new Properties();
        List<String> reservedParameters = SystemConstants.API_RESERVED_PARAMETERS;
        Enumeration<String> enumParameterNames = request.getParameterNames();
        while (enumParameterNames.hasMoreElements()) {
            String key = enumParameterNames.nextElement();
            if (!reservedParameters.contains(key)) {
                String[] values = request.getParameterValues(key);
                String value = values[0]; // extract only the first value
                properties.put(key, URLDecoder.decode(value, "UTF-8"));
            }
        }
        return properties;
    }

    protected String extractApplicationBaseUrl(HttpServletRequest request) {
        String applicationBaseUrl = null;
        try {
            IURLManager urlManager = (IURLManager) ApsWebApplicationUtils.getBean(SystemConstants.URL_MANAGER, request);
            applicationBaseUrl = urlManager.getApplicationBaseURL(request);
        } catch (Exception t) {
            logger.error("Error extracting application base url", t);
        }
        return applicationBaseUrl;
    }

    protected MediaType extractProducesMediaType(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        int index = pathInfo.indexOf('.');
        if (index < 0) {
            return MediaType.APPLICATION_XML;
        }
        String extension = pathInfo.substring(index + 1);
        if (extension.equalsIgnoreCase("json")) {
            return MediaType.APPLICATION_JSON;
        } else {
            return MediaType.APPLICATION_XML;
        }
    }

    protected StringApiResponse buildErrorResponse(ApiMethod.HttpMethod httpMethod, String namespace, String resourceName, Throwable t) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Method '").append(httpMethod).append("' Resource '").append(resourceName).append("'");
        if (null != namespace) {
            buffer.append(" Namespace '").append(namespace).append("'");
        }
        final String message = buffer.toString();
        logger.error("Error building api response  - {}", message, t);
        StringApiResponse response = new StringApiResponse();
        if (t instanceof ApiException) {
            response.addErrors(((ApiException) t).getErrors());
        } else {
            ApiError error = new ApiError(IApiErrorCodes.SERVER_ERROR, "Error building response - " + message, HttpStatus.INTERNAL_SERVER_ERROR);
            response.addError(error);
        }
        response.setResult(IResponseBuilder.FAILURE, null);
        return response;
    }

    protected void extractOAuthParameters(HttpServletRequest request, ApiMethod apiMethod, Properties properties) throws ApiException {
        IAuthorizationManager authManager = (IAuthorizationManager) ApsWebApplicationUtils.getBean(SystemConstants.AUTHORIZATION_SERVICE, request);
        properties.put(SystemConstants.API_REQUEST_PARAMETER, request);
        String permission = apiMethod.getRequiredPermission();
        logger.debug("Permission required: {}", permission);

        LegacyApiUserExtractor legacyApiUserExtractor = (LegacyApiUserExtractor) ApsWebApplicationUtils.getBean(SystemConstants.LEGACY_API_USER_EXTRACTOR, request);

        UserDetails user = legacyApiUserExtractor.getUser(request);

        if (null != user) {
            String username = user.getUsername();
            if (permission != null && !authManager.isAuthOnPermission(user, permission)) {
                List<Role> roles = authManager.getUserRoles(user);
                for (Role role : roles) {
                    logger.debug("User {} requesting resource has {} permission ", username, (null != role.getPermissions()) ? role.getPermissions().toString() : "");
                }
                throw new ApiException(IApiErrorCodes.API_AUTHORIZATION_REQUIRED, "Authorization Required", HttpStatus.FORBIDDEN);
            }
        } else if (apiMethod.getRequiredAuth()) {
            throw new ApiException(IApiErrorCodes.API_AUTHENTICATION_REQUIRED, "Authentication Required", HttpStatus.UNAUTHORIZED);
        }
    }

    protected ResponseEntity<?> createResponseEntity(Object responseObject) {
        HttpStatus status;
        if (responseObject instanceof AbstractApiResponse) {
            AbstractApiResponse mainResponse = (AbstractApiResponse) responseObject;
            status = this.extractResponseStatus(mainResponse.getErrors());
        } else {
            status = HttpStatus.OK;
        }
        if (responseObject instanceof String) {
            return new ResponseEntity<>(new JsonResponse(responseObject.toString()), status);
        }
        return new ResponseEntity<>(responseObject, HttpStatus.valueOf(status.value()));
    }

    protected class JsonResponse {

        private final String value;

        public JsonResponse(String value) {
            this.value = value;
        }

        @JsonValue
        @JsonRawValue
        public String value() {
            return value;
        }
    }

    protected HttpStatus extractResponseStatus(List<ApiError> errors) {
        HttpStatus status = HttpStatus.OK;
        if (null != errors) {
            for (ApiError error : errors) {
                HttpStatus errorStatus = error.getStatus();
                if (null != errorStatus && status.value() < errorStatus.value()) {
                    status = errorStatus;
                }
            }
        }
        return status;
    }

}
