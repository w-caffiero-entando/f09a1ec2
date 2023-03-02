package org.entando.entando.aps.system.services.api.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

public class ListResponseSerializer<T> extends StdSerializer<ListResponse<T>> {

    public ListResponseSerializer() {
        this(null);
    }

    public ListResponseSerializer(Class<ListResponse<T>> t) {
        super(t);
    }

    @Override
    public void serialize(ListResponse<T> listResponse, JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        if (listResponse.getSize() != null) {
            jsonGenerator.writeFieldName("size");
            jsonGenerator.writeNumber(listResponse.getSize());
        }
        if (listResponse.getEntity() != null) {
            for (T value : listResponse.getEntity()) {
                jsonGenerator.writeFieldName("item");
                jsonGenerator.writeObject(value);
            }
        }
        jsonGenerator.writeEndObject();
    }

}
