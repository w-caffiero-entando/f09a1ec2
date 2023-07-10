/*
 * Copyright 2018-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.web.component;

import static org.hamcrest.CoreMatchers.is;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.services.user.UserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.entando.entando.aps.system.services.component.ComponentDeleteResponse;
import org.entando.entando.aps.system.services.component.ComponentDeleteResponse.ComponentDeleteResponseRow;
import org.entando.entando.aps.system.services.component.ComponentService;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.entando.entando.web.AbstractControllerTest;
import org.entando.entando.web.common.RestErrorCodes;
import org.entando.entando.web.component.validator.ComponentValidator;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ComponentControllerTest extends AbstractControllerTest {

    @Mock
    private ComponentService componentService;

    @Mock
    private ComponentValidator componentValidator;

    @InjectMocks
    private ComponentController controller;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .addInterceptors(entandoOauth2Interceptor)
                .setMessageConverters(getMessageConverters())
                .setHandlerExceptionResolvers(createHandlerExceptionResolver())
                .build();
    }

    @Test
    void invokeUsageDetailsWithServerError() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        when(componentService.extractComponentUsageDetails(Mockito.any())).thenThrow(new EntRuntimeException("Error extracting Component details"));
        List<Map<String, String>> request = List.of(
                Map.of("type", "pageModel", "code", "service"));
        String payload = new ObjectMapper().writeValueAsString(request);
        ResultActions result = mockMvc.perform(
                post("/components/usageDetails")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken)
        );
        result.andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.payload", Matchers.hasSize(0)))
                .andExpect(jsonPath("$.errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.errors[0].code", is(RestErrorCodes.INTERNAL_ERROR)));
        Mockito.verify(componentService, Mockito.times(1)).extractComponentUsageDetails(Mockito.any());
    }

    @Test
    void invokationOfDeleteComponentShouldReturnRightResponse() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        ComponentDeleteResponse serviceResponse = new ComponentDeleteResponse();
        serviceResponse.setStatus(ComponentDeleteResponse.STATUS_SUCCESS);
        ComponentDeleteResponseRow singleResult = ComponentDeleteResponseRow.builder()
                .type("customType")
                .code("internalCode")
                .status(ComponentDeleteResponse.STATUS_SUCCESS)
                .build();
        serviceResponse.getComponents().add(singleResult);
        List<Map<String, String>> request = List.of(
                Map.of("type", "customType", "code", "internalCode"));
        when(componentService.deleteInternalComponents(request)).thenReturn(serviceResponse);
        String payload = new ObjectMapper().writeValueAsString(request);
        ResultActions result = mockMvc.perform(
                delete("/components/allInternals/delete")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken)
        );
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()", is(3)));
        result.andExpect(jsonPath("$.metaData.size()", is(0)));
        result.andExpect(jsonPath("$.errors.size()", is(0)));
        result.andExpect(jsonPath("$.payload.size()", is(2)));
        result.andExpect(jsonPath("$.payload.status", is("success")));
        result.andExpect(jsonPath("$.payload.components[0].type", is("customType")));
        result.andExpect(jsonPath("$.payload.components[0].code", is("internalCode")));
        result.andExpect(jsonPath("$.payload.components[0].status", is("success")));
    }
    
}
