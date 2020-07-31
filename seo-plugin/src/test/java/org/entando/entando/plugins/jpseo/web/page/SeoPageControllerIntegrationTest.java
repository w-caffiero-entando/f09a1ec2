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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.FileTextReader;
import java.io.InputStream;
import org.entando.entando.aps.system.services.page.IPageService;
import org.entando.entando.aps.system.services.page.model.PageDto;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

public class SeoPageControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private IPageService pageService;

    private static String SEO_TEST_1 = "seoTest1";
    private static String SEO_TEST_2 = "seoTest2";

    @Test
    public void testPostSeoPage() throws Exception {
        try {
            String accessToken = this.createAccessToken();

            final ResultActions result = this
                    .executePostSeoPage("1_POST_valid.json", accessToken, status().isOk());

            Assert.assertNotNull(this.pageService.getPage(SEO_TEST_1, IPageService.STATUS_DRAFT));
            result.andExpect(jsonPath("$.errors.size()", is(0)))
                    .andExpect(jsonPath("$.payload.code", is(SEO_TEST_1)))
                    .andExpect(jsonPath("$.payload.status", is("unpublished")))
                    .andExpect(jsonPath("$.payload.onlineInstance", is(false)))
                    .andExpect(jsonPath("$.payload.displayedInMenu", is(true)))
                    .andExpect(jsonPath("$.payload.pageModel", is("service")))
                    .andExpect(jsonPath("$.payload.charset", is("utf-8")))
                    .andExpect(jsonPath("$.payload.contentType", is("text/html")))
                    .andExpect(jsonPath("$.payload.parentCode", is("service")))
                    .andExpect(jsonPath("$.payload.seo", is(true)))
                    .andExpect(jsonPath("$.payload.titles.size()", is(2)))
                    .andExpect(jsonPath("$.payload.fullTitles.size()", is(2)))
                    .andExpect(jsonPath("$.payload.seoData.friendlyCode", is("test_page_1")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.size()", is(2)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.description", is("test")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.keywords", is("keyword1, keyword 2")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags.size()", is(3)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].key", is("copyright")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].type", is("name")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].value", is("2020")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].useDefaultLang", is(false)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].key", is("author")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].type", is("name")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].value", is("entando")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].useDefaultLang", is(false)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[2].key", is("description")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[2].type", is("name")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[2].value", is("test page")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[2].useDefaultLang", is(false)))
                    .andExpect(
                            jsonPath("$.payload.seoData.seoDataByLang.en.inheritDescriptionFromDefaultLang", is(false)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.inheritKeywordsFromDefaultLang", is(false)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.description", is("")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.keywords", is("")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags.size()", is(3)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].key", is("copyright")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].type", is("name")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].value", is("entando")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].useDefaultLang", is(false)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].key", is("author")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].type", is("name")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].value", is("entando")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].useDefaultLang", is(false)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[2].key", is("description")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[2].type", is("name")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[2].value", is("metatag di prova")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[2].useDefaultLang", is(false)));

            // result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.inheritDescriptionFromDefaultLang",is(false)));
            // result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.inheritKeywordsFromDefaultLang",is(false)));
        } finally {
            PageDto page = this.pageService.getPage(SEO_TEST_1, IPageService.STATUS_DRAFT);
            if (null != page) {
                this.pageService.removePage(SEO_TEST_1);
            }
        }
    }

    @Test
    public void testPostSeoPageNoSeoFields() throws Exception {
        try {
            String accessToken = this.createAccessToken();

            final ResultActions result = this
                    .executePostSeoPage("1_POST_valid_empty_fields_1.json", accessToken, status().isOk());

            Assert.assertNotNull(this.pageService.getPage(SEO_TEST_1, IPageService.STATUS_DRAFT));
            result.andExpect(jsonPath("$.errors.size()", is(0)))
                    .andExpect(jsonPath("$.payload.code", is(SEO_TEST_1)))
                    .andExpect(jsonPath("$.payload.status", is("unpublished")))
                    .andExpect(jsonPath("$.payload.onlineInstance", is(false)))
                    .andExpect(jsonPath("$.payload.displayedInMenu", is(true)))
                    .andExpect(jsonPath("$.payload.pageModel", is("service")))
                    .andExpect(jsonPath("$.payload.charset", is("utf-8")))
                    .andExpect(jsonPath("$.payload.contentType", is("text/html")))
                    .andExpect(jsonPath("$.payload.parentCode", is("service")))
                    .andExpect(jsonPath("$.payload.seo", is(false)))
                    .andExpect(jsonPath("$.payload.titles.size()", is(2)))
                    .andExpect(jsonPath("$.payload.fullTitles.size()", is(2)))
                    .andExpect(jsonPath("$.payload.seoData.friendlyCode", is("")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.size()", is(2)));

        } finally {
            PageDto page = this.pageService.getPage(SEO_TEST_1, IPageService.STATUS_DRAFT);
            if (null != page) {
                this.pageService.removePage(SEO_TEST_1);
            }
        }
    }

    @Test
    public void testPostSeoPageNullSeoFields() throws Exception {
        try {
            String accessToken = this.createAccessToken();

            final ResultActions result = this
                    .executePostSeoPage("1_POST_valid_empty_fields_2.json", accessToken, status().isOk());

            Assert.assertNotNull(this.pageService.getPage(SEO_TEST_1, IPageService.STATUS_DRAFT));
            result.andExpect(jsonPath("$.errors.size()", is(0)))
                    .andExpect(jsonPath("$.payload.code", is(SEO_TEST_1)))
                    .andExpect(jsonPath("$.payload.status", is("unpublished")))
                    .andExpect(jsonPath("$.payload.onlineInstance", is(false)))
                    .andExpect(jsonPath("$.payload.displayedInMenu", is(true)))
                    .andExpect(jsonPath("$.payload.pageModel", is("service")))
                    .andExpect(jsonPath("$.payload.charset", is("utf-8")))
                    .andExpect(jsonPath("$.payload.contentType", is("text/html")))
                    .andExpect(jsonPath("$.payload.parentCode", is("service")))
                    .andExpect(jsonPath("$.payload.seo", is(false)))
                    .andExpect(jsonPath("$.payload.titles.size()", is(2)))
                    .andExpect(jsonPath("$.payload.fullTitles.size()", is(2)))
                    .andExpect(jsonPath("$.payload.seoData.friendlyCode", is("")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.size()", is(2)));

        } finally {
            PageDto page = this.pageService.getPage(SEO_TEST_1, IPageService.STATUS_DRAFT);
            if (null != page) {
                this.pageService.removePage(SEO_TEST_1);
            }
        }
    }

    @Test
    public void testPutSeoPage() throws Exception {
        try {
            String accessToken = this.createAccessToken();

            final ResultActions result = this
                    .executePostSeoPage("2_POST_valid.json", accessToken, status().isOk());

            Assert.assertNotNull(this.pageService.getPage(SEO_TEST_2, IPageService.STATUS_DRAFT));

            result.andExpect(jsonPath("$.errors.size()", is(0)))
                    .andExpect(jsonPath("$.payload.code", is(SEO_TEST_2)))
                    .andExpect(jsonPath("$.payload.status", is("unpublished")))
                    .andExpect(jsonPath("$.payload.onlineInstance", is(false)))
                    .andExpect(jsonPath("$.payload.displayedInMenu", is(true)))
                    .andExpect(jsonPath("$.payload.pageModel", is("service")))
                    .andExpect(jsonPath("$.payload.charset", is("utf-8")))
                    .andExpect(jsonPath("$.payload.contentType", is("text/html")))
                    .andExpect(jsonPath("$.payload.parentCode", is("service")))
                    .andExpect(jsonPath("$.payload.seo", is(true)))
                    .andExpect(jsonPath("$.payload.titles.size()", is(2)))
                    .andExpect(jsonPath("$.payload.fullTitles.size()", is(2)))
                    .andExpect(jsonPath("$.payload.seoData.friendlyCode", is("test_page_2")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.size()", is(2)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.description", is("test")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.keywords", is("keyword1, keyword 2")))
                    .andExpect(
                            jsonPath("$.payload.seoData.seoDataByLang.en.inheritDescriptionFromDefaultLang", is(false)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.inheritKeywordsFromDefaultLang", is(false)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags.size()", is(3)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].key", is("copyright")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].type", is("name")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].value", is("2020")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].useDefaultLang", is(false)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].key", is("author")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].type", is("name")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].value", is("entando")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].useDefaultLang", is(false)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[2].key", is("description")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[2].type", is("name")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[2].value", is("test page")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[2].useDefaultLang", is(false)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.description", is("")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.keywords", is("")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags.size()", is(3)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].key", is("copyright")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].type", is("name")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].value", is("entando")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].useDefaultLang", is(false)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].key", is("author")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].type", is("name")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].value", is("entando")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].useDefaultLang", is(false)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[2].key", is("description")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[2].type", is("name")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[2].value", is("metatag di prova")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[2].useDefaultLang", is(false)));

            //result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.inheritDescriptionFromDefaultLang",is(true)));
            //result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.inheritKeywordsFromDefaultLang",is(true)));

            Assert.assertNotNull(this.pageService.getPage(SEO_TEST_2, IPageService.STATUS_DRAFT));

            final ResultActions resultPut = this
                    .executePutSeoPage("2_PUT_valid.json", accessToken, status().isOk());

            Assert.assertNotNull(this.pageService.getPage(SEO_TEST_2, IPageService.STATUS_DRAFT));
            resultPut.andExpect(jsonPath("$.errors.size()", is(0)))
                    .andExpect(jsonPath("$.payload.code", is(SEO_TEST_2)))
                    .andExpect(jsonPath("$.payload.status", is("unpublished")))
                    .andExpect(jsonPath("$.payload.onlineInstance", is(false)))
                    .andExpect(jsonPath("$.payload.displayedInMenu", is(true)))
                    .andExpect(jsonPath("$.payload.pageModel", is("home")))
                    .andExpect(jsonPath("$.payload.charset", is("utf-8")))
                    .andExpect(jsonPath("$.payload.contentType", is("text/html")))
                    .andExpect(jsonPath("$.payload.parentCode", is("homepage")))
                    .andExpect(jsonPath("$.payload.seo", is(true)))
                    .andExpect(jsonPath("$.payload.titles.size()", is(2)))
                    .andExpect(jsonPath("$.payload.fullTitles.size()", is(2)))
                    .andExpect(jsonPath("$.payload.seoData.friendlyCode", is("test_page_2_friendly_url")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.size()", is(2)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.description", is("test page")))
                    .andExpect(
                            jsonPath("$.payload.seoData.seoDataByLang.en.keywords",
                                    is("keyword number 1, keyword number 2")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags.size()", is(2)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].key", is("author")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].type", is("name")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].value", is("entando")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].useDefaultLang", is(false)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].key", is("description")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].type", is("name")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].value", is("test page")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].useDefaultLang", is(false)))
                    .andExpect(
                            jsonPath("$.payload.seoData.seoDataByLang.en.inheritDescriptionFromDefaultLang", is(false)))
                    .andExpect(
                            jsonPath("$.payload.seoData.seoDataByLang.en.inheritKeywordsFromDefaultLang", is(false)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.description", is("")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.keywords", is("")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags.size()", is(2)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].key", is("author")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].type", is("name")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].value", is("entando")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].useDefaultLang", is(false)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].key", is("description")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].type", is("name")))
                    .andExpect(
                            jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].value",
                                    is("descrizione meta test")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].useDefaultLang", is(false)));

            // resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.inheritDescriptionFromDefaultLang",is(true)));
            // resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.inheritKeywordsFromDefaultLang",is(true)));

            final ResultActions resultPutMetaDefaultLangTrue = this
                    .executePutSeoPage("2_PUT_valid_meta_default_lang_true.json", accessToken, status().isOk());

            Assert.assertNotNull(this.pageService.getPage(SEO_TEST_2, IPageService.STATUS_DRAFT));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.errors.size()", is(0)))
                    .andExpect(jsonPath("$.payload.code", is(SEO_TEST_2)))
                    .andExpect(jsonPath("$.payload.status", is("unpublished")))
                    .andExpect(jsonPath("$.payload.onlineInstance", is(false)))
                    .andExpect(jsonPath("$.payload.displayedInMenu", is(true)))
                    .andExpect(jsonPath("$.payload.pageModel", is("home")))
                    .andExpect(jsonPath("$.payload.charset", is("utf-8")))
                    .andExpect(jsonPath("$.payload.contentType", is("text/html")))
                    .andExpect(jsonPath("$.payload.parentCode", is("homepage")))
                    .andExpect(jsonPath("$.payload.seo", is(true)))
                    .andExpect(jsonPath("$.payload.titles.size()", is(2)))
                    .andExpect(jsonPath("$.payload.fullTitles.size()", is(2)))
                    .andExpect(jsonPath("$.payload.seoData.friendlyCode", is("test_page_2_friendly_url")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.size()", is(2)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.description", is("test page")))
                    .andExpect(
                            jsonPath("$.payload.seoData.seoDataByLang.en.keywords",
                                    is("keyword number 1, keyword number 2")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags.size()", is(2)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].key", is("author")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].type", is("name")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].value", is("entando")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].useDefaultLang", is(false)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].key", is("description")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].type", is("name")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].value", is("test page")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].useDefaultLang", is(false)))
                    .andExpect(
                            jsonPath("$.payload.seoData.seoDataByLang.en.inheritDescriptionFromDefaultLang", is(false)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.inheritKeywordsFromDefaultLang", is(false)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.description", is("")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.keywords", is("")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags.size()", is(2)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].key", is("author")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].type", is("name")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].value", is("entando")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].useDefaultLang", is(true)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].key", is("description")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].type", is("name")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].value", is("test in italiano")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].useDefaultLang", is(true)));

            //    resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.inheritDescriptionFromDefaultLang",is(true)));
            //    resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.inheritKeywordsFromDefaultLang",is(true)));

            final ResultActions resultNoMetatag = this
                    .executePutSeoPage("2_PUT_valid_no_metatag.json", accessToken, status().isOk());

            Assert.assertNotNull(this.pageService.getPage(SEO_TEST_2, IPageService.STATUS_DRAFT));

            Assert.assertNotNull(this.pageService.getPage(SEO_TEST_2, IPageService.STATUS_DRAFT));
            resultNoMetatag.andExpect(jsonPath("$.errors.size()", is(0)))
                    .andExpect(jsonPath("$.payload.code", is(SEO_TEST_2)))
                    .andExpect(jsonPath("$.payload.status", is("unpublished")))
                    .andExpect(jsonPath("$.payload.onlineInstance", is(false)))
                    .andExpect(jsonPath("$.payload.displayedInMenu", is(true)))
                    .andExpect(jsonPath("$.payload.pageModel", is("home")))
                    .andExpect(jsonPath("$.payload.charset", is("utf-8")))
                    .andExpect(jsonPath("$.payload.contentType", is("text/html")))
                    .andExpect(jsonPath("$.payload.parentCode", is("homepage")))
                    .andExpect(jsonPath("$.payload.seo", is(true)))
                    .andExpect(jsonPath("$.payload.titles.size()", is(2)))
                    .andExpect(jsonPath("$.payload.fullTitles.size()", is(2)))
                    .andExpect(jsonPath("$.payload.seoData.friendlyCode", is("test_page_2_friendly_url")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.size()", is(2)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.description", is("test page")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.keywords",
                            is("keyword number 1, keyword number 2")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags.size()", is(0)))

                    .andExpect(
                            jsonPath("$.payload.seoData.seoDataByLang.en.inheritDescriptionFromDefaultLang", is(false)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.inheritKeywordsFromDefaultLang", is(false)))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.description", is("")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.keywords", is("")))
                    .andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags.size()", is(0)));


        } finally {
            PageDto page = this.pageService.getPage(SEO_TEST_2, IPageService.STATUS_DRAFT);
            if (null != page) {
                this.pageService.removePage(SEO_TEST_2);
            }
        }
    }

    private ResultActions executePostSeoPage(String fileName, String accessToken, ResultMatcher expected)
            throws Exception {
        InputStream isJsonPostValid2 = this.getClass().getResourceAsStream(fileName);
        String jsonPostValid2 = FileTextReader.getText(isJsonPostValid2);
        String path = "/plugins/seo/pages/";

        ResultActions result = mockMvc
                .perform(post(path)
                        .content(jsonPostValid2)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(expected);

        System.out.println("result: \n" + result.andReturn().getResponse().getContentAsString());
        return result;
    }

    private ResultActions executePutSeoPage(String fileName, String accessToken, ResultMatcher expected)
            throws Exception {
        InputStream isJsonPostValid = this.getClass().getResourceAsStream(fileName);
        String jsonPostValid = FileTextReader.getText(isJsonPostValid);

        String path = "/plugins/seo/pages/{pageCode}";
        ResultActions result1 = mockMvc
                .perform(put(path, SEO_TEST_2)
                        .content(jsonPostValid)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result1.andExpect(expected);

        System.out.println("result1: \n" + result1.andReturn().getResponse().getContentAsString());

        return result1;
    }

    private String createAccessToken() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, Permission.MANAGE_PAGES, Permission.MANAGE_PAGES)
                .build();
        return mockOAuthInterceptor(user);
    }

}
