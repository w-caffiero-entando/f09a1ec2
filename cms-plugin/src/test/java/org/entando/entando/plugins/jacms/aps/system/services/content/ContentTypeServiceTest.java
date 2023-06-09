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
package org.entando.entando.plugins.jacms.aps.system.services.content;


import static com.agiletec.plugins.jacms.aps.system.services.content.model.attribute.ContentStatusState.PUBLISHED;
import static com.agiletec.plugins.jacms.aps.system.services.content.model.attribute.ContentStatusState.PUBLISHED_WITH_NEW_VERSION_IN_DRAFT_STATE;
import static com.agiletec.plugins.jacms.aps.system.services.content.model.attribute.ContentStatusState.PUBLISHED_WITH_NEW_VERSION_IN_READY_STATE;
import static com.agiletec.plugins.jacms.aps.system.services.content.model.attribute.ContentStatusState.READY;
import static com.agiletec.plugins.jacms.aps.system.services.content.model.attribute.ContentStatusState.UNPUBLISHED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.ContentRecordVO;
import com.agiletec.plugins.jacms.aps.system.services.content.model.ContentDto;
import com.agiletec.plugins.jacms.aps.system.services.content.model.attribute.ContentStatusState;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.ContentModel;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.model.ContentModelDto;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ArrayUtils;
import org.entando.entando.aps.system.services.mockhelper.PageMockHelper;
import org.entando.entando.aps.system.services.userprofile.MockUser;
import org.entando.entando.plugins.jacms.aps.system.services.assertionhelper.ContentTypeAssertionHelper;
import org.entando.entando.plugins.jacms.aps.system.services.mockhelper.ContentMockHelper;
import org.entando.entando.plugins.jacms.aps.system.services.mockhelper.ContentTypeMockHelper;
import org.entando.entando.plugins.jacms.web.content.validator.RestContentListRequest;
import org.entando.entando.web.common.assembler.PagedMetadataMapper;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.web.component.ComponentUsageEntity;
import org.entando.entando.web.page.model.PageSearchRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContentTypeServiceTest {

    private RestListRequest restListRequest;
    
    @Mock
    private HttpServletRequest httpRequest;
    
    @Mock
    private ContentService contentService;
    
    @Spy
    private PagedMetadataMapper pagedMetadataMapper = new PagedMetadataMapper();
    
    @InjectMocks
    private ContentTypeService contentTypeService;

    @BeforeEach
    public void setUp() throws Exception {
        Mockito.lenient().when(this.httpRequest.getAttribute("user")).thenReturn(new MockUser());
        this.restListRequest = ContentTypeMockHelper.mockRestListRequest();

        Field f = this.contentTypeService.getClass().getDeclaredField("httpRequest");
        f.setAccessible(true);
        f.set(this.contentTypeService, this.httpRequest);

        Field fp = this.contentTypeService.getClass().getDeclaredField("pagedMetadataMapper");
        fp.setAccessible(true);
        fp.set(this.contentTypeService, this.pagedMetadataMapper);

    }

    @Test
    public void getContentTypeUsageForNonExistingCodeShouldReturnZero() {

        int componentUsage = contentTypeService.getComponentUsage("not_existing");
        assertEquals(0, componentUsage);
    }
    
    @Test
    public void getContentTypeUsageDetailsWithContentsTest() {
        int relatedContents = 3;
        PagedMetadata<ContentDto> contentDtoPagedMetadata = ContentMockHelper.mockPagedContentDto(this.restListRequest, relatedContents);
        when(this.contentService.getContents(any(RestContentListRequest.class), any(UserDetails.class))).thenReturn(contentDtoPagedMetadata);
        PagedMetadata<ComponentUsageEntity> pageUsageDetails = this.contentTypeService.getComponentUsageDetails(ContentTypeMockHelper.CONTENT_TYPE_CODE,  this.restListRequest);
        ContentTypeAssertionHelper.assertUsageDetails(pageUsageDetails, relatedContents, relatedContents, this.restListRequest.getPage());
    }
    
    @Test
    public void getContentTypeUsageDetailsWithTemplateTest() throws Exception {
        List<ContentTypeServiceUtilizer> contentTypeServiceUtilizers = new ArrayList<>();
        this.contentTypeService.setContentTypeServiceUtilizers(contentTypeServiceUtilizers);
        ContentTypeServiceUtilizer utilizer = Mockito.mock(ContentTypeServiceUtilizer.class);
        contentTypeServiceUtilizers.add(utilizer);
        ContentModelDto contentModelDto = new ContentModelDto();
        contentModelDto.setId(23l);
        when(utilizer.getContentTypeUtilizer(Mockito.anyString())).thenReturn(List.of(contentModelDto));
        
        PagedMetadata<ContentDto> contentDtoPagedMetadata = ContentMockHelper.mockPagedContentDto(this.restListRequest, 0);
        when(this.contentService.getContents(any(RestContentListRequest.class), any(UserDetails.class))).thenReturn(contentDtoPagedMetadata);
        PagedMetadata<ComponentUsageEntity> pageUsageDetails = this.contentTypeService.getComponentUsageDetails(ContentTypeMockHelper.CONTENT_TYPE_CODE,  this.restListRequest);
        assertEquals(1, pageUsageDetails.getTotalItems());
    }

    @Test
    public void getContentStatusWithContentStatusPublicWillReturnPublished() throws Exception {

        ContentDto contentDto = new ContentDto();
        contentDto.setStatus(Content.STATUS_PUBLIC);
        assertEquals(PUBLISHED, ContentStatusState.calculateState(contentDto));
    }

    @Test
    public void getContentStatusWithContentStatusReadyAndOnlineWillReturnPublicNotEqualToReady() throws Exception {

        ContentDto contentDto = new ContentDto();
        contentDto.setStatus(Content.STATUS_READY);
        contentDto.setOnLine(true);
        assertEquals(PUBLISHED_WITH_NEW_VERSION_IN_READY_STATE, ContentStatusState.calculateState(contentDto));
    }

    @Test
    public void getContentStatusWithContentStatusReadyAndNOTOnlineWillReturnReady() throws Exception {

        ContentDto contentDto = new ContentDto();
        contentDto.setStatus(Content.STATUS_READY);
        assertEquals(READY, ContentStatusState.calculateState(contentDto));
    }

    @Test
    public void getContentStatusWithContentStatusDraftAndOnlineWillReturnPublicNotEqualToDraft() throws Exception {

        ContentDto contentDto = new ContentDto();
        contentDto.setStatus(Content.STATUS_DRAFT);
        contentDto.setOnLine(true);
        assertEquals(PUBLISHED_WITH_NEW_VERSION_IN_DRAFT_STATE, ContentStatusState.calculateState(contentDto));
    }

    @Test
    public void getContentStatusWithContentStatusDraftAndNOTOnlineWillReturnUnpublished() throws Exception {

        ContentDto contentDto = new ContentDto();
        contentDto.setStatus(Content.STATUS_DRAFT);
        assertEquals(UNPUBLISHED, ContentStatusState.calculateState(contentDto));
    }


}
