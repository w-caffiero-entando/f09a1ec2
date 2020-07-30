/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.plugins.jpseo.web.page;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import javax.servlet.http.HttpSession;
import org.entando.entando.aps.system.services.page.PageAuthorizationService;
import org.entando.entando.plugins.jpseo.aps.system.services.page.SeoPageDto;
import org.entando.entando.plugins.jpseo.aps.system.services.page.SeoPageService;
import org.entando.entando.plugins.jpseo.web.page.validator.SeoPageValidator;
import org.entando.entando.web.AbstractControllerTest;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class SeoPageControllerTest extends AbstractControllerTest {

    private static final String PAGE_CODE = "TST";
    private static final String STATUS_DRAFT = "draft";

    @Mock
    private SeoPageValidator validator;

    @Mock
    private SeoPageService service;

    @Mock
    private PageAuthorizationService authorizationService;

    @Mock
    private HttpSession httpSession;

    @InjectMocks
    private SeoPageController controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .addInterceptors(entandoOauth2Interceptor)
                .setHandlerExceptionResolvers(createHandlerExceptionResolver())
                .build();
    }

    @Test
    public void testGetSeoPage() throws Exception {
        SeoPageDto seoPageDto = new SeoPageDto();
        UserDetails user = this.createUser(true);
        when(this.httpSession.getAttribute("user")).thenReturn(user);
        when(this.service.getPage(Mockito.eq(PAGE_CODE),Mockito.eq(STATUS_DRAFT)))
                .thenReturn(seoPageDto);
        when(this.authorizationService.isAuth(user,PAGE_CODE)).thenReturn(true);
        ResultActions result = getSeoPage(PAGE_CODE, STATUS_DRAFT, user);
        result.andExpect(status().isOk());
    }

    @Test
    public void testGetSeoPageNotAuthorized() throws Exception {
        SeoPageDto seoPageDto = new SeoPageDto();
        UserDetails user = this.createUser(true);
        when(this.httpSession.getAttribute("user")).thenReturn(user);
        when(this.service.getPage(Mockito.eq(PAGE_CODE),Mockito.eq(STATUS_DRAFT)))
                .thenReturn(seoPageDto);
        when(this.authorizationService.isAuth(user,PAGE_CODE)).thenReturn(false);
        ResultActions result = getSeoPage(PAGE_CODE, STATUS_DRAFT, user);
        result.andExpect(status().isUnauthorized());
    }

    private ResultActions getSeoPage(String pageCode,String status, UserDetails user) throws Exception {
        String accessToken = mockOAuthInterceptor(user);
        String path = "/plugins/seo/pages/{pageCode}";
        return mockMvc.perform(
                get(path, pageCode)
                        .sessionAttr("user", user)
                        .param("status", status)
                        .header("Authorization", "Bearer " + accessToken));
    }

    private UserDetails createUser(boolean adminAuth) throws Exception {
        UserDetails user = (adminAuth) ? (new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.ADMINS_GROUP_NAME, "roletest", Permission.SUPERUSER)
                .build())
                : (new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                        .withAuthorization(Group.FREE_GROUP_NAME, "roletest", Permission.MANAGE_PAGES)
                        .build());
        return user;
    }
}
