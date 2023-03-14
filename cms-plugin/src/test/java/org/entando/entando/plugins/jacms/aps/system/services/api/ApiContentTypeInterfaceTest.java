package org.entando.entando.plugins.jacms.aps.system.services.api;

import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.pagemodel.IPageModelManager;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import com.agiletec.plugins.jacms.aps.system.services.content.ContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.ContentModel;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.IContentModelManager;
import java.util.Map;
import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.model.LegacyApiError;
import org.entando.entando.aps.system.services.api.model.StringApiResponse;
import org.entando.entando.plugins.jacms.aps.system.services.api.model.JAXBContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ApiContentTypeInterfaceTest {

    @Mock
    private ContentManager contentManager;
    @Mock
    private IContentModelManager contentModelManager;
    @Mock
    private IPageManager pageManager;
    @Mock
    private IPageModelManager pageModelManager;

    @InjectMocks
    private ApiContentTypeInterface apiContentTypeInterface;

    @Test
    void updateContentTypeShouldFailIfContentModelDoesNotExist() throws Throwable {
        JAXBContentType jaxbContentType = new JAXBContentType();
        jaxbContentType.setTypeCode("NWS");
        jaxbContentType.setDefaultModelId(999);
        Content content = new Content();
        content.setTypeCode("NWS");
        Mockito.when(contentManager.getEntityPrototype("NWS")).thenReturn(content);
        Mockito.when(contentManager.getEntityAttributePrototypes()).thenReturn(Map.of());
        Mockito.when(contentManager.getEntityClass()).thenReturn(Content.class);
        StringApiResponse response = apiContentTypeInterface.updateContentType(jaxbContentType);
        Assertions.assertEquals(1, response.getErrors().size());
        LegacyApiError error = response.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Content model with id '999' does not exist", error.getMessage());
    }

    @Test
    void updateContentTypeShouldFailWhenUsingModelOfDifferentContentType() throws Throwable {
        JAXBContentType jaxbContentType = new JAXBContentType();
        jaxbContentType.setTypeCode("NWS");
        jaxbContentType.setDefaultModelId(999);
        Content content = new Content();
        content.setTypeCode("NWS");
        Mockito.when(contentManager.getEntityPrototype("NWS")).thenReturn(content);
        Mockito.when(contentManager.getEntityAttributePrototypes()).thenReturn(Map.of());
        Mockito.when(contentManager.getEntityClass()).thenReturn(Content.class);
        ContentModel contentModel = new ContentModel();
        contentModel.setContentType("BNR");
        Mockito.when(contentModelManager.getContentModel(999)).thenReturn(contentModel);
        StringApiResponse response = apiContentTypeInterface.updateContentType(jaxbContentType);
        Assertions.assertEquals(1, response.getErrors().size());
        LegacyApiError error = response.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Content model with id '999' is for contents of type 'BNR'", error.getMessage());
    }

    @Test
    void updateContentTypeShouldFailIfViewPageDoesNotExist() throws Throwable {
        JAXBContentType jaxbContentType = new JAXBContentType();
        jaxbContentType.setTypeCode("NWS");
        jaxbContentType.setViewPage("viewPage");
        Content content = new Content();
        content.setTypeCode("NWS");
        Mockito.when(contentManager.getEntityPrototype("NWS")).thenReturn(content);
        Mockito.when(contentManager.getEntityAttributePrototypes()).thenReturn(Map.of());
        Mockito.when(contentManager.getEntityClass()).thenReturn(Content.class);
        StringApiResponse response = apiContentTypeInterface.updateContentType(jaxbContentType);
        Assertions.assertEquals(1, response.getErrors().size());
        LegacyApiError error = response.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("View Page with id 'viewPage' does not exist", error.getMessage());
    }

    @Test
    void updateContentTypeShouldFailIfPageMainFrameDoesNotExist() throws Throwable {
        JAXBContentType jaxbContentType = new JAXBContentType();
        jaxbContentType.setTypeCode("NWS");
        jaxbContentType.setViewPage("viewPage");
        Content content = new Content();
        content.setTypeCode("NWS");
        Mockito.when(contentManager.getEntityPrototype("NWS")).thenReturn(content);
        Mockito.when(contentManager.getEntityAttributePrototypes()).thenReturn(Map.of());
        Mockito.when(contentManager.getEntityClass()).thenReturn(Content.class);
        IPage page = Mockito.mock(IPage.class);
        Mockito.when(pageManager.getOnlinePage("viewPage")).thenReturn(page);
        Mockito.when(page.getModelCode()).thenReturn("pageModel");
        Mockito.when(page.getCode()).thenReturn("viewPage");
        PageModel pageModel = new PageModel();
        Mockito.when(pageModelManager.getPageModel("pageModel")).thenReturn(pageModel);
        StringApiResponse response = apiContentTypeInterface.updateContentType(jaxbContentType);
        Assertions.assertEquals(1, response.getErrors().size());
        LegacyApiError error = response.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Main frame for Page with id 'viewPage' not present", error.getMessage());
    }
}
