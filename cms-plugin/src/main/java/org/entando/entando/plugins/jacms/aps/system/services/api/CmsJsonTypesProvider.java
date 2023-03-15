package org.entando.entando.plugins.jacms.aps.system.services.api;

import com.agiletec.plugins.jacms.aps.system.services.content.model.attribute.JAXBLinkAttribute;
import com.agiletec.plugins.jacms.aps.system.services.content.model.attribute.JAXBResourceAttribute;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.entando.entando.aps.system.services.api.JsonTypesProvider;
import org.springframework.stereotype.Component;

@Component
public class CmsJsonTypesProvider implements JsonTypesProvider {

    @Override
    public NamedType[] getJsonSubtypes() {
        return new NamedType[]{
                new NamedType(JAXBLinkAttribute.class),
                new NamedType(JAXBResourceAttribute.class)
        };
    }
}
