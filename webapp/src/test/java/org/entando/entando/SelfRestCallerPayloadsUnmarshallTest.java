package org.entando.entando;

import com.agiletec.plugins.jacms.aps.system.services.contentmodel.ContentModel;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.InputStream;
import java.util.List;
import org.entando.entando.aps.system.services.api.DefaultJsonTypesProvider;
import org.entando.entando.aps.system.services.api.ObjectMapperConfiguration;
import org.entando.entando.plugins.jacms.aps.system.services.api.CmsJsonTypesProvider;
import org.entando.entando.plugins.jacms.aps.system.services.api.model.JAXBContent;
import org.entando.entando.plugins.jacms.aps.system.services.api.model.JAXBContentType;
import org.entando.entando.plugins.jacms.aps.system.services.api.model.JAXBResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SelfRestCallerPayloadsUnmarshallTest {

    private XmlMapper xmlMapper;

    @BeforeEach
    void setUp() {
        ObjectMapperConfiguration mapperConfiguration = new ObjectMapperConfiguration();
        mapperConfiguration.setJsonTypesProviders(List.of(new DefaultJsonTypesProvider(), new CmsJsonTypesProvider()));
        xmlMapper = mapperConfiguration.xmlMapper();
    }

    @Test
    void shouldUnmarshalBNR() throws Exception {
        shouldUnmarshal("BNR/BNR.xml", JAXBContentType.class);
        shouldUnmarshal("BNR/content1.xml", JAXBContent.class);
        shouldUnmarshal("BNR/content2.xml", JAXBContent.class);
        shouldUnmarshal("BNR/contentModel_10003.xml", ContentModel.class);
        shouldUnmarshal("BNR/contentModel_10023.xml", ContentModel.class);
        shouldUnmarshal("BNR/image1.xml", JAXBResource.class);
    }

    @Test
    void shouldUnmarshalNWS() throws Exception {
        shouldUnmarshal("NWS/NWS.xml", JAXBContentType.class);
        shouldUnmarshal("NWS/content1.xml", JAXBContent.class);
        shouldUnmarshal("NWS/content2.xml", JAXBContent.class);
        shouldUnmarshal("NWS/contentModel_10002.xml", ContentModel.class);
        shouldUnmarshal("NWS/contentModel_10020.xml", ContentModel.class);
        shouldUnmarshal("NWS/contentModel_10021.xml", ContentModel.class);
        shouldUnmarshal("NWS/contentModel_10022.xml", ContentModel.class);
        shouldUnmarshal("NWS/image1.xml", JAXBResource.class);
        shouldUnmarshal("NWS/image2.xml", JAXBResource.class);
    }

    @Test
    void shouldUnmarshalTCL() throws Exception {
        shouldUnmarshal("TCL/TCL.xml", JAXBContentType.class);
        shouldUnmarshal("TCL/content1.xml", JAXBContent.class);
        shouldUnmarshal("TCL/contentModel_10004.xml", ContentModel.class);
        shouldUnmarshal("TCL/contentModel_10024.xml", ContentModel.class);
    }

    private <T> void shouldUnmarshal(String resourcePath, Class<T> type) throws Exception {
        try (InputStream is = SelfRestCallerPayloadsUnmarshallTest.class.getClassLoader()
                .getResourceAsStream("component/defaultResources/postprocess/" + resourcePath)) {
            Assertions.assertDoesNotThrow(() -> xmlMapper.readValue(is, type));
        }
    }
}
