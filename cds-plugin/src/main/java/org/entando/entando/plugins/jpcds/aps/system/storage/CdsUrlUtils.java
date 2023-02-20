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
package org.entando.entando.plugins.jpcds.aps.system.storage;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.core.UriBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.services.tenants.TenantConfig;

public final class CdsUrlUtils {

    private static final String CDS_PUBLIC_URL_TENANT_PARAM = "cdsPublicUrl";
    private static final String CDS_PRIVATE_URL_TENANT_PARAM = "cdsPrivateUrl";
    private static final String CDS_PATH_TENANT_PARAM = "cdsPath";
    private static final String URL_SEP = "/";
    private static final String SECTION_PUBLIC = "/public";
    private static final String SECTION_PRIVATE = "/protected";

    private CdsUrlUtils(){
    }

    public static String getInternalSection(boolean isProtectedResource) {
        return (isProtectedResource) ? SECTION_PRIVATE : SECTION_PUBLIC;
    }

    public static URI buildCdsExternalPublicResourceUrl(Optional<TenantConfig> config, CdsConfiguration configuration, String ... paths){
        String publicUrl = config.flatMap(c -> c.getProperty(CDS_PUBLIC_URL_TENANT_PARAM)).orElse(configuration.getCdsPublicUrl());

        return CdsUrlBuilder.builder().url(publicUrl).paths(paths).build();

    }

    public static URI buildCdsInternalApiUrl(Optional<TenantConfig> config, CdsConfiguration configuration, String ... paths){
        String apiUrl = config.flatMap(c -> c.getProperty(CDS_PRIVATE_URL_TENANT_PARAM)).orElse(configuration.getCdsPrivateUrl());
        String basePath = config.flatMap(c -> c.getProperty(CDS_PATH_TENANT_PARAM)).orElse(configuration.getCdsPath());

        return CdsUrlBuilder.builder().url(apiUrl).path(basePath).paths(paths).build();
    }

    public static String fetchBaseUrl(Optional<TenantConfig> config, CdsConfiguration configuration, boolean usePrivateUrl) {
        String privateUrl = config.flatMap(c -> c.getProperty(CDS_PRIVATE_URL_TENANT_PARAM)).orElse(configuration.getCdsPrivateUrl());
        String publicUrl = config.flatMap(c -> c.getProperty(CDS_PUBLIC_URL_TENANT_PARAM)).orElse(configuration.getCdsPublicUrl());

        String baseUrl = usePrivateUrl ? privateUrl : publicUrl;

        return StringUtils.removeEnd(baseUrl, URL_SEP);
    }

    public static EntSubPath extractPathAndFilename(String subPath) {
        if(StringUtils.isBlank(subPath)){
            subPath = "";
        }

        String path = "";
        String filename = subPath;
        int sepIndex = subPath.lastIndexOf(URL_SEP);
        if (sepIndex >= 0) {
            path = subPath.substring(0, sepIndex);
            filename = subPath.substring(sepIndex + 1);
        }
        return EntSubPath.builder().path(path).fileName(filename).build();

    }

    public static class CdsUrlBuilder {
        private String url;
        private List<String> paths = new ArrayList<>();

        private CdsUrlBuilder(){}

        public static CdsUrlBuilder builder() {
            return new CdsUrlBuilder();
        }

        public CdsUrlBuilder url(String u){
            this.url = u;
            return this;
        }

        public CdsUrlBuilder url(URI u){
            this.url = u.toString();
            return this;
        }

        public CdsUrlBuilder path(String p){
            paths.add(p);
            return this;
        }

        public CdsUrlBuilder paths(String ... u){
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


    @Getter
    @Setter
    @ToString
    @Builder
    public static class EntSubPath {
        private String path;
        private String fileName;
    }

}
