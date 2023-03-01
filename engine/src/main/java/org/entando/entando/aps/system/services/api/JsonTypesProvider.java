package org.entando.entando.aps.system.services.api;

import com.fasterxml.jackson.databind.jsontype.NamedType;

public interface JsonTypesProvider {

    NamedType[] getJsonSubtypes();

}
