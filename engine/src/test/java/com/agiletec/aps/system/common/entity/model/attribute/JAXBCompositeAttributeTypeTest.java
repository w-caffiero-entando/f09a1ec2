package com.agiletec.aps.system.common.entity.model.attribute;

import com.agiletec.aps.system.common.entity.parse.attribute.CompositeAttributeHandler;
import com.agiletec.aps.util.ApsProperties;
import java.util.HashMap;
import java.util.Map;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JAXBCompositeAttributeTypeTest {

    @Test
    void shouldCreateCompositeAttribute() throws Exception {
        JAXBCompositeAttributeType compositeAttributeType = new JAXBCompositeAttributeType();
        compositeAttributeType.setType("Composite");
        compositeAttributeType.setName("composite");
        compositeAttributeType.setSearchable(true);
        compositeAttributeType.setIndexable(true);
        Map<String, AttributeInterface> attributesMap = new HashMap<>();
        attributesMap.put("Composite", getCompositeAttribute());
        AttributeInterface attribute = compositeAttributeType.createAttribute(attributesMap);
        Assertions.assertEquals(CompositeAttribute.class, attribute.getClass());
    }

    @Test
    void shouldFailCreatingCompositeAttributeWithoutName() {
        JAXBCompositeAttributeType compositeAttributeType = new JAXBCompositeAttributeType();
        compositeAttributeType.setType("Composite");
        Map<String, AttributeInterface> attributesMap = new HashMap<>();
        attributesMap.put("Composite", getCompositeAttribute());
        Assertions.assertThrows(ApiException.class, () ->
                compositeAttributeType.createAttribute(attributesMap));
    }

    @Test
    void shouldFailCreatingCompositeAttributeWithInvalidName() {
        JAXBCompositeAttributeType compositeAttributeType = new JAXBCompositeAttributeType();
        compositeAttributeType.setType("Composite");
        compositeAttributeType.setName("Invalid name");
        Map<String, AttributeInterface> attributesMap = new HashMap<>();
        attributesMap.put("Composite", getCompositeAttribute());
        Assertions.assertThrows(ApiException.class, () ->
                compositeAttributeType.createAttribute(attributesMap));
    }

    private CompositeAttribute getCompositeAttribute() {
        CompositeAttribute compositeAttribute = new CompositeAttribute();
        compositeAttribute.setType("Composite");
        compositeAttribute.setName("composite");
        compositeAttribute.setNames(getAttributeNames("composite"));
        compositeAttribute.setHandler(new CompositeAttributeHandler());
        return compositeAttribute;
    }

    private ApsProperties getAttributeNames(String enName) {
        ApsProperties properties = new ApsProperties();
        properties.setProperty("en", enName);
        return properties;
    }
}
