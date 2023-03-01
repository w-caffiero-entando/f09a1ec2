package org.entando.entando.aps.system.services.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApiObjectMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private List<JsonTypesProvider> jsonTypesProviders;

    @PostConstruct
    public void init() {
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        for (JsonTypesProvider jsonTypesProvider : jsonTypesProviders) {
            OBJECT_MAPPER.registerSubtypes(jsonTypesProvider.getJsonSubtypes());
        }
    }

    public Object readValue(InputStream src, Class<?> valueType) throws IOException {
        return OBJECT_MAPPER.readValue(src, valueType);
    }
}
