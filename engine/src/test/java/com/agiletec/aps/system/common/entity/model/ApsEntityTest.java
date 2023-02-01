package com.agiletec.aps.system.common.entity.model;

import com.agiletec.aps.system.common.entity.model.attribute.CompositeAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.TextAttribute;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ApsEntityTest {
    private static final String COMPOSITE_ATTRIBUTE_NAME = "my_composite_attribute";

    @Test
    void shouldAddOrReplaceAttributeAddAttribute() {
        ApsEntity entity = new ApsEntity();
        entity.addOrReplaceAttribute(buildCompositeAttribute());

        Assertions.assertThat(entity.getAttributeList()).hasSize(1);
        Assertions.assertThat(entity.getAttributeMap()).containsOnlyKeys(COMPOSITE_ATTRIBUTE_NAME);

    }

    @Test
    void shouldAddOrReplaceAttributeReplaceAttribute() {
        ApsEntity entity = new ApsEntity();
        CompositeAttribute compositeAttribute = buildCompositeAttribute();
        entity.addAttribute(compositeAttribute);

        entity.addOrReplaceAttribute(buildCompositeAttribute());

        Assertions.assertThat(entity.getAttributeList()).hasSize(1);
        Assertions.assertThat(entity.getAttributeMap()).containsOnlyKeys(COMPOSITE_ATTRIBUTE_NAME);
    }

    private CompositeAttribute buildCompositeAttribute(){
        CompositeAttribute compositeAttribute = new CompositeAttribute();
        compositeAttribute.setName(COMPOSITE_ATTRIBUTE_NAME);

        TextAttribute attribute = new TextAttribute();
        attribute.setName("attributeName1");
        return compositeAttribute;
    }
}
