/*
 * Copyright 2022-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.util;

import com.google.common.net.HttpHeaders;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.net.ssl.HttpsURLConnection;
import javax.print.attribute.standard.ReferenceUriSchemesSupported;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.owasp.encoder.Encode;
import org.springframework.security.web.header.Header;

@Slf4j
public final class UrlUtils {

    public static final String ENTANDO_APP_USE_TLS = "ENTANDO_APP_USE_TLS";
    public static final String HTTP_SCHEME = "http";
    public static final String HTTPS_SCHEME = "https";

    private UrlUtils(){}

    public static String fetchScheme(HttpServletRequest request){
        return getProtoFromEnv().filter(UrlUtils::isHttps)
                .orElseGet(() -> getProtoFromXHeader(request).filter(UrlUtils::isHttps).orElse(request.getScheme()));
    }

    private static Optional<String> getProtoFromEnv(){
        if( BooleanUtils.toBoolean(System.getenv(ENTANDO_APP_USE_TLS)) ) {
            return Optional.ofNullable(HTTPS_SCHEME);
        } else {
            return Optional.ofNullable(HTTP_SCHEME);
        }
    }

    private static Optional<String> getProtoFromXHeader(HttpServletRequest request){
        return Optional.ofNullable(request.getHeader(HttpHeaders.X_FORWARDED_PROTO)).filter(StringUtils::isNotBlank);
    }

    private static boolean isHttps(String proto){
        return HTTPS_SCHEME.equals(proto);
    }

    public static String fetchServer(HttpServletRequest request){
        return getHostFromXHeader(request)
                .orElseGet(() -> getHostFromHeader(request).orElse(request.getServerName()));
        // FIXME encoding
        // seems valid only for port ... to encode ?
//        if (hostName.length() > serverName.length()) {
//            String encodedHostName = org.owasp.encoder.Encode.forHtmlContent(hostName);
//            link.append(encodedHostName.substring(serverName.length()));
//        }

    }

    private static Optional<String> getHostFromXHeader(HttpServletRequest request){
        return Optional.ofNullable(request.getHeader(HttpHeaders.X_FORWARDED_HOST)).filter(StringUtils::isNotBlank);
    }

    private static Optional<String> getHostFromHeader(HttpServletRequest request){
        return Optional.ofNullable(request.getHeader(HttpHeaders.HOST))
                .filter(StringUtils::isNotBlank)
                .filter(s -> s.startsWith(request.getServerName()))
                .map(s -> s.split(":")[0]);
    }

    public static int fetchPort(HttpServletRequest request) {
        return getPortFromXHeader(request)
                .orElseGet(() -> getPortFromHeader(request).orElse(request.getServerPort()));
    }

    private static Optional<Integer> getPortFromXHeader(HttpServletRequest request){
        return Optional.ofNullable(request.getHeader(HttpHeaders.X_FORWARDED_PORT)).filter(StringUtils::isNotBlank).map(Integer::valueOf);
    }

    private static Optional<Integer> getPortFromHeader(HttpServletRequest request){
        return Optional.ofNullable(request.getHeader(HttpHeaders.HOST))
                .filter(StringUtils::isNotBlank)
                .filter(s -> s.startsWith(request.getServerName()))
                .filter(s -> s.contains(":"))
                .map(s -> s.split(":"))
                .filter(part -> part.length > 1)
                .map(part -> part[1])
                .map(Encode::forHtmlContent) // FIXME is unuseful ???
                .map(Integer::valueOf);
    }

    public static Optional<String> fetchServerNameFromUri(String uri){
        return Optional.ofNullable(uri)
                .map(u -> getUri(u))
                .map(URI::getHost)
                .filter(StringUtils::isNotBlank);
    }

    public static Optional<String> fetchPathFromUri(String uri){
        return Optional.ofNullable(uri)
                .map(u -> getUri(u))
                .map(URI::getPath)
                .filter(StringUtils::isNotBlank);
    }

    public static Optional<String> removeContextRootFromPath(String path, HttpServletRequest request){
        return Optional.ofNullable(path).filter(StringUtils::isNotBlank).map(s -> {
            if(s.startsWith(request.getContextPath())){
                return s.replaceFirst(request.getContextPath(), "");
            }
            return s;
        }).filter(StringUtils::isNotBlank);
    }


    private static URI getUri(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException ex) {
            log.debug("error with url:'{}'", url, ex);
            return null;
        }
    }

    public static class EntUrlBuilder {
        private String url;
        private List<String> paths = new ArrayList<>();

        private EntUrlBuilder(){}

        public static EntUrlBuilder builder() {
            return new EntUrlBuilder();
        }

        public EntUrlBuilder url(String u){
            this.url = u;
            return this;
        }

        public EntUrlBuilder url(URI u){
            this.url = u.toString();
            return this;
        }

        public EntUrlBuilder path(String p){
            paths.add(p);
            return this;
        }

        public EntUrlBuilder paths(String ... u){
            paths.addAll(Arrays.asList(u));
            return this;
        }

        public URI build(){
            UriBuilder builder = UriBuilder.fromUri(url);
            for (String path : paths) {
                if(StringUtils.isNotBlank(path)) {
                    builder.path(path.trim());
                }
            }
            return builder.build();
        }
    }

}
