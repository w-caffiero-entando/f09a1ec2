package org.entando.entando.aps.system.services.api;

import com.agiletec.aps.system.common.entity.model.attribute.JAXBBooleanAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.JAXBCompositeAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.JAXBDateAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.JAXBHypertextAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.JAXBListAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.JAXBNumberAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.JAXBTextAttribute;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.entando.entando.aps.system.common.entity.model.attribute.JAXBEnumeratorMapAttribute;
import org.springframework.stereotype.Component;

@Component
public class DefaultJsonTypesProvider implements JsonTypesProvider {

    @Override
    public NamedType[] getJsonSubtypes() {
        return new NamedType[]{
                new NamedType(JAXBBooleanAttribute.class),
                new NamedType(JAXBCompositeAttribute.class),
                new NamedType(JAXBDateAttribute.class),
                new NamedType(JAXBEnumeratorMapAttribute.class),
                new NamedType(JAXBHypertextAttribute.class),
                new NamedType(JAXBListAttribute.class),
                new NamedType(JAXBNumberAttribute.class),
                new NamedType(JAXBTextAttribute.class)
        };
    }
}
