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
            result.andExpect(jsonPath("$.errors.size()", is(0)));
            result.andExpect(jsonPath("$.payload.code", is(SEO_TEST_1)));
            result.andExpect(jsonPath("$.payload.status", is("unpublished")));
            result.andExpect(jsonPath("$.payload.onlineInstance",is(false)));
            result.andExpect(jsonPath("$.payload.displayedInMenu",is(true)));
            result.andExpect(jsonPath("$.payload.pageModel",is("service")));
            result.andExpect(jsonPath("$.payload.charset",is("utf-8")));
            result.andExpect(jsonPath("$.payload.contentType",is("text/html")));
            result.andExpect(jsonPath("$.payload.parentCode",is("service")));
            result.andExpect(jsonPath("$.payload.seo",is(true)));
            result.andExpect(jsonPath("$.payload.titles.size()",is(2)));
            result.andExpect(jsonPath("$.payload.fullTitles.size()",is(2)));
            result.andExpect(jsonPath("$.payload.seoData.friendlyCode",is("test_page_1")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.size()",is(2)));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.description",is("test")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.keywords",is("keyword1, keyword 2")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags.size()",is(3)));

            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].key",is("copyright")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].type",is("name")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].value",is("2020")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].useDefaultLang",is(false)));

            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].key",is("author")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].type",is("name")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].value",is("entando")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].useDefaultLang",is(false)));

            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[2].key",is("description")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[2].type",is("name")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[2].value",is("test page")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[2].useDefaultLang",is(false)));

            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.inheritDescriptionFromDefaultLang",is(false)));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.inheritKeywordsFromDefaultLang",is(false)));

            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.description",is("")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.keywords",is("")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags.size()",is(3)));

            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].key",is("copyright")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].type",is("name")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].value",is("entando")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].useDefaultLang",is(false)));

            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].key",is("author")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].type",is("name")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].value",is("entando")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].useDefaultLang",is(false)));

            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[2].key",is("description")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[2].type",is("name")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[2].value",is("metatag di prova")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[2].useDefaultLang",is(false)));

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
            result.andExpect(jsonPath("$.errors.size()", is(0)));
            result.andExpect(jsonPath("$.payload.code", is(SEO_TEST_1)));
            result.andExpect(jsonPath("$.payload.status", is("unpublished")));
            result.andExpect(jsonPath("$.payload.onlineInstance",is(false)));
            result.andExpect(jsonPath("$.payload.displayedInMenu",is(true)));
            result.andExpect(jsonPath("$.payload.pageModel",is("service")));
            result.andExpect(jsonPath("$.payload.charset",is("utf-8")));
            result.andExpect(jsonPath("$.payload.contentType",is("text/html")));
            result.andExpect(jsonPath("$.payload.parentCode",is("service")));
            result.andExpect(jsonPath("$.payload.seo",is(false)));
            result.andExpect(jsonPath("$.payload.titles.size()",is(2)));
            result.andExpect(jsonPath("$.payload.fullTitles.size()",is(2)));
            result.andExpect(jsonPath("$.payload.seoData.friendlyCode", is("")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.size()", is(2)));

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
            result.andExpect(jsonPath("$.errors.size()", is(0)));
            result.andExpect(jsonPath("$.payload.code", is(SEO_TEST_1)));
            result.andExpect(jsonPath("$.payload.status", is("unpublished")));
            result.andExpect(jsonPath("$.payload.onlineInstance",is(false)));
            result.andExpect(jsonPath("$.payload.displayedInMenu",is(true)));
            result.andExpect(jsonPath("$.payload.pageModel",is("service")));
            result.andExpect(jsonPath("$.payload.charset",is("utf-8")));
            result.andExpect(jsonPath("$.payload.contentType",is("text/html")));
            result.andExpect(jsonPath("$.payload.parentCode",is("service")));
            result.andExpect(jsonPath("$.payload.seo",is(false)));
            result.andExpect(jsonPath("$.payload.titles.size()",is(2)));
            result.andExpect(jsonPath("$.payload.fullTitles.size()",is(2)));
            result.andExpect(jsonPath("$.payload.seoData.friendlyCode", is("")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.size()", is(2)));

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

            result.andExpect(jsonPath("$.errors.size()", is(0)));
            result.andExpect(jsonPath("$.payload.code", is(SEO_TEST_2)));
            result.andExpect(jsonPath("$.payload.status", is("unpublished")));
            result.andExpect(jsonPath("$.payload.onlineInstance",is(false)));
            result.andExpect(jsonPath("$.payload.displayedInMenu",is(true)));
            result.andExpect(jsonPath("$.payload.pageModel",is("service")));
            result.andExpect(jsonPath("$.payload.charset",is("utf-8")));
            result.andExpect(jsonPath("$.payload.contentType",is("text/html")));
            result.andExpect(jsonPath("$.payload.parentCode",is("service")));
            result.andExpect(jsonPath("$.payload.seo",is(true)));
            result.andExpect(jsonPath("$.payload.titles.size()",is(2)));
            result.andExpect(jsonPath("$.payload.fullTitles.size()",is(2)));
            result.andExpect(jsonPath("$.payload.seoData.friendlyCode",is("test_page_2")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.size()",is(2)));

            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.description",is("test")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.keywords",is("keyword1, keyword 2")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.inheritDescriptionFromDefaultLang",is(false)));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.inheritKeywordsFromDefaultLang",is(false)));

            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags.size()",is(3)));

            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].key",is("copyright")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].type",is("name")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].value",is("2020")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].useDefaultLang",is(false)));

            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].key",is("author")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].type",is("name")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].value",is("entando")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].useDefaultLang",is(false)));

            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[2].key",is("description")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[2].type",is("name")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[2].value",is("test page")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[2].useDefaultLang",is(false)));


            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.description",is("")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.keywords",is("")));

            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags.size()",is(3)));

            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].key",is("copyright")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].type",is("name")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].value",is("entando")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].useDefaultLang",is(false)));

            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].key",is("author")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].type",is("name")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].value",is("entando")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].useDefaultLang",is(false)));

            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[2].key",is("description")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[2].type",is("name")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[2].value",is("metatag di prova")));
            result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[2].useDefaultLang",is(false)));

            //result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.inheritDescriptionFromDefaultLang",is(true)));
            //result.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.inheritKeywordsFromDefaultLang",is(true)));

            Assert.assertNotNull(this.pageService.getPage(SEO_TEST_2, IPageService.STATUS_DRAFT));

            final ResultActions resultPut = this
                    .executePutSeoPage("2_PUT_valid.json", accessToken, status().isOk());

            Assert.assertNotNull(this.pageService.getPage(SEO_TEST_2, IPageService.STATUS_DRAFT));
            resultPut.andExpect(jsonPath("$.errors.size()", is(0)));
            resultPut.andExpect(jsonPath("$.payload.code", is(SEO_TEST_2)));
            resultPut.andExpect(jsonPath("$.payload.status", is("unpublished")));
            resultPut.andExpect(jsonPath("$.payload.onlineInstance",is(false)));
            resultPut.andExpect(jsonPath("$.payload.displayedInMenu",is(true)));
            resultPut.andExpect(jsonPath("$.payload.pageModel",is("home")));
            resultPut.andExpect(jsonPath("$.payload.charset",is("utf-8")));
            resultPut.andExpect(jsonPath("$.payload.contentType",is("text/html")));
            resultPut.andExpect(jsonPath("$.payload.parentCode",is("homepage")));
            resultPut.andExpect(jsonPath("$.payload.seo",is(true)));
            resultPut.andExpect(jsonPath("$.payload.titles.size()",is(2)));
            resultPut.andExpect(jsonPath("$.payload.fullTitles.size()",is(2)));
            resultPut.andExpect(jsonPath("$.payload.seoData.friendlyCode",is("test_page_2_friendly_url")));
            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.size()",is(2)));

            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.description",is("test page")));
            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.keywords",is("keyword number 1, keyword number 2")));
            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags.size()",is(2)));

            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].key",is("author")));
            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].type",is("name")));
            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].value",is("entando")));
            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].useDefaultLang",is(false)));

            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].key",is("description")));
            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].type",is("name")));
            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].value",is("test page")));
            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].useDefaultLang",is(false)));

            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.inheritDescriptionFromDefaultLang",is(false)));
            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.inheritKeywordsFromDefaultLang",is(false)));

            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.description",is("")));
            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.keywords",is("")));
            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags.size()",is(2)));

            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].key",is("author")));
            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].type",is("name")));
            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].value",is("entando")));
            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].useDefaultLang",is(false)));

            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].key",is("description")));
            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].type",is("name")));
            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].value",is("descrizione meta test")));
            resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].useDefaultLang",is(false)));

            // resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.inheritDescriptionFromDefaultLang",is(true)));
            // resultPut.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.inheritKeywordsFromDefaultLang",is(true)));



            final ResultActions resultPutMetaDefaultLangTrue = this
                    .executePutSeoPage("2_PUT_valid_meta_default_lang_true.json", accessToken, status().isOk());

            Assert.assertNotNull(this.pageService.getPage(SEO_TEST_2, IPageService.STATUS_DRAFT));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.errors.size()", is(0)));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.code", is(SEO_TEST_2)));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.status", is("unpublished")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.onlineInstance",is(false)));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.displayedInMenu",is(true)));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.pageModel",is("home")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.charset",is("utf-8")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.contentType",is("text/html")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.parentCode",is("homepage")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seo",is(true)));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.titles.size()",is(2)));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.fullTitles.size()",is(2)));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.friendlyCode",is("test_page_2_friendly_url")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.size()",is(2)));

            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.description",is("test page")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.keywords",is("keyword number 1, keyword number 2")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags.size()",is(2)));


            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].key",is("author")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].type",is("name")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].value",is("entando")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[0].useDefaultLang",is(false)));

            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].key",is("description")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].type",is("name")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].value",is("test page")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.metaTags[1].useDefaultLang",is(false)));


            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.inheritDescriptionFromDefaultLang",is(false)));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.en.inheritKeywordsFromDefaultLang",is(false)));

            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.description",is("")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.keywords",is("")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags.size()",is(2)));

            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].key",is("author")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].type",is("name")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].value",is("entando")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[0].useDefaultLang",is(true)));

            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].key",is("description")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].type",is("name")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].value",is("test in italiano")));
            resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.metaTags[1].useDefaultLang",is(true)));

            //    resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.inheritDescriptionFromDefaultLang",is(true)));
            //    resultPutMetaDefaultLangTrue.andExpect(jsonPath("$.payload.seoData.seoDataByLang.it.inheritKeywordsFromDefaultLang",is(true)));


            final ResultActions resultNoMetatag = this
                    .executePutSeoPage("2_PUT_valid_no_metatag.json", accessToken, status().isOk());

            Assert.assertNotNull(this.pageService.getPage(SEO_TEST_2, IPageService.STATUS_DRAFT));



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
