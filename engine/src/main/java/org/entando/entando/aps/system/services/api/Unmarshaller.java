package org.entando.entando.aps.system.services.api;

import com.agiletec.aps.system.SystemConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component(SystemConstants.UNMARSHALLER)
public class Unmarshaller {

    private final ObjectMapper jsonObjectMapper;
    private final XmlMapper xmlMapper;

    @Autowired
    public Unmarshaller(ObjectMapper jsonObjectMapper, XmlMapper xmlMapper) {
        this.jsonObjectMapper = jsonObjectMapper;
        this.xmlMapper = xmlMapper;
    }

    public <T> T unmarshal(MediaType mediaType, InputStream inputStream, Class<T> expectedType) throws IOException {
        return getMapper(mediaType).readValue(inputStream, expectedType);
    }

    public <T> T unmarshal(MediaType mediaType, String content, Class<T> expectedType) throws IOException {
        return getMapper(mediaType).readValue(content, expectedType);
    }

    private ObjectMapper getMapper(MediaType mediaType) {
        if (mediaType.equals(MediaType.APPLICATION_XML)) {
            return xmlMapper;
        }
        return jsonObjectMapper;
    }
}
