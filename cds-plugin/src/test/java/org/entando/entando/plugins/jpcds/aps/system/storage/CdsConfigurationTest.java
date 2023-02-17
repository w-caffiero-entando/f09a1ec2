package org.entando.entando.plugins.jpcds.aps.system.storage;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class CdsConfigurationTest {

    @Test
    void shouldDtoWork(){
        CdsConfiguration config = new CdsConfiguration();
        config.setCdsPath("/path");

        Assertions.assertThat(config.getCdsPath()).isEqualTo("/path");
    }
}
