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
package org.entando.entando.web.tenant;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import com.jayway.jsonpath.JsonPath;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class TenantControllerIntegrationTest extends AbstractControllerIntegrationTest {


    @Test
    void shouldCurrentTenantReturnAlwaysResults() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization("administrators", "user", new String[]{Permission.ENTER_BACKEND})
                .build();
        String accessToken = mockOAuthInterceptor(user);

        ResultActions result = mockMvc
                .perform(get("/currentTenant")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.primary", Matchers.is(true)));
    }

    @Test
    void shouldGetTenantReturnPrimary() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization("administrators", "user", new String[]{Permission.ENTER_BACKEND})
                .build();
        String accessToken = mockOAuthInterceptor(user);

        ResultActions result = mockMvc
                .perform(get("/tenants/{tenant}","primary")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.primary", Matchers.is(true)))
                .andExpect(jsonPath("$.payload.code", Matchers.is("primary")));
    }

    @Test
    void shouldGetTenantReturn404ForBadInput() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization("administrators", "user", new String[]{Permission.ENTER_BACKEND})
                .build();
        String accessToken = mockOAuthInterceptor(user);

        ResultActions result = mockMvc
                .perform(get("/tenants/{tenant}","tenant1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));

        result.andDo(resultPrint())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRetrieveTenants() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization("administrators", "user", new String[]{Permission.ENTER_BACKEND})
                .build();
        String accessToken = mockOAuthInterceptor(user);

        ResultActions result = mockMvc
                .perform(get("/tenants")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));

        result.andDo(resultPrint())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", Matchers.is(1)))
                .andExpect(jsonPath("$.payload.[0].code", Matchers.is("primary")));
    }

    @Test
    void shouldPermissionWorkFine() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization("administrators", "user", new String[]{Permission.CONTENT_EDITOR})
                .build();
        String accessToken = mockOAuthInterceptor(user);

        ResultActions result = mockMvc
                .perform(get("/tenants/{tenant}","primary")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isForbidden());

        result = mockMvc
                .perform(get("/tenants")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isForbidden());

        result = mockMvc
                .perform(get("/currentTenant")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isForbidden());

    }

}
