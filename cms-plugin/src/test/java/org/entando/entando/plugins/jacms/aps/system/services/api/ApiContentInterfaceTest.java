package org.entando.entando.plugins.jacms.aps.system.services.api;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.attribute.NumberAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.TextAttribute;
import com.agiletec.aps.system.services.group.IGroupManager;
import com.agiletec.aps.system.services.user.User;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.plugins.jacms.aps.system.services.content.ContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.helper.IContentAuthorizationHelper;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.content.model.ContentRecordVO;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.ContentModel;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.IContentModelManager;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.model.LegacyApiError;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.api.model.StringApiResponse;
import org.entando.entando.plugins.jacms.aps.system.services.api.model.CmsApiResponse;
import org.entando.entando.plugins.jacms.aps.system.services.api.model.JAXBContent;
import org.entando.entando.plugins.jacms.aps.system.services.api.model.JAXBContentAttribute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ApiContentInterfaceTest {

    @Mock
    private ContentManager contentManager;
    @Mock
    private IContentAuthorizationHelper contentAuthorizationHelper;
    @Mock
    private IContentModelManager contentModelManager;
    @Mock
    private IGroupManager groupManager;

    @InjectMocks
    private ApiContentInterface apiContentInterface;

    @Test
    void getContentsShouldFailIfContentTypeDoesNotExist() {
        Mockito.when(contentManager.getSmallContentTypesMap()).thenReturn(new HashMap<>());
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> apiContentInterface.getContents(new Properties()));
        Assertions.assertEquals(1, exception.getErrors().size());
        LegacyApiError error = exception.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Content Type 'null' does not exist", error.getMessage());
    }

    @Test
    void getContentShouldFileIfContentDoesNotExist() {
        Properties properties = new Properties();
        properties.setProperty("id", "DoesNotExists");
        properties.setProperty(SystemConstants.API_LANG_CODE_PARAMETER, "en");
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> apiContentInterface.getContent(properties));
        Assertions.assertEquals(1, exception.getErrors().size());
        LegacyApiError error = exception.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Null content by id 'DoesNotExists'", error.getMessage());
    }

    @Test
    void getContentShouldFileIfContentIsNotAllowed() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("id", "NWS3");
        properties.setProperty(SystemConstants.API_LANG_CODE_PARAMETER, "en");
        properties.put(SystemConstants.API_USER_PARAMETER, new User());
        Content content = new Content();
        Mockito.when(contentManager.loadContent("NWS3", true)).thenReturn(content);
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> apiContentInterface.getContent(properties));
        Assertions.assertEquals(1, exception.getErrors().size());
        LegacyApiError error = exception.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, error.getStatus());
        Assertions.assertEquals("Required content 'NWS3' is not allowed", error.getMessage());
    }

    @Test
    void getContentToHtmlShouldFileIfDefaultModelIdIsUsedOnContentWithoutDefaultModelId() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("id", "NWS3");
        properties.setProperty("modelId", "default");
        properties.setProperty(SystemConstants.API_LANG_CODE_PARAMETER, "en");
        Content content = new Content();
        Mockito.when(contentManager.loadContent("NWS3", true)).thenReturn(content);
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> apiContentInterface.getContentToHtml(properties));
        Assertions.assertEquals(1, exception.getErrors().size());
        LegacyApiError error = exception.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
        Assertions.assertEquals("Invalid 'default' system model for content type 'null' - Contact the administrators",
                error.getMessage());
    }

    @Test
    void getContentToHtmlShouldFileIfListModelIdIsUsedOnContentWithoutListModelId() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("id", "NWS3");
        properties.setProperty("modelId", "list");
        properties.setProperty(SystemConstants.API_LANG_CODE_PARAMETER, "en");
        Content content = new Content();
        Mockito.when(contentManager.loadContent("NWS3", true)).thenReturn(content);
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> apiContentInterface.getContentToHtml(properties));
        Assertions.assertEquals(1, exception.getErrors().size());
        LegacyApiError error = exception.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
        Assertions.assertEquals("Invalid 'list' system model for content type 'null' - Contact the administrators",
                error.getMessage());
    }

    @Test
    void getContentToHtmlShouldFileIfModelIdIsNotNumeric() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("id", "NWS3");
        properties.setProperty("modelId", "NaN");
        properties.setProperty(SystemConstants.API_LANG_CODE_PARAMETER, "en");
        Content content = new Content();
        Mockito.when(contentManager.loadContent("NWS3", true)).thenReturn(content);
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> apiContentInterface.getContentToHtml(properties));
        Assertions.assertEquals(1, exception.getErrors().size());
        LegacyApiError error = exception.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
        Assertions.assertEquals("The model id must be an integer or 'default' or 'list' - 'NaN'", error.getMessage());
    }

    @Test
    void getContentToHtmlShouldFileIfModelDoesNotExist() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("id", "NWS3");
        properties.setProperty("modelId", "42");
        properties.setProperty(SystemConstants.API_LANG_CODE_PARAMETER, "en");
        Content content = new Content();
        Mockito.when(contentManager.loadContent("NWS3", true)).thenReturn(content);
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> apiContentInterface.getContentToHtml(properties));
        Assertions.assertEquals(1, exception.getErrors().size());
        LegacyApiError error = exception.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
        Assertions.assertEquals("The content model with id '42' does not exist", error.getMessage());
    }

    @Test
    void getContentToHtmlShouldFileIfContentTypeCodeIsDifferentFromModelTypeCode() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("id", "NWS3");
        properties.setProperty("modelId", "42");
        properties.setProperty(SystemConstants.API_LANG_CODE_PARAMETER, "en");
        Content content = new Content();
        content.setTypeCode("typeCode1");
        Mockito.when(contentManager.loadContent("NWS3", true)).thenReturn(content);
        ContentModel contentModel = new ContentModel();
        contentModel.setContentType("typeCode2");
        Mockito.when(contentModelManager.getContentModel(42)).thenReturn(contentModel);
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> apiContentInterface.getContentToHtml(properties));
        Assertions.assertEquals(1, exception.getErrors().size());
        LegacyApiError error = exception.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
        Assertions.assertEquals("The content model with id '42' does not match with content of type 'typeCode1'",
                error.getMessage());
    }

    @Test
    void addContentShouldFailIfTypeCodeDoesNotExist() throws Exception {
        JAXBContent jaxbContent = new JAXBContent();
        CmsApiResponse cmsApiResponse = apiContentInterface.addContent(jaxbContent, new Properties());
        Assertions.assertEquals(1, cmsApiResponse.getErrors().size());
        LegacyApiError error = cmsApiResponse.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Content type with code 'null' does not exist", error.getMessage());
    }

    @Test
    void addContentShouldFailIfContentIdIsSpecified() throws Exception {
        JAXBContent jaxbContent = new JAXBContent();
        jaxbContent.setId("NWS3");
        Mockito.when(contentManager.getEntityPrototype(Mockito.any())).thenReturn(new Content());
        CmsApiResponse cmsApiResponse = apiContentInterface.addContent(jaxbContent, new Properties());
        Assertions.assertEquals(1, cmsApiResponse.getErrors().size());
        LegacyApiError error = cmsApiResponse.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("You cannot specify Content Id", error.getMessage());
    }

    @Test
    void updateContentShouldFailIfTypeCodeDoesNotExist() throws Exception {
        JAXBContent jaxbContent = new JAXBContent();
        CmsApiResponse cmsApiResponse = apiContentInterface.updateContent(jaxbContent, new Properties());
        Assertions.assertEquals(1, cmsApiResponse.getErrors().size());
        LegacyApiError error = cmsApiResponse.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Content type with code 'null' does not exist", error.getMessage());
    }

    @Test
    void updateContentShouldFailIfContentDoesNotExist() throws Exception {
        JAXBContent jaxbContent = new JAXBContent();
        Mockito.when(contentManager.getEntityPrototype(Mockito.any())).thenReturn(new Content());
        CmsApiResponse cmsApiResponse = apiContentInterface.updateContent(jaxbContent, new Properties());
        Assertions.assertEquals(1, cmsApiResponse.getErrors().size());
        LegacyApiError error = cmsApiResponse.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Content with code 'null' does not exist", error.getMessage());
    }

    @Test
    void updateContentShouldFailIfMasterGroupIsChanged() throws Exception {
        JAXBContent jaxbContent = new JAXBContent();
        jaxbContent.setId("NWS3");
        JAXBContent spyJaxbContent = Mockito.spy(jaxbContent);
        Mockito.doReturn(Set.of("free")).when(spyJaxbContent).getGroups();
        Mockito.doReturn("free").when(spyJaxbContent).getMainGroup();
        Content content = new Content();
        content.setMainGroup("admin");
        Mockito.when(contentManager.loadContent("NWS3", false)).thenReturn(content);
        Mockito.when(contentManager.getEntityPrototype(Mockito.any())).thenReturn(new Content());
        CmsApiResponse cmsApiResponse = apiContentInterface.updateContent(spyJaxbContent, new Properties());
        Assertions.assertEquals(1, cmsApiResponse.getErrors().size());
        LegacyApiError error = cmsApiResponse.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Invalid main group free not equal to master admin", error.getMessage());
    }

    @Test
    void updateContentShouldFailIfUserIsNotAllowed() throws Exception {
        JAXBContent jaxbContent = new JAXBContent();
        jaxbContent.setId("NWS3");
        JAXBContent spyJaxbContent = Mockito.spy(jaxbContent);
        Mockito.doReturn(Set.of("free")).when(spyJaxbContent).getGroups();
        Mockito.doReturn("free").when(spyJaxbContent).getMainGroup();
        Content content = new Content();
        content.setMainGroup("free");
        Mockito.when(contentManager.loadContent("NWS3", false)).thenReturn(content);
        Mockito.when(contentManager.getEntityPrototype(Mockito.any())).thenReturn(new Content());
        Properties properties = new Properties();
        properties.put(SystemConstants.API_USER_PARAMETER, new User());
        CmsApiResponse cmsApiResponse = apiContentInterface.updateContent(spyJaxbContent, properties);
        Assertions.assertEquals(1, cmsApiResponse.getErrors().size());
        LegacyApiError error = cmsApiResponse.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, error.getStatus());
        Assertions.assertEquals("Content groups makes the new content not allowed for user null", error.getMessage());
    }

    @Test
    void updateContentShouldFailIfUserIsMainGroupDoesNotExist() throws Exception {
        JAXBContent jaxbContent = new JAXBContent();
        jaxbContent.setId("NWS3");
        JAXBContent spyJaxbContent = Mockito.spy(jaxbContent);
        Mockito.doReturn(Set.of()).when(spyJaxbContent).getGroups();
        Mockito.doReturn("invalidGroup").when(spyJaxbContent).getMainGroup();
        Content content = new Content();
        content.setMainGroup("invalidGroup");
        Mockito.when(contentManager.loadContent("NWS3", false)).thenReturn(content);
        Mockito.when(contentManager.getEntityPrototype(Mockito.any())).thenReturn(new Content());
        Properties properties = new Properties();
        properties.put(SystemConstants.API_USER_PARAMETER, new User());
        Mockito.when(contentAuthorizationHelper.isAuth(
                Mockito.any(UserDetails.class), Mockito.any(Content.class))).thenReturn(true);
        CmsApiResponse cmsApiResponse = apiContentInterface.updateContent(spyJaxbContent, properties);
        Assertions.assertEquals(1, cmsApiResponse.getErrors().size());
        LegacyApiError error = cmsApiResponse.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Invalid main group - invalidGroup", error.getMessage());
    }

    @Test
    void deleteContentShouldFailIfContentDoesNotExist() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("id", "doesNotExist");
        StringApiResponse response = apiContentInterface.deleteContent(properties);
        Assertions.assertEquals(1, response.getErrors().size());
        LegacyApiError error = response.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Content with code 'doesNotExist' does not exist", error.getMessage());
    }

    @Test
    void deleteContentShouldFailIfUserIsNotAllowed() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("id", "NWS3");
        properties.put(SystemConstants.API_USER_PARAMETER, new User());
        Mockito.when(contentManager.loadContent("NWS3", false)).thenReturn(new Content());
        StringApiResponse response = apiContentInterface.deleteContent(properties);
        Assertions.assertEquals(1, response.getErrors().size());
        LegacyApiError error = response.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, error.getStatus());
        Assertions.assertEquals("Content groups makes the new content not allowed for user null", error.getMessage());
    }

    @Test
    void deleteContentShouldFailIfContentIsReferenced() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("id", "NWS3");
        properties.put(SystemConstants.API_USER_PARAMETER, new User());
        Mockito.when(contentManager.loadContent("NWS3", false)).thenReturn(new Content());
        Mockito.when(contentAuthorizationHelper.isAuth(
                Mockito.any(UserDetails.class), Mockito.any(Content.class))).thenReturn(true);
        Mockito.when(contentManager.getContentUtilizers("NWS3")).thenReturn(List.of("REF"));
        Mockito.when(contentManager.loadContentVO("REF")).thenReturn(new ContentRecordVO());
        StringApiResponse response = apiContentInterface.deleteContent(properties);
        Assertions.assertEquals(1, response.getErrors().size());
        LegacyApiError error = response.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Content NWS3 referenced to content null - 'null'", error.getMessage());
    }

    @Test
    void updateContentTextShouldFailIfContentDoesNotExist() {
        JAXBContentAttribute jaxbContentAttribute = new JAXBContentAttribute();
        jaxbContentAttribute.setContentId("doesNotExist");
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> apiContentInterface.updateContentText(jaxbContentAttribute));
        Assertions.assertEquals(1, exception.getErrors().size());
        LegacyApiError error = exception.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Content with code 'doesNotExist' does not exist", error.getMessage());
    }

    @Test
    void updateContentTextShouldFailIfAttributeNameDoesNotExist() throws Exception {
        JAXBContentAttribute jaxbContentAttribute = new JAXBContentAttribute();
        jaxbContentAttribute.setContentId("NWS3");
        Mockito.when(contentManager.loadContent("NWS3", true)).thenReturn(new Content());
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> apiContentInterface.updateContentText(jaxbContentAttribute));
        Assertions.assertEquals(1, exception.getErrors().size());
        LegacyApiError error = exception.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Content Attribute with code 'null' does not exist into content NWS3",
                error.getMessage());
    }

    @Test
    void updateContentTextShouldFailIfAttributeIsNotATextAttribute() throws Exception {
        JAXBContentAttribute jaxbContentAttribute = new JAXBContentAttribute();
        jaxbContentAttribute.setContentId("NWS3");
        jaxbContentAttribute.setAttributeName("number");
        NumberAttribute numberAttribute = new NumberAttribute();
        numberAttribute.setName("number");
        Content content = new Content();
        content.addAttribute(numberAttribute);
        Mockito.when(contentManager.loadContent("NWS3", true)).thenReturn(content);
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> apiContentInterface.updateContentText(jaxbContentAttribute));
        Assertions.assertEquals(1, exception.getErrors().size());
        LegacyApiError error = exception.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Content Attribute with code 'number' isn't a Text Attribute", error.getMessage());
    }

    @Test
    void updateContentTextShouldFailIfAttributeLangIsEmpty() throws Exception {
        JAXBContentAttribute jaxbContentAttribute = new JAXBContentAttribute();
        jaxbContentAttribute.setContentId("NWS3");
        jaxbContentAttribute.setAttributeName("text");
        TextAttribute textAttribute = new TextAttribute();
        textAttribute.setName("text");
        Content content = new Content();
        content.addAttribute(textAttribute);
        Mockito.when(contentManager.loadContent("NWS3", true)).thenReturn(content);
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> apiContentInterface.updateContentText(jaxbContentAttribute));
        Assertions.assertEquals(1, exception.getErrors().size());
        LegacyApiError error = exception.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("LangCode or value is Empty", error.getMessage());
    }
}
