package org.entando.entando.plugins.jpcds.aps.system.storage;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class CdsCreateRowResponseDtoTest {


    @Test
    public void shouldMarshallingAndUnmarshallingUseTheCorrectFieldsName()
            throws JsonParseException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        CdsCreateRowResponseDto dtoObject = new CdsCreateRowResponseDto();
        dtoObject.setProtectedFile(true);
        dtoObject.setStatus("OK");
        dtoObject.setFilename("nome_file");
        dtoObject.setPath("/mypath");

        String dtoAsString = mapper.writeValueAsString(dtoObject);

        Assertions.assertThat(dtoAsString).contains("\"status\":\"OK\"");
        Assertions.assertThat(dtoAsString).contains("\"is_protected_file\":true");
        Assertions.assertThat(dtoAsString).contains("\"filename\":\"nome_file\"");
        Assertions.assertThat(dtoAsString).contains("\"path\":\"/mypath\"");
        Assertions.assertThat(dtoAsString).contains("\"date\":null");

        String test = "{\"status\":\"OK\",\"filename\":\"nome_file\",\"date\":1234,\"path\":\"/her_path\",\"is_protected_file\":false}";
        CdsCreateRowResponseDto exp = mapper.readValue(test, CdsCreateRowResponseDto.class);
        Assertions.assertThat(exp.getStatus()).isEqualTo("OK");
        Assertions.assertThat(exp.getFilename()).isEqualTo("nome_file");
        Assertions.assertThat(exp.getPath()).isEqualTo("/her_path");
        Assertions.assertThat(exp.getDate()).isEqualTo(1234);
        Assertions.assertThat(exp.isProtectedFile()).isFalse();
    }
}
