/*
 * Copyright 2023-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.servlet.security;

import org.entando.entando.keycloak.services.KeycloakConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AnonymousConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@ExtendWith(MockitoExtension.class)
class KeycloakSecurityConfigTest {
    
    @Mock
    private KeycloakAuthenticationFilter keycloakAuthenticationFilter;
    
    @Mock
    private KeycloakConfiguration configuration;
    
    private KeycloakSecurityConfig securityConfig;

    @BeforeEach
    public void setUp() {
        this.securityConfig = new KeycloakSecurityConfig(keycloakAuthenticationFilter, configuration);
    }
    
    @Test
    void shouldExecuteSessionSettings() throws Exception {
        Mockito.when(configuration.isEnabled()).thenReturn(true);
        HttpSecurity http = Mockito.mock(HttpSecurity.class);
        SessionManagementConfigurer configurer = Mockito.mock(SessionManagementConfigurer.class);
        Mockito.when(http.sessionManagement()).thenReturn(configurer);
        Mockito.when(configurer.sessionCreationPolicy(Mockito.any())).thenReturn(configurer);
        Mockito.when(configurer.and()).thenReturn(http);
        HeadersConfigurer headersConfigurer = Mockito.mock(HeadersConfigurer.class);
        Mockito.when(http.headers()).thenReturn(headersConfigurer);
        FrameOptionsConfig frameOptionsConfig = Mockito.mock(FrameOptionsConfig.class);
        Mockito.when(headersConfigurer.frameOptions()).thenReturn(frameOptionsConfig);
        Mockito.when(headersConfigurer.and()).thenReturn(http);
        Mockito.when(frameOptionsConfig.sameOrigin()).thenReturn(headersConfigurer);
        
        AnonymousConfigurer anonymousConfigurer = Mockito.mock(AnonymousConfigurer.class);
        Mockito.when(http.addFilterBefore(keycloakAuthenticationFilter, BasicAuthenticationFilter.class)).thenReturn(http);
        Mockito.when(http.anonymous()).thenReturn(anonymousConfigurer);
        Mockito.when(anonymousConfigurer.disable()).thenReturn(http);
        
        CsrfConfigurer csfrConfigurer = Mockito.mock(CsrfConfigurer.class);
        Mockito.when(http.csrf()).thenReturn(csfrConfigurer);
        Mockito.when(csfrConfigurer.disable()).thenReturn(http);
        
        this.securityConfig.configure(http);
        
        Mockito.verify(configurer, Mockito.times(1)).sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
    }
    
}
