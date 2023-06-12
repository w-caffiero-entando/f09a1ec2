/*
 * Copyright 2023-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.plugins.jacms.web.component;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.services.user.UserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.util.List;
import java.util.Map;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class ComponentControllerIntegrationTest extends AbstractControllerIntegrationTest {
    
    private ObjectMapper mapper = new ObjectMapper();
    
    @Test
    void extractCategoryUsageDetailsShouldReturnContents() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        List<Map<String, String>> request = List.of(
                Map.of("type", "category", "code", "general"));
        String payload = mapper.writeValueAsString(request);
        ResultActions result = mockMvc.perform(
                post("/components/usageDetails")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        String bodyResult = result.andReturn().getResponse().getContentAsString();
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()", is(1)));
        result.andExpect(jsonPath("$[0].type", is("category")));
        result.andExpect(jsonPath("$[0].code", is("general")));
        result.andExpect(jsonPath("$[0].exist", is(true)));
        result.andExpect(jsonPath("$[0].usage", is(9)));
        result.andExpect(jsonPath("$[0].references.size()", is(9)));
        List<String> contents = List.of("ART102", "ART111", "ART120", "ART122", "EVN23", "EVN25");
        List<String> categories = List.of("general_cat1", "general_cat2", "general_cat3");
        for (int i = 0; i < 9; i++) {
            String type = JsonPath.read(bodyResult, "$[0].references[" + i + "].type");
            String code = JsonPath.read(bodyResult, "$[0].references[" + i + "].code");
            if (type.equals("content")) {
                Assertions.assertTrue(contents.contains(code));
                result.andExpect(jsonPath("$[0].references[" + i + "].online", is(true)));
            } else {
                Assertions.assertEquals("category", type);
                Assertions.assertTrue(categories.contains(code));
                result.andExpect(jsonPath("$[0].references[" + i + "].online").doesNotExist());
            }
        }
    }
    
    @Test
    void extractCategoryUsageDetailsShouldReturnResources() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        List<Map<String, String>> request = List.of(
                Map.of("type", "category", "code", "resource_root"));
        String payload = mapper.writeValueAsString(request);
        ResultActions result = mockMvc.perform(
                post("/components/usageDetails")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
        String bodyResult = result.andReturn().getResponse().getContentAsString();
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()", is(1)));
        result.andExpect(jsonPath("$[0].type", is("category")));
        result.andExpect(jsonPath("$[0].code", is("resource_root")));
        result.andExpect(jsonPath("$[0].exist", is(true)));
        result.andExpect(jsonPath("$[0].usage", is(4)));
        result.andExpect(jsonPath("$[0].references.size()", is(4)));
        List<String> resources = List.of("44", "8");
        List<String> categories = List.of("Attach", "Image");
        for (int i = 0; i < 4; i++) {
            String type = JsonPath.read(bodyResult, "$[0].references[" + i + "].type");
            String code = JsonPath.read(bodyResult, "$[0].references[" + i + "].code");
            if (type.equals("asset")) {
                Assertions.assertTrue(resources.contains(code));
            } else {
                Assertions.assertEquals("category", type);
                Assertions.assertTrue(categories.contains(code));
                result.andExpect(jsonPath("$[0].references[" + i + "].online").doesNotExist());
            }
        }
    }
    
    @Test
    void extractPageUsageDetails() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        List<Map<String, String>> request = List.of(
                Map.of("type", "page", "code", "pagina_11"));
        String payload = mapper.writeValueAsString(request);
        ResultActions result = mockMvc.perform(
                post("/components/usageDetails")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        String bodyResult = result.andReturn().getResponse().getContentAsString();
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()", is(1)));
        result.andExpect(jsonPath("$[0].type", is("page")));
        result.andExpect(jsonPath("$[0].code", is("pagina_11")));
        result.andExpect(jsonPath("$[0].exist", is(true)));
        result.andExpect(jsonPath("$[0].online", is(false)));
        result.andExpect(jsonPath("$[0].usage", is(3)));
        result.andExpect(jsonPath("$[0].references.size()", is(3)));
        List<String> contents = List.of("EVN193", "EVN194");
        for (int i = 0; i < 3; i++) {
            String type = JsonPath.read(bodyResult, "$[0].references[" + i + "].type");
            String code = JsonPath.read(bodyResult, "$[0].references[" + i + "].code");
            if (type.equals("page")) {
                Assertions.assertEquals("pagina_11", code);
                result.andExpect(jsonPath("$[0].references[" + i + "].online", is(true)));
            } else {
                Assertions.assertEquals("content", type);
                Assertions.assertTrue(contents.contains(code));
                result.andExpect(jsonPath("$[0].references[" + i + "].online").exists());
            }
        }
    }
    
    @Test
    void extractContentTypeUsageDetails() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        List<Map<String, String>> request = List.of(
                Map.of("type", "contentType", "code", "ART"));
        String payload = mapper.writeValueAsString(request);
        ResultActions result = mockMvc.perform(
                post("/components/usageDetails")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        String bodyResult = result.andReturn().getResponse().getContentAsString();
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()", is(1)));
        result.andExpect(jsonPath("$[0].type", is("contentType")));
        result.andExpect(jsonPath("$[0].code", is("ART")));
        result.andExpect(jsonPath("$[0].exist", is(true)));
        result.andExpect(jsonPath("$[0].online").doesNotExist());
        int size = JsonPath.read(bodyResult, "$[0].usage");
        Assertions.assertEquals(15, size);
        result.andExpect(jsonPath("$[0].references.size()", is(size)));
        List<String> contentModels = List.of("1", "11", "2", "3");
        for (int i = 0; i < size; i++) {
            String type = JsonPath.read(bodyResult, "$[0].references[" + i + "].type");
            String code = JsonPath.read(bodyResult, "$[0].references[" + i + "].code");
            if (type.equals("content")) {
                Assertions.assertTrue(code.startsWith("ART"));
                boolean online = JsonPath.read(bodyResult, "$[0].references[" + i + "].online");
                Assertions.assertEquals(!code.equals("ART179"), online);
            } else {
                Assertions.assertEquals("contentTemplate", type);
                Assertions.assertTrue(contentModels.contains(code));
                result.andExpect(jsonPath("$[0].references[" + i + "].online").doesNotExist());
            }
        }
    }
    
    @Disabled
    @Test
    void extractContentTemplateUsageDetails() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        List<Map<String, String>> request = List.of(
                Map.of("type", "contentTemplate", "code", "11"));
        String payload = mapper.writeValueAsString(request);
        ResultActions result = mockMvc.perform(
                post("/components/usageDetails")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        String bodyResult = result.andReturn().getResponse().getContentAsString();
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()", is(1)));
        result.andExpect(jsonPath("$[0].type", is("contentTemplate")));
        result.andExpect(jsonPath("$[0].code", is("11")));
        result.andExpect(jsonPath("$[0].exist", is(true)));
        result.andExpect(jsonPath("$[0].online").doesNotExist());
        int size = JsonPath.read(bodyResult, "$[0].usage");
        Assertions.assertEquals(3, size);
        result.andExpect(jsonPath("$[0].references.size()", is(size)));
        for (int i = 0; i < size; i++) {
            String type = JsonPath.read(bodyResult, "$[0].references[" + i + "].type");
            String code = JsonPath.read(bodyResult, "$[0].references[" + i + "].code");
            if (type.equals("contentType")) {
                Assertions.assertTrue(code.startsWith("ART"));
                result.andExpect(jsonPath("$[0].references[" + i + "].online").doesNotExist());
            } else {
                Assertions.assertEquals("page", type);
                Assertions.assertEquals("homepage", code);
                result.andExpect(jsonPath("$[0].references[" + i + "].online").exists());
            }
        }
    }
    
    //@Disabled
    @Test
    void extractContentUsageDetails() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);
        List<Map<String, String>> request = List.of(
                Map.of("type", "content", "code", "ART111"));
        String payload = mapper.writeValueAsString(request);
        ResultActions result = mockMvc.perform(
                post("/components/usageDetails")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        String bodyResult = result.andReturn().getResponse().getContentAsString();
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()", is(1)));
        result.andExpect(jsonPath("$[0].type", is("content")));
        result.andExpect(jsonPath("$[0].code", is("ART111")));
        result.andExpect(jsonPath("$[0].exist", is(true)));
        result.andExpect(jsonPath("$[0].online", is(true)));
        int size = JsonPath.read(bodyResult, "$[0].usage");
        Assertions.assertEquals(2, size);
        result.andExpect(jsonPath("$[0].references.size()", is(size)));
        for (int i = 0; i < size; i++) {
            String type = JsonPath.read(bodyResult, "$[0].references[" + i + "].type");
            String code = JsonPath.read(bodyResult, "$[0].references[" + i + "].code");
            if (type.equals("content")) {
                Assertions.assertEquals("ART102", code);
            } else {
                Assertions.assertEquals("page", type);
                Assertions.assertEquals("customers_page", code);
            }
            result.andExpect(jsonPath("$[0].references[" + i + "].online", is(true)));
        }
    }
    
}
