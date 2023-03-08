package org.entando.entando.aps.system.services.widgettype.api;

import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
class ApiWidgetTypeInterfaceTest {

    @InjectMocks
    private ApiWidgetTypeInterface apiWidgetTypeInterface;

    @Test
    void shouldGetXmlApiResourceUrl() {
        WidgetType widgetType = new WidgetType();
        widgetType.setCode("type1");
        String resourceUrl = apiWidgetTypeInterface.getApiResourceUrl(
                widgetType, "http://localhost:8080/", "en", MediaType.APPLICATION_XML);
        Assertions.assertEquals("http://localhost:8080/api/legacy/en/core/widgetType.xml?code=type1", resourceUrl);
    }

    @Test
    void shouldGetJsonApiResourceUrl() {
        WidgetType widgetType = new WidgetType();
        widgetType.setCode("type1");
        String resourceUrl = apiWidgetTypeInterface.getApiResourceUrl(
                widgetType, "http://localhost:8080/", "en", MediaType.APPLICATION_JSON);
        Assertions.assertEquals("http://localhost:8080/api/legacy/en/core/widgetType.json?code=type1", resourceUrl);
    }
}
