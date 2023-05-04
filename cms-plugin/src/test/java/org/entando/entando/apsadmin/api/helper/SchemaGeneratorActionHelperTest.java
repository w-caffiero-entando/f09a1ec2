package org.entando.entando.apsadmin.api.helper;

import com.agiletec.aps.system.services.pagemodel.PageModel;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.ContentModel;
import org.entando.entando.aps.system.common.entity.api.response.EntityTypeResponse;
import org.entando.entando.aps.system.services.api.model.LinkedListApiResponse;
import org.entando.entando.aps.system.services.api.model.StringApiResponse;
import org.entando.entando.aps.system.services.api.model.StringListApiResponse;
import org.entando.entando.aps.system.services.api.response.ServicesResponse;
import org.entando.entando.aps.system.services.guifragment.api.GuiFragmentResponse;
import org.entando.entando.aps.system.services.guifragment.api.JAXBGuiFragment;
import org.entando.entando.aps.system.services.i18n.model.JAXBI18nLabel;
import org.entando.entando.aps.system.services.i18n.response.I18nLabelResponse;
import org.entando.entando.aps.system.services.pagemodel.api.PageModelResponse;
import org.entando.entando.aps.system.services.storage.api.BasicFileAttributeViewApiResponse;
import org.entando.entando.aps.system.services.storage.api.JAXBStorageResource;
import org.entando.entando.aps.system.services.userprofile.api.model.JAXBUserDetails;
import org.entando.entando.aps.system.services.userprofile.api.model.JAXBUserProfile;
import org.entando.entando.aps.system.services.userprofile.api.model.JAXBUserProfileType;
import org.entando.entando.aps.system.services.userprofile.api.response.UserProfileResponse;
import org.entando.entando.aps.system.services.userprofile.api.response.UserProfileTypeResponse;
import org.entando.entando.aps.system.services.widgettype.api.JAXBWidgetType;
import org.entando.entando.aps.system.services.widgettype.api.WidgetTypeResponse;
import org.entando.entando.plugins.jacms.aps.system.services.api.model.CmsApiResponse;
import org.entando.entando.plugins.jacms.aps.system.services.api.model.JAXBContent;
import org.entando.entando.plugins.jacms.aps.system.services.api.model.JAXBContentAttribute;
import org.entando.entando.plugins.jacms.aps.system.services.api.model.JAXBContentType;
import org.entando.entando.plugins.jacms.aps.system.services.api.model.JAXBResource;
import org.entando.entando.plugins.jacms.aps.system.services.api.response.ContentModelResponse;
import org.entando.entando.plugins.jacms.aps.system.services.api.response.ContentResponse;
import org.entando.entando.plugins.jacms.aps.system.services.api.response.ContentTypeResponse;
import org.entando.entando.plugins.jacms.aps.system.services.api.response.ResourceResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SchemaGeneratorActionHelperTest {

    private static final Class<?>[] API_PAYLOAD_CLASSES = new Class<?>[]{
            BasicFileAttributeViewApiResponse.class,
            CmsApiResponse.class,
            ContentModelResponse.class,
            ContentResponse.class,
            ContentTypeResponse.class,
            EntityTypeResponse.class,
            GuiFragmentResponse.class,
            I18nLabelResponse.class,
            LinkedListApiResponse.class,
            PageModelResponse.class,
            ResourceResponse.class,
            ServicesResponse.class,
            StringApiResponse.class,
            StringListApiResponse.class,
            UserProfileResponse.class,
            UserProfileTypeResponse.class,
            WidgetTypeResponse.class,
            JAXBContent.class,
            JAXBContentType.class,
            JAXBI18nLabel.class,
            JAXBGuiFragment.class,
            JAXBUserDetails.class,
            JAXBUserProfile.class,
            JAXBUserProfileType.class,
            JAXBResource.class,
            JAXBStorageResource.class,
            JAXBWidgetType.class,
            PageModel.class,
            ContentModel.class,
            JAXBContentAttribute.class
    };

    private SchemaGeneratorActionHelper helper = new SchemaGeneratorActionHelper();

    @Test
    void shouldGenerateSchemaForApiPayloads() {
        for (Class<?> type : API_PAYLOAD_CLASSES) {
            Assertions.assertDoesNotThrow(() -> helper.generateSchema(type),
                    "Unable to generate schema for " + type.getSimpleName());
        }
    }
}
