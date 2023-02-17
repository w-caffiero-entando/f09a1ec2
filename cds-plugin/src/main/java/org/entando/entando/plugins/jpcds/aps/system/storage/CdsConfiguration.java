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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@EqualsAndHashCode
@ToString
@Component
public class CdsConfiguration {

    @Value("${resourceRootURL}")
    private String baseURL;
    @Value("${resourceDiskRootFolder}")
    private String baseDiskRoot;
    @Value("${protectedResourceDiskRootFolder}")
    private String protectedBaseDiskRoot;
    @Value("${protectedResourceRootURL}")
    private String protectedBaseURL;
    @Value("${cds.enabled:false}")
    private boolean enabled;
    @Value("${cds.public.url:https://cds.entando.org}")
    private String cdsPublicUrl;
    @Value("${cds.private.url:https://cds.entando.org}")
    private String cdpPrivateUrl;
    @Value("${cds.path:/api/v1}")
    private String cdsPath;
    @Value("${keycloak.auth.url:http://localhost:8081/auth}")
    private String kcAuthUrl;
    @Value("${keycloak.realm:entando}")
    private String kcRealm;
    @Value("${keycloak.client.id:entando-app}")
    private String kcClientId;
    @Value("${keycloak.client.secret:}")
    private String kcClientSecret;


}
