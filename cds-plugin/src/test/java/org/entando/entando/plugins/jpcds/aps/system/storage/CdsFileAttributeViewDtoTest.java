package org.entando.entando.plugins.jpcds.aps.system.storage;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.ToString;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class CdsFileAttributeViewDtoTest {


    @Test
    public void shouldMarshallingAndUnmarshallingUseTheCorrectFieldsName()
            throws JsonParseException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        CdsFileAttributeViewDto dtoObject = new CdsFileAttributeViewDto();
        dtoObject.setDirectory(false);
        dtoObject.setSize(1024L);
        dtoObject.setProtectedFolder(true);
        dtoObject.setName("myname");
        dtoObject.setPath("/mypath");

        String dtoAsString = mapper.writeValueAsString(dtoObject);

        Assertions.assertThat(dtoAsString).contains("\"directory\":false");
        Assertions.assertThat(dtoAsString).contains("\"protected_folder\":true");
        Assertions.assertThat(dtoAsString).contains("\"size\":1024");
        Assertions.assertThat(dtoAsString).contains("\"path\":\"/mypath\"");
        Assertions.assertThat(dtoAsString).contains("\"name\":\"myname\"");

        String test = "{\"size\":1000,\"directory\":true,\"path\":\"/her_path\",\"name\":\"hername\",\"protected_folder\":false}";
        CdsFileAttributeViewDto exp = mapper.readValue(test, CdsFileAttributeViewDto.class);
        Assertions.assertThat(exp.getSize()).isEqualTo(1000);
        Assertions.assertThat(exp.getDirectory()).isTrue();
        Assertions.assertThat(exp.getPath()).isEqualTo("/her_path");
        Assertions.assertThat(exp.getName()).isEqualTo("hername");
        Assertions.assertThat(exp.getDate()).isNull();
        Assertions.assertThat(exp.getProtectedFolder()).isFalse();



        Map<String,String> m = new HashMap<>();
        m.put("secs_since_epoch","1676592000");
        dtoObject.setLastModifiedTime(m);
        dtoAsString = mapper.writeValueAsString(dtoObject);
        Assertions.assertThat(dtoAsString).contains("\"last_modified_time\":{\"secs_since_epoch\":\"1676592000\"}");

        test = "{\"size\":1000,\"directory\":true,\"last_modified_time\":{\"test\":true,\"secs_since_epoch\":\"1676592000\"},\"path\":\"/her_path\",\"name\":\"hername\",\"protected_folder\":false}";
        exp = mapper.readValue(test, CdsFileAttributeViewDto.class);
        Assertions.assertThat(exp.getDate()).isNotNull();
        Assertions.assertThat(exp.getLastModifiedTime().get("secs_since_epoch")).isEqualTo("1676592000");

    }

    @Test
    public void shouldWorkFineWithHashEqualsAndToString() {

        CdsFileAttributeViewDto dtoObject1 = new CdsFileAttributeViewDto();
        dtoObject1.setDirectory(false);
        dtoObject1.setSize(1024L);
        dtoObject1.setProtectedFolder(true);
        dtoObject1.setName("myname");
        dtoObject1.setPath("/mypath");

        CdsFileAttributeViewDto dtoObject2 = new CdsFileAttributeViewDto();
        dtoObject2.setDirectory(false);
        dtoObject2.setSize(1024L);
        dtoObject2.setProtectedFolder(true);
        dtoObject2.setName("myname");
        dtoObject2.setPath("/mypath");

        Assertions.assertThat(dtoObject2.toString()).contains("myname","/mypath","1024");
        Assertions.assertThat(dtoObject2.hashCode()).isEqualTo(dtoObject1.hashCode());
        Assertions.assertThat(dtoObject2.equals(dtoObject1)).isTrue();
    }

}
