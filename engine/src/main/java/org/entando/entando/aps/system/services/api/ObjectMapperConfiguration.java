package org.entando.entando.aps.system.services.api;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.entando.entando.aps.system.services.api.model.ListResponse;
import org.entando.entando.aps.system.services.api.model.ListResponseSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ObjectMapperConfiguration {

    @Bean("DefaultObjectMapper")
    @Primary
    public ObjectMapper defaultObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        configureDefaultSettings(objectMapper);
        return objectMapper;
    }

    @Bean
    public XmlMapper xmlMapper() {
        XmlMapper xmlMapper = new XmlMapper();

        SimpleModule simpleModule = new SimpleModule("CustomXmlSerializerModule", Version.unknownVersion());
        simpleModule.addSerializer(ListResponse.class, new ListResponseSerializer());
        xmlMapper.registerModule(simpleModule);

        configureDefaultSettings(xmlMapper);
        return xmlMapper;
    }

    private void configureDefaultSettings(ObjectMapper mapper) {
        mapper.registerModule(new JaxbAnnotationModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}
